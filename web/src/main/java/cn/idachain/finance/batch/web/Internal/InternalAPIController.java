package cn.idachain.finance.batch.web.Internal;

import cn.idachain.finance.batch.common.enums.ErrorCode;
import cn.idachain.finance.batch.service.service.impl.BatchExecuteService;
import cn.idachain.finance.batch.task.task.ReconcileTask;
import cn.idachain.finance.batch.web.req.BatchExecuteRequest;
import cn.idachain.finance.batch.web.resp.CommonResult;
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

    @RequestMapping(value = "taskexecute", method = RequestMethod.POST)
    public CommonResult taskexecute(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API taskexecute request:{} ===========",request);
        if (!batchExecuteService.taskexecute(request.getTaskName())){
            return new CommonResult(ErrorCode.TRADE_ERROR.getCode(),"执行失败");
        }
        log.info("========== API taskexecute finish ===========");
        return new CommonResult();
    }

    @PostMapping("/reconcile")
    public CommonResult reconcile() {
        log.info("========= API reconcile start manually =========");
        reconcileTask.reconcile();
        log.info("========= API reconcile completed ==========");
        return new CommonResult();
    }
}

