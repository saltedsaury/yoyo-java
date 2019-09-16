package cn.idachain.finance.batch.service.service.impl;

import cn.idachain.finance.batch.common.enums.Channel;
import cn.idachain.finance.batch.common.enums.Direction;
import cn.idachain.finance.batch.common.enums.TransferOrderStatus;
import cn.idachain.finance.batch.common.enums.TransferProcessStatus;
import cn.idachain.finance.batch.service.service.ITransferOrderService;
import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import cn.idachain.finance.batch.service.dao.ITransferOrderDao;
import cn.idachain.finance.batch.service.external.CexRespCode;
import cn.idachain.finance.batch.service.external.CexResponse;
import cn.idachain.finance.batch.service.external.ExternalInterface;
import cn.idachain.finance.batch.service.external.model.BatchTransferParam;
import cn.idachain.finance.batch.service.external.model.LoanParam;
import cn.idachain.finance.batch.service.external.model.TransferInfoData;
import cn.idachain.finance.batch.service.external.model.TransferParam;
import cn.idachain.finance.batch.service.util.GenerateIdUtil;
import com.baomidou.mybatisplus.plugins.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private void updateOrderByTransaction(final TransferOrder order, final String orderStatus,
                                          final String processStatus){
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                    transferOrderDao.updateStatusByObj(order,orderStatus);
                    transferOrderDao.updateProcessStatusByObj(order,processStatus);
                }catch (Exception e){
                    status.setRollbackOnly();
                    log.error("transfer order status update db error,{}",e);
                    throw new BizException(BizExceptionEnum.DB_ERROR);
                }
            }
        });
    }
    /**
     * 扣减余额
     * @param deriction
     * @param amount
     * @param ccy
     */
    private boolean deduct(String deriction,BigDecimal amount,
                           String ccy,String orderNo,String customerNo) {
        //整体资金方向为转入，应从up扣减余额
        if(Direction.IN.getCode().equals(deriction)){
            CexResponse response = null;
            TransferParam param =  TransferParam.builder()
                    .amount(amount)
                    .currency(ccy)
                    .outOrderNo(orderNo)
                    .userNo(customerNo)
                    .build();
            log.info("transfer out from uptop, param:{}",param.toString());
            response = externalInterface.transferOut(param);
            if (CexRespCode.SUCCESS.getCode().equals(response.getCode())){
                log.info("transfer out from uptop success, response :{}",response);
                return true;
            }
            log.error("call up transferOut error,code:{},msg:{},traceId:{}",
                    response.getCode(),response.getMsg(),response.getTraceId());
            return false;
        }
        //整体资金方向为转出，应从account扣减余额
        else if(Direction.OUT.getCode().equals(deriction)){
            log.info("transfer out from financing, customerNo:{},currency:{},orderNo:{},amount:{}"
                    ,customerNo,ccy,orderNo,amount);
            return balanceDetailService.transfer(customerNo,ccy,Direction.OUT.getCode(),orderNo,amount);
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
    private boolean increase(String deriction,BigDecimal amount,String ccy,String orderNo,String customerNo){
        //整体资金方向为转入，应从增加account余额
        if(Direction.IN.getCode().equals(deriction)){
            log.info("transfer in to financing, customerNo:{},currency:{},orderNo:{},amount:{}"
                    ,customerNo,ccy,orderNo,amount);
            return balanceDetailService.transfer(customerNo,ccy,Direction.IN.getCode(),orderNo,amount);
        }
        //整体资金方向为转出，应从增加up余额
        else if(Direction.OUT.getCode().equals(deriction)){
            CexResponse response = null;
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
                return true;
            }
            log.error("call up transferIn error,code:{},msg:{},traceId:{}",
                    response.getCode(),response.getMsg(),response.getTraceId());
            return false;
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
        boolean transferInFlag;
        boolean transferOutFlag;
        List<TransferOrder> orders = transferOrderDao.getTransferOrderByStatus(
                TransferOrderStatus.PROCESSING.getCode(),new Page(0,10));
        for (TransferOrder order : orders) {
            transferInFlag = false;
            transferOutFlag = false;
            try{
                if (TransferProcessStatus.CHARGEBACK_SUCCESS.getCode().equals(order.getProcessStatus())) {
                    transferInFlag= increase(order.getDeriction(), order.getAmount(), order.getCcy(),
                            order.getOrderNo(), order.getCustomerNo());
                }
                if (transferInFlag){
                    //更新订单状态  增加余额成功
                    updateOrderByTransaction(order,TransferOrderStatus.SUCCESS.getCode(),
                            TransferProcessStatus.SUCCESS.getCode());
                }else{
                    //更新订单状态  增加余额失败
                    //此状态用于标识需要报警的单子
                    updateOrderByTransaction(order,TransferOrderStatus.PROCESSING.getCode(),
                            TransferProcessStatus.TRANSFERED_FAILED.getCode());
                }
            }catch (Exception e){
                log.error("transferOrder {} confirm error with exception:{}",e.getMessage());
                e.printStackTrace();
            }
        }
        List<TransferOrder> initList = transferOrderDao.getTransferOrderByStatus(
                TransferOrderStatus.INIT.getCode(),new Page(0,10));
        for (TransferOrder order : orders) {
            transferInFlag = false;
            transferOutFlag = false;
            transferOutFlag = deduct(order.getDeriction(), order.getAmount(), order.getCcy(),
                    order.getOrderNo(), order.getCustomerNo());
            if (transferOutFlag) {
                //更新订单状态  扣款成功
                updateOrderByTransaction(order, TransferOrderStatus.PROCESSING.getCode(),
                        TransferProcessStatus.CHARGEBACK_SUCCESS.getCode());
                transferInFlag = increase(order.getDeriction(), order.getAmount(), order.getCcy(),
                        order.getOrderNo(), order.getCustomerNo());
                if (transferInFlag){
                    //更新订单状态  增加余额成功
                    updateOrderByTransaction(order,TransferOrderStatus.SUCCESS.getCode(),
                            TransferProcessStatus.SUCCESS.getCode());
                }
            }
        }
    }
}
