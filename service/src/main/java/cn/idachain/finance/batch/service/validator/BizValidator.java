package cn.idachain.finance.batch.service.validator;

import cn.idachain.finance.batch.common.exception.BizException;
import cn.idachain.finance.batch.common.exception.BizExceptionEnum;
import cn.idachain.finance.batch.service.external.CexRespCode;
import cn.idachain.finance.batch.service.external.CexResponse;
import cn.idachain.finance.batch.service.external.ExternalInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizValidator {
    @Autowired
    private ExternalInterface externalInterface;

    public void validate(String payPassword) {
        CexResponse response = externalInterface.validatePaypassword(payPassword);
        String code = response.getCode();
        if (!CexRespCode.isSuccess(code)) {
            log.info("校验资金密码失败 : resp:{}", response);
            BizExceptionEnum.SECURITY_PWD_ERROR.setMessage(response.getMsg());
            throw new BizException(BizExceptionEnum.SECURITY_PWD_ERROR);
        }
    }
}
