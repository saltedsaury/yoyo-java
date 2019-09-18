package cn.idachain.finance.batch.service.external;

import cn.idachain.finance.batch.service.external.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 *
 * Created by liuhailin on 2019/1/30.
 */
public interface ExternalInterface {

    /**
     * 获取用户信息
     * @param req
     * @return
     */
    CexResponse getUserInfo(CexRequest req);

    /**
     * 获取用户信息
     *
     * @param req
     * @return
     */
    CexResponse getUserInfoByToken(CexRequest req);

    /**
     * 校验资金密码
     * @param securityPwd
     * @return
     */
    CexResponse validatePaypassword(String securityPwd);

    /**
     * 上传二维码图片
     */
    CexResponse uploadImage(MultipartFile file);

    ResponseEntity<byte[]> readImage(String fileId);

    /**
     * 异步发送消息
     * @param param
     * @return
     */
    boolean asyncSendMessage(MessageSendParam param);
    /**
     * 转出
     * @return
     */
    CexResponse transferOut(TransferParam param);

    CexResponse transferOutWithoutToken(TransferParam param);

    CexResponse transferOut(TransferParam param, int retryCount);

    /**
     * 转入
     * @return
     */
    CexResponse transferIn(TransferParam param);

    CexResponse transferIn(TransferParam param, int retryCount);

    /**
     * 查询用户对应币种余额
     * @return
     */
    CexResponse currencyAsset(String currency);


    /**
     * 查询划转集合
     *
     * @param outOrderNos
     * @return
     */
    CexResponse selectTransferInList(List<Long> outOrderNos);

    /**
     * 获取成交记录
     *
     * @return
     */
    CexResponse queryDealList(String symbol);

    /**
     * 冻结
     *
     * @return
     */
    CexResponse assetFreeze(TransferParam param);

    /**
     * 解冻
     *
     * @return
     */
    CexResponse assetUnfreeze(UnfreezeParam param);

    /**
     * 解冻并渠道划出
     *
     * @return
     */
    CexResponse unFreezeTransferOut(UnfreezeParam param);


    /**
     * 解冻并渠道划出
     *
     * @return
     */
    CexResponse batchTransferIn(BatchTransferParam param);

    /**
     * 渠道短款
     *
     * @return
     */
    CexResponse loan(LoanParam param);


    CexResponse fundsLeft();

    CexResponse loanLeft();


}
