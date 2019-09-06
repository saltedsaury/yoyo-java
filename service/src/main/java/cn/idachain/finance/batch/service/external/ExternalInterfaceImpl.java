package cn.idachain.finance.batch.service.external;

import cn.idachain.finance.batch.common.constants.CexConstant;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.service.external.model.*;
import cn.idachain.finance.batch.service.util.ThreadLocalUtil;
import cn.idachain.finance.batch.common.constants.CexConstant;
import cn.idachain.finance.batch.common.enums.MonitorTarget;
import cn.idachain.finance.batch.common.util.MonitorLogUtils;
import cn.idachain.finance.batch.common.util.SignUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.testng.collections.Maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static cn.idachain.finance.batch.common.constants.CexConstant.HEADER_PARAM_DEVICEID_VALUE;
import static cn.idachain.finance.batch.common.constants.CexConstant.HEADER_PARAM_DEVICESOURCE_VALUE;


/**
 * Created by liuhailin on 2019/1/30.
 */
@Slf4j
@Component
public class ExternalInterfaceImpl implements ExternalInterface {

    @Value("${vote.cex.interface.privatekey}")
    protected String privateKey;

    @Autowired
    private CexRestTemplate cexRestTemplate;

    @Override
    public CexResponse getUserInfo(CexRequest req) {
        log.info("getUserInfo.start");
        return ensureNotNull(cexRestTemplate.post(CexConstant.USER_INFO_URL, CexResponse.class, req));
    }

    @Override
    public CexResponse getUserInfoByToken(CexRequest req) {
        log.info("getUserInfo.start");
        return ensureNotNull(cexRestTemplate.post(CexConstant.USER_INFO_URL_BY_SELF, CexResponse.class, req));
    }

    @Override
    public CexResponse validatePaypassword(String securityPwd) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(CexConstant.PARAM_DATA_SECURITYPWD, securityPwd);
        return ensureNotNull(execute(CexConstant.CHANNEL_PASSWORD_CHECK_SECURITYPWD, data, 0));
    }

    @Override
    public CexResponse uploadImage(MultipartFile file) {
        return null;
    }

    private String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));
        String body = "";
        StringBuilder content = new StringBuilder();
        while ((body = rd.readLine()) != null) {
            content.append(body).append("\n");
        }
        return content.toString().trim();
    }

    @Override
    public ResponseEntity<byte[]> readImage(String fileId) {
        CexRequest req = createRequest();

        String url = CexConstant.USER_FILE_READ_FILE + "?fileId=" + fileId;
        return cexRestTemplate.exchange(url, byte[].class, req);
    }

    @Override
    public boolean asyncSendMessage(MessageSendParam param) {
        return false;
    }

    @Override
    public CexResponse transferOut(TransferParam param) {
        return transferOut(param, 0);
    }

    @Override
    public CexResponse transferOut(TransferParam param, int retryCount) {
        return ensureNotNull(execute(CexConstant.TRANSFER_OUT, param, retryCount));
    }

    @Override
    public CexResponse transferIn(TransferParam param) {
        return transferIn(param, 0);
    }

    @Override
    public CexResponse transferIn(TransferParam param, int retryCount) {
        return ensureNotNull(execute(CexConstant.TRANSFER_IN, param, retryCount));
    }

    private CexResponse ensureNotNull(CexResponse response) {
        if (response == null) {
            return CexResponse.builder()
                    .code(CexRespCode.INVOKE_EXCEPTION.getCode())
                    .msg(CexRespCode.INVOKE_EXCEPTION.getDesc())
                    .build();
        }
        return response;
    }

    private CexRequest createRequest() {
        CexRequest req = CexRequest.builder()
                .cexPassport(ThreadLocalUtil.getCexPassport())
                .deviceId(ThreadLocalUtil.getDeviceId())
                .deviceSource(ThreadLocalUtil.getSource())
                .build();
        return req;
    }

    @Override
    public CexResponse currencyAsset(String currency) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(CexConstant.PARAM_DATA_CURRENCY, currency);
        return execute(CexConstant.USER_ASSET_QUERY_CRRENCY_ASSET, data, 0);
    }

    @Override
    public CexResponse selectTransferInList(List<Long> outOrderNos) {
        Map<String, List> map = Maps.newHashMap();
        map.put("outOrderNos", outOrderNos);
        return execute(CexConstant.SELECT_TRANSFER_LIST, map, 0);
    }

    @Override
    public CexResponse queryDealList(String symbol) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(CexConstant.PARAM_DATA_SYMBOL, symbol);
        CexRequest req = createRequest();
        req.setData(data);
        req.setDeviceId(CexConstant.VALUE_DEVICEID);
        req.setDeviceSource(CexConstant.VALUE_DEVICESOURCE);
        return cexRestTemplate.post(CexConstant.QUERY_DEAL_LIST, CexResponse.class, req);
    }

    /**
     * 冻结
     *
     * @param param
     * @return
     */
    @Override
    public CexResponse assetFreeze(TransferParam param) {
        return ensureNotNull(execute(CexConstant.ASSET_FREEZE, param, 3));
    }

    /**
     * 解冻
     *
     * @param param
     * @return
     */
    @Override
    public CexResponse assetUnfreeze(UnfreezeParam param) {
        return ensureNotNull(execute(CexConstant.ASSET_UNFREEZE, param, 0));
    }

    /**
     * 解冻并渠道划出
     *
     * @param param
     * @return
     */
    @Override
    public CexResponse unFreezeTransferOut(UnfreezeParam param) {
        return ensureNotNull(execute(CexConstant.UNFREEZE_TRANSFER_OUT, param, 0));
    }

    /**
     * 渠道给用户加钱
     *
     * @return
     */
    @Override
    public CexResponse batchTransferIn(BatchTransferParam param) {
        return ensureNotNull(execute(CexConstant.BATCH_TRANSFER_IN, param, 0));
    }

    /**
     * 渠道短款
     *
     * @param param
     * @return
     */
    @Override
    public CexResponse loan(LoanParam param) {
        return ensureNotNull(execute(CexConstant.LOAN, param, 0));
    }

    /**
     * 渠道资金池余额
     *
     * @return
     */
    @Override
    public CexResponse fundsLeft() {
        CexRequest cexRequest = CexRequest.builder()
                .deviceId(HEADER_PARAM_DEVICEID_VALUE)
                .deviceSource(HEADER_PARAM_DEVICESOURCE_VALUE)
                .sign("sign")
                .build();
        return ensureNotNull(cexRestTemplate.exchange(CexConstant.FUNDS_LEFT, CexResponse.class, cexRequest).getBody());
    }

    /**
     * 渠道短款账户余额
     *
     * @return
     */
    @Override
    public CexResponse loanLeft() {
        CexRequest cexRequest = CexRequest.builder()
                .deviceId(HEADER_PARAM_DEVICEID_VALUE)
                .deviceSource(HEADER_PARAM_DEVICESOURCE_VALUE)
                .sign("sign")
                .build();
        return ensureNotNull(cexRestTemplate.exchange(CexConstant.LOAN_LEFT, CexResponse.class, cexRequest).getBody());
    }


    private CexResponse execute(String url, Object param, int retryCount) {
        log.debug("url:{},param:{},retryCount:{}", url, param, retryCount);
        try {
            retryCount = retryCount <= 0 ? 1 : retryCount + 1;
            String requestParam = JSONObject.toJSONString(param);
            Map<String, Object> data = Maps.newHashMap();
            data.put(CexConstant.PARAM_DATA_REQUESTPARAM, requestParam);

            String sign = SignUtil.sign(requestParam, privateKey);

            CexRequest req = CexRequest.builder()
                    .deviceId(HEADER_PARAM_DEVICEID_VALUE)
                    .deviceSource(HEADER_PARAM_DEVICESOURCE_VALUE)
                    .sign(sign)
                    .data(data)
                    .build();
            if (!BlankUtil.isBlank(getCexPassport())) {
                req.setCexPassport(getCexPassport());
            }

            if (param.getClass().isAssignableFrom(TransferParam.class)) {
                req.setOutOrderNo(((TransferParam) param).getOutOrderNo());
            }
            if (param.getClass().isAssignableFrom(LoanParam.class)) {
                req.setOutOrderNo(((LoanParam) param).getOutOrderNo());
            }
            if (param.getClass().isAssignableFrom(UnfreezeParam.class)) {
                req.setOutOrderNo(((UnfreezeParam) param).getOutOrderNo());
            }
            if (param.getClass().isAssignableFrom(BatchTransferParam.class)) {
                req.setOutOrderNo(((BatchTransferParam) param).getBatchNo());
            }
            CexResponse response = null;
            for (int i = 0; i < retryCount; i++) {
                try {
                    response = cexRestTemplate.post(url, CexResponse.class, req);
                    if (!response.getCode().equals(CexRespCode.SUCCESS.getCode())) {
                        log.info("transfer respCode convert cexRespCode:{} c2cRespCode:{}", response.getCode(), CexRespCode.SUCCESS_TRANSFER_FAIL.getCode());
                        response.setCode(CexRespCode.SUCCESS_TRANSFER_FAIL.getCode());
                    }
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    // 最后一次还失败，返回
                    if (i == retryCount - 1) {
                        MonitorLogUtils.logMonitorInfo(MonitorTarget.INVOKE_UP_FAILED.getTargetName(), MonitorLogUtils.TargetInfoType.INT, url + e.getMessage());
                        return ensureNotNull(response);
                    }
                }
            }
            return ensureNotNull(response);
        } catch (Exception e) {
            log.error("transfer error url:{}, param:{}", url, param);
            return ensureNotNull(null);
        }
    }


    private String getCexPassport() {
        return ThreadLocalUtil.getCexPassport();
    }

}
