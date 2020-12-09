package cn.yoyo.base.web.Internal;

import cn.yoyo.base.web.req.BatchExecuteRequest;
import cn.yoyo.base.web.resp.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 内部接口
 */
@Slf4j
@RestController
@RequestMapping("internal")
public class InternalAPIController {
    @Autowired
    private BatchExecuteService batchExecuteService;
    @Autowired
    private ReconcileTask reconcileTask;


    @RequestMapping(value = "batchexecute", method = RequestMethod.POST)
    public CommonResult batchexecute(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API batchexecute start:{} ===========", request);
        if (!batchExecuteService.execute(request.getBatchCode(),request.getBatchType())){
            return new CommonResult(ErrorCode.TRADE_ERROR.getCode(),"执行失败");
        }
        log.info("========== API batchexecute finished ===========");
        return new CommonResult();
    }

}

