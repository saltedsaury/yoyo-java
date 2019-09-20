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
    public CommonResult batchexecute(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API batchexecute finish ===========",request);
        if (!batchExecuteService.execute(request.getBatchCode(),request.getBatchType())){
            return new CommonResult(ErrorCode.TRADE_ERROR.getCode(),"执行失败");
        }
        log.info("========== API batchexecute response:{} ===========");
        return CommonResult.of(000000,"success");
    }

    @RequestMapping(value = "taskexecute", method = RequestMethod.POST)
    public CommonResult taskexecute(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API taskexecute request:{} ===========",request);
        if (!batchExecuteService.taskexecute(request.getTaskName())){
            return new CommonResult(ErrorCode.TRADE_ERROR.getCode(),"执行失败");
        }
        log.info("========== API taskexecute finish ===========");
        return CommonResult.of(000000,"success");
    }
}

