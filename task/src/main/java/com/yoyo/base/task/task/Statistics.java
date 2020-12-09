package com.yoyo.base.task.task;

import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanItemGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanItemGetResult;
import com.youzan.cloud.open.sdk.gen.v4_0_1.api.YouzanTradesSoldGet;
import com.youzan.cloud.open.sdk.gen.v4_0_1.model.YouzanTradesSoldGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_1.model.YouzanTradesSoldGetResult;
import com.yoyo.base.common.dataobject.DailyProfit;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.Relationship;
import com.yoyo.base.common.dataobject.SystemParam;
import com.yoyo.base.common.util.DateUtil;
import com.yoyo.base.service.service.IDailyProfitService;
import com.yoyo.base.service.service.IProfitDetailService;
import com.yoyo.base.service.service.IRelationshipService;
import com.yoyo.base.service.service.ISystemParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class Statistics {

    @Autowired
    private IProfitDetailService profitDetailService;
    @Autowired
    private DefaultYZClient yzClient;
    @Autowired
    private ISystemParamService systemParamService;
    @Autowired
    private IRelationshipService relationshipService;
    @Autowired
    private IDailyProfitService dailyProfitService;


    /**
     * @throws Exception
     */
    @Scheduled(cron = "${task.statistics}")
    public boolean execute() throws Exception {
        log.info("insurance over due task begin.");
        //任务启动时的时间戳
        Date currentDate = new Date(System.currentTimeMillis());
        //获取历史时间戳
        SystemParam systemParam = systemParamService.getSystemParam("HistoryDate");
        Date historyDate = DateUtil.date(Long.parseLong(systemParam.getValue()));
        //获取有赞云凭证access_token
        TokenParameter tokenParameter = TokenParameter.self()
                .clientId("3beba2d95f888e14d9")
                .clientSecret("55e4b827c35e595b4db8e2cd5cc5022a")
                .grantId("91393221")
                .refresh(true)
                .build();
        OAuthToken oAuthToken = yzClient.getOAuthToken(tokenParameter);
        Token token = new Token(oAuthToken.getAccessToken());

        try {
            //获取订单（24小时内的订单，分页获取）

            YouzanTradesSoldGetParams youzanTradesSoldGetParams = new YouzanTradesSoldGetParams();
            youzanTradesSoldGetParams.setStartCreated(currentDate);
            youzanTradesSoldGetParams.setEndCreated(historyDate);
            youzanTradesSoldGetParams.setPageNo(1);
            dealOrder(youzanTradesSoldGetParams,token);


            //遍历频道，计算当日收益汇总 （销售收益 分成收益两条数据）
            List<Relationship> relationships = relationshipService.getRelationshipList(null);
            for (Relationship relationship : relationships){
                //统计销售收益
                BigDecimal totalProfit = profitDetailService.sumProfit(
                        relationship.getChannelId(),historyDate,currentDate)
                        .multiply(new BigDecimal("0.25"));

                //统计分成收益
                Relationship where = new Relationship();
                where.setParentId(relationship.getChannelId());
                List<Relationship> children = relationshipService.getRelationshipList(where);
                BigDecimal totalDividend = BigDecimal.ZERO;
                for (Relationship child : children){
                    totalDividend = totalDividend.add(profitDetailService.sumProfit(
                            relationship.getChannelId(),historyDate,currentDate));
                }
                totalDividend = totalDividend.multiply(new BigDecimal("0.05"))
                        .setScale(2, BigDecimal.ROUND_DOWN);
                //当日统计插入数据库
                DailyProfit dailyProfit = new DailyProfit();
                dailyProfit.setChannelId(relationship.getChannelId());
                dailyProfit.setDividend(totalDividend);
                dailyProfit.setProfit(totalProfit);
                dailyProfitService.setDailyProfit(dailyProfit);
            }
            //将currentDate更新到系统配置信息表
            systemParam.setValue(String.valueOf(currentDate.getTime()));
            systemParamService.updateSystemParam(systemParam);

        }catch (Exception e){
            log.error("insurance over due task failed.");
            throw e;
        }

        log.info("insurance over due task end.");
        return true;
    }

    private void dealOrder(YouzanTradesSoldGetParams youzanTradesSoldGetParams,Token token) throws SDKException {
        YouzanTradesSoldGet youzanTradesSoldGet = new YouzanTradesSoldGet();
        youzanTradesSoldGet.setAPIParams(youzanTradesSoldGetParams);
        YouzanTradesSoldGetResult result = yzClient.invoke(youzanTradesSoldGet, token, YouzanTradesSoldGetResult.class);

        log.info("deal order for page {},data size: {}",youzanTradesSoldGetParams.getPageNo(),result.getData().getFullOrderInfoList().size());

        //计算订单收益，订单时间，频道id 生成收益明细记入数据库
        if(result.getData().getFullOrderInfoList()!=null
                && result.getData().getFullOrderInfoList().size()>0){
            for (YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist info
                    : result.getData().getFullOrderInfoList()){
                String payment = info.getFullOrderInfo().getPayInfo().getPayment();  //订单支付金额
                Date created = info.getFullOrderInfo().getOrderInfo().getCreated();  //订单创建时间
                String tid = info.getFullOrderInfo().getOrderInfo().getTid();  //订单编号
                BigDecimal profit = new BigDecimal(0);
                BigDecimal cost = BigDecimal.ZERO;   //总成本
                String channelId = "";
                for (YouzanTradesSoldGetResult.YouzanTradesSoldGetResultOrders item
                        :info.getFullOrderInfo().getOrders()){
                    Long num = item.getNum();
                    Long itemId = item.getItemId();
                    channelId = item.getBuyerMessages().split(":")[1];  //买家留言
                    //获取商品信息
                    YouzanItemGet youzanItemGet = new YouzanItemGet();

                    YouzanItemGetParams youzanItemGetParams = new YouzanItemGetParams();
                    youzanItemGetParams.setItemId(itemId);
                    youzanItemGet.setAPIParams(youzanItemGetParams);

                    YouzanItemGetResult goods = yzClient.invoke(youzanItemGet, token, YouzanItemGetResult.class);
                    Long itemCost = goods.getData().getItem().getCostPrice();  //单商品成本
                    cost = cost.add(new BigDecimal(num*itemCost));
                }

                profit = new BigDecimal(payment).subtract(cost);  //单笔订单收益
                ProfitDetail detail = new ProfitDetail();
                detail.setTid(tid);
                detail.setChannelId(channelId);
                detail.setProfit(profit);
                detail.setOrderCreated(created);

                profitDetailService.setProfitDetail(detail);
            }
            youzanTradesSoldGetParams.setPageNo(youzanTradesSoldGetParams.getPageNo()+1);
            dealOrder(youzanTradesSoldGetParams,token);
        }

    }
}
