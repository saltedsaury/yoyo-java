package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
import cn.idachain.finance.batch.common.enums.TransferProcessStatus;
import cn.idachain.finance.batch.common.enums.TransferType;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.exception.TryAgainException;
import cn.idachain.finance.batch.service.dao.IRecBalanceSnapshotDao;
import cn.idachain.finance.batch.service.dao.ITransferOrderDao;
import cn.idachain.finance.batch.service.external.CexRespCode;
import cn.idachain.finance.batch.service.external.CexResponse;
import cn.idachain.finance.batch.service.external.ExternalInterface;
import cn.idachain.finance.batch.service.external.model.BatchTransferParam;
import cn.idachain.finance.batch.service.external.model.LoanParam;
import cn.idachain.finance.batch.service.external.model.TransferInfoData;
import cn.idachain.finance.batch.service.external.model.TransferParam;
import cn.idachain.finance.batch.service.service.ITransferOrderService;
import cn.idachain.finance.batch.service.service.dto.TransferProcessDTO;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TransferOrderService implements ITransferOrderService {

    @Autowired
    private ITransferOrderDao transferOrderDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ExternalInterface externalInterface;
    @Autowired
    private BalanceDetailService balanceDetailService;
    @Autowired
    private IRecBalanceSnapshotDao balanceSnapshotDao;

    @Value("${task.financing.transfer-confirm.count}")
    private Integer confirmCount;

    private void updateOrderByTransaction(final TransferOrder order,
                                          final TransferOrderStatus orderStatus,
                                          final TransferProcessStatus processStatus,
                                          final Long transferTime,
                                          final Long chargeTime) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
            try{
                if (chargeTime != null) {
                    order.setChargeTime(chargeTime);
                }
                if (transferTime != null) {
                    order.setTransferTime(transferTime);
                    transferOrderDao.updateStatus(order, orderStatus.getCode(), processStatus.getCode());
                    balanceSnapshotDao.insertSnapshot(order.getCcy(), order.getAmount(),
                            Direction.getByCode(order.getDeriction()), transferTime);
                } else {
                    transferOrderDao.updateStatus(order, orderStatus.getCode(), processStatus.getCode());
                }
            }catch (Exception e){
                status.setRollbackOnly();
                log.error("transfer order status update db error",e);
                throw new BizException(BizExceptionEnum.DB_ERROR);
            }
            }
        });
    }
    /**
     * 扣减余额
     * @param direction
     * @param amount
     * @param ccy
     */
    private TransferProcessDTO deduct(String direction, BigDecimal amount, String ccy,
                                      String orderNo, String customerNo, String transferType, String accountNo) {
        //整体资金方向为转入，应从up扣减余额
        if(Direction.IN.getCode().equals(direction)){
            TransferParam param =  TransferParam.builder()
                    .amount(amount)
                    .currency(ccy)
                    .outOrderNo(orderNo)
                    .userNo(customerNo)
                    .build();
            log.info("transfer out from uptop, param:{}",param.toString());

            CexResponse response;
            if (TransferType.SYSTEM.getCode().equals(transferType)){
                response = externalInterface.transferOutWithoutToken(param);
            }else {
                response = externalInterface.transferOut(param);
            }
            if (CexRespCode.SUCCESS.getCode().equals(response.getCode())){
                log.info("transfer out from uptop success, response :{}",response);
                return TransferProcessDTO.success()
                        .setDirection(Direction.IN)
                        .setOuterTransferTime(extractTransferTime(response));
            }
            if (CexRespCode.SUCCESS_TRANSFER_FAIL.getCode().equals(response.getCode())){
                log.warn("call up transferOut error,code:{},msg:{},traceId:{}",
                        response.getCode(),response.getMsg(),response.getTraceId());
                BizExceptionEnum.TRANSFER_ERROR.setMessage(response.getMsg());
                throw new BizException(BizExceptionEnum.TRANSFER_ERROR);
            }
            log.error("call up transferOut error,code:{},msg:{},traceId:{}",
                    response.getCode(),response.getMsg(),response.getTraceId());
            return TransferProcessDTO.fail().setDirection(Direction.IN);
        }
        //整体资金方向为转出，应从account扣减余额
        else if(Direction.OUT.getCode().equals(direction)){
            if (TransferType.SYSTEM.getCode().equals(transferType)){
                log.info("transfer out from financing for boss , customerNo:{},currency:{},orderNo:{},amount:{}"
                        ,customerNo,ccy,orderNo,amount);
                return TransferProcessDTO.success()
                        .setDirection(Direction.OUT)
                        .setInnerTransferTime(balanceDetailService
                                .systemTransfer(customerNo,ccy,Direction.OUT.getCode(), orderNo,amount,accountNo));
            }
            log.info("transfer out from financing, customerNo:{},currency:{},orderNo:{},amount:{}"
                    ,customerNo,ccy,orderNo,amount);
            return TransferProcessDTO.success()
                    .setDirection(Direction.OUT)
                    .setInnerTransferTime(balanceDetailService
                            .transfer(customerNo,ccy,Direction.OUT.getCode(),orderNo,amount));
        }
        else{
            throw new BizException(BizExceptionEnum.DERICTION_ERROR);
        }
    }

    /**
     * 增加余额
     * @param deriction
     * @param amount
     * @param ccy
     */
    private TransferProcessDTO increase(String deriction,BigDecimal amount,String ccy,String orderNo,
                             String customerNo,String transferType,String accountNo){
        //整体资金方向为转入，应从增加account余额
        if(Direction.IN.getCode().equals(deriction)){
            if (TransferType.SYSTEM.getCode().equals(transferType)){
                log.info("transfer in to financing for boss, customerNo:{},currency:{},orderNo:{},amount:{}"
                        ,customerNo,ccy,orderNo,amount);
                return TransferProcessDTO.success()
                        .setDirection(Direction.IN)
                        .setInnerTransferTime(balanceDetailService
                                .systemTransfer(customerNo,ccy, Direction.IN.getCode(),orderNo,amount,accountNo));
            }
            log.info("transfer in to financing, customerNo:{},currency:{},orderNo:{},amount:{}"
                    ,customerNo,ccy,orderNo,amount);
            return TransferProcessDTO.success()
                    .setDirection(Direction.IN)
                    .setInnerTransferTime(balanceDetailService
                            .transfer(customerNo,ccy,Direction.IN.getCode(),orderNo,amount));
        }
        //整体资金方向为转出，应从增加up余额
        else if(Direction.OUT.getCode().equals(deriction)){
            CexResponse response;
            //构建转入信息
            List<TransferInfoData> dataList = new ArrayList<TransferInfoData>();
            TransferInfoData data = TransferInfoData.builder()
                    .amount(amount)
                    .outOrderNo(orderNo)
                    .userNo(customerNo)
                    .build();
            dataList.add(data);
            //构建批次信息
            BatchTransferParam param = BatchTransferParam.builder()
                    .batchNo(orderNo)
                    .channelBatchTransferInItemList(dataList)
                    .currency(ccy)
                    .build();
            log.info("transfer in to uptop, param:{}",param.toString());
            response = externalInterface.batchTransferIn(param);
            if (CexRespCode.SUCCESS.getCode().equals(response.getCode())){
                log.info("transfer in to uptop success, response:{}",response.toString());
                return TransferProcessDTO.success()
                        .setDirection(Direction.OUT)
                        .setOuterTransferTime(extractTransferTime(response));
            }
            log.error("call up transferIn error,code:{},msg:{},traceId:{}",
                    response.getCode(),response.getMsg(),response.getTraceId());
            return TransferProcessDTO.fail().setDirection(Direction.OUT);
        }
        throw new BizException(BizExceptionEnum.DERICTION_ERROR);
    }

    private boolean loan(String ccy,BigDecimal amount,String orderNo){
        CexResponse response = null;

        LoanParam param = LoanParam.builder()
                .outOrderNo(orderNo)
                .currency(ccy)
                .amount(amount)
                .build();
        log.info("call loan from uptop, param:{}",param.toString());
        response = externalInterface.loan(param);
        if (CexRespCode.SUCCESS.getCode().equals(response.getCode())){
            log.info("call loan from uptop success, response:{}",response.toString());
            return true;
        }
        log.error("call up loan error,code:{},msg:{},traceId:{}",
                response.getCode(),response.getMsg(),response.getTraceId());
        return false;
    }

    @Override
    public void transferConfirm(){
        TransferProcessDTO increase;
        TransferProcessDTO deduct;
        List<String> process = new ArrayList<>();
        process.add(TransferProcessStatus.CHARGEBACK_SUCCESS.getCode());
        process.add(TransferProcessStatus.TRANSFERED_FAILED.getCode());
        List<TransferOrder> orders = transferOrderDao.getTransferOrderByStatus(
                TransferOrderStatus.PROCESSING.getCode(),process,
                new Page(0,confirmCount));
        for (TransferOrder order : orders) {
            try{
                increase = increase(order.getDeriction(), order.getAmount(), order.getCcy(),
                        order.getOrderNo(), order.getCustomerNo(),order.getTransferType(),order.getAccountNo());
                if (increase.isSuccess()){
                    //更新订单状态  增加余额成功
                    updateOrderByTransaction(order, TransferOrderStatus.SUCCESS,
                            TransferProcessStatus.SUCCESS, increase.getOuterTransferTime(), increase.getInnerTransferTime());
                }else{
                    //更新订单状态  增加余额失败
                    //此状态用于标识需要报警的单子
                    updateOrderByTransaction(order,TransferOrderStatus.PROCESSING,
                            TransferProcessStatus.TRANSFERED_FAILED, null, null);
                    log.error("update transfer order status to transfer failed:{}",order.toString());
                }

            }catch (Exception e){
                log.error("transferOrder {} confirm error with exception:", e.getMessage());
                e.printStackTrace();
            }
        }
        process = new ArrayList<>();
        process.add(TransferProcessStatus.INIT.getCode());
        List<TransferOrder> initList = transferOrderDao.getTransferOrderByStatus(
                TransferOrderStatus.INIT.getCode(), process, new Page(0,confirmCount)
        );
        for (TransferOrder order : initList) {
            try {
                deduct = deduct(order.getDeriction(), order.getAmount(), order.getCcy(),
                        order.getOrderNo(), order.getCustomerNo(), order.getTransferType(),order.getAccountNo());
            }catch (TryAgainException tryAgain){
                log.warn("update account balance failed :{}",order.toString());
                deduct = TransferProcessDTO.fail();
            } catch (BizException e){
                //更新订单状态  扣款失败
                updateOrderByTransaction(order, TransferOrderStatus.PROCESSING,
                        TransferProcessStatus.CHARGEBACK_FAILED, null, null);
                log.info("update transfer order status to chargeback failed:{}",order.toString());
                deduct = TransferProcessDTO.fail();
            }catch (DuplicateKeyException ex){
                deduct = TransferProcessDTO.success();
            }
            if (deduct.isSuccess()) {
                //更新订单状态  扣款成功
                updateOrderByTransaction(order, TransferOrderStatus.PROCESSING,
                        TransferProcessStatus.CHARGEBACK_SUCCESS, deduct.getOuterTransferTime(), deduct.getInnerTransferTime());

                increase = increase(order.getDeriction(), order.getAmount(), order.getCcy(),
                        order.getOrderNo(), order.getCustomerNo(), order.getTransferType(), order.getAccountNo());

                if (increase.isSuccess()){
                    //更新订单状态  增加余额成功
                    updateOrderByTransaction(order,TransferOrderStatus.SUCCESS,
                            TransferProcessStatus.SUCCESS, increase.getOuterTransferTime(), increase.getInnerTransferTime());
                    // 这里修复一条错误的日志
                    log.info("update transfer order status to transfer success:{}",order.toString());
                }
            }
        }
    }

    private Long extractTransferTime(CexResponse response) {
        final String key = "transferTime";
        if (response.getData() == null) {
            return null;
        }
        //noinspection unchecked
        return new JSONObject((Map<String, Object>) response.getData()).getLong(key);
    }
}
