package cn.idachain.finance.batch.common.model;

import lombok.Data;

@Data
public class InvestListVO {

    /**
     *投资单号
     */
    private String tradeNo;

    /**
     * 用户号
     */
    private String customerNo;

    /**
     * 产品编码
     */
    private String productNo;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 投资金额
     */
    private String amount;

    /**
     * 起息日
     */
    private String valueDate;

    /**
     * 已收利息
     */
    private String bonus;

    /**
     * 待收利息
     */
    private String prepareBonus;

    /**
     * 保险
     */
    private String insuranceName;

    /**
     * 保费金额
     */
    private String insuranceFee;

    /**
     * 保险单号
     */
    private String insuranceTradeNo;

    /**
     * 赎回金额
     */
    private String redemptionAmount;

    /**
     * 提前赎回手续费
     */
    private String fee;

    /**
     * 投资时间
     */
    private String investTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态-中文
     */
    private String statusStr;

}
