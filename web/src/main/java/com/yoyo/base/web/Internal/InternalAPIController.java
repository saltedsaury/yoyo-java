package com.yoyo.base.web.Internal;

import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.Relationship;
import com.yoyo.base.common.model.VO.ProductDetail;
import com.yoyo.base.common.util.DateUtil;
import com.yoyo.base.service.service.IActivityProductService;
import com.yoyo.base.service.service.IDailyProfitService;
import com.yoyo.base.service.service.IRelationshipService;
import com.yoyo.base.web.req.AddActivityProdRequest;
import com.yoyo.base.web.req.BatchExecuteRequest;
import com.yoyo.base.web.req.GetProductListRequest;
import com.yoyo.base.web.resp.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 内部接口
 */
@Slf4j
@RestController
@RequestMapping("base")
public class InternalAPIController {
    @Autowired
    private IDailyProfitService dailyProfitService;
    @Autowired
    private IRelationshipService relationshipService;
    @Autowired
    private IActivityProductService activityProductService;

    @RequestMapping(value = "getProfit", method = RequestMethod.POST)
    public CommonResult getActivityMapping(@RequestBody BatchExecuteRequest request) throws Exception {
        log.info("========== API getProfit start:{} ===========", request);
        Date current = new Date();
        List<DailyProfit> dailyProfits = dailyProfitService.getDailyProfit(
                request.getChannelId(),current, DateUtil.offsiteDay(current,-2));
        Relationship relationship = new Relationship();
        relationship.setParentId(relationship.getChannelId());
        List<Relationship> relationships = relationshipService.getRelationshipList(relationship);
        log.info("========== API getProfit finished ===========");
        return new CommonResult();
    }

    @RequestMapping(value = "setActivityProducts", method = RequestMethod.POST)
    public CommonResult setActivityProducts(@RequestBody AddActivityProdRequest request) throws Exception {
        log.info("========== API setActivityProducts start:{} ===========", request);
        activityProductService.setActivityProduct(request.getActivityId(),request.getProducts());
        log.info("========== API setActivityProducts finished ===========");
        return new CommonResult();
    }

    @RequestMapping(value = "getProductList", method = RequestMethod.POST)
    public CommonResult getProductList(@RequestBody GetProductListRequest request) throws Exception {
        log.info("========== API getProductList start:{} ===========", request.toString());
        List<ProductDetail> result = activityProductService.getProductList(request.getActivityId());
        log.info("========== API getProductList finished ===========");
        return new CommonResult(result);
    }
}

