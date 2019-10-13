package cn.idachain.finance.batch.web.Internal;

import cn.idachain.finance.batch.common.enums.ErrorCode;
import cn.idachain.finance.batch.service.service.impl.BatchExecuteService;
import cn.idachain.finance.batch.web.req.BatchExecuteRequest;
import cn.idachain.finance.batch.web.resp.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 内部接口
 */
@Slf4j
@RestController
@RequestMapping("internal")
public class InternalAPIController {
    @Autowired
    private BatchExecuteService batchExecuteService;


    @RequestMapping(value = "batchexecute", method = RequestMethod.POST)
    public CommonResult investCompensete(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API batchexecute request:{} ===========",request);
        CommonResult response = new CommonResult();
        if (!batchExecuteService.execute(request.getBatchCode(),request.getBatchType())){
            return new CommonResult(ErrorCode.TRADE_ERROR.getCode(),"执行失败");
        }
        log.info("========== API batchexecute response:{} ===========",response);
        return response;
    }
}

