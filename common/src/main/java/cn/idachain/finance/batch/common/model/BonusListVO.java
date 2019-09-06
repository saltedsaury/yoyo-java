package cn.idachain.finance.batch.common.model;

import lombok.Data;

@Data
public class BonusListVO {

    /**
     * 用户号
     */
    private String customerNo;

    /**
     * 投资单号
     */
    private String investNo;

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
     * 投资总额
     */
    private String amount;

    /**
     * 期数
     */
    private String round;

    /**
     *类型
     */
    private String type;

    /**
     *回款金额
     */
    private String bonus;

    /**
     *计划回款日期
     */
    private String bonusDate;

    /**
     *实际回款日期
     */
    private String actualDate;

    /**
     *状态
     */
    private String status;
}
