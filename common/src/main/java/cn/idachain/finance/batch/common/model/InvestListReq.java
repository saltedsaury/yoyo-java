package cn.idachain.finance.batch.common.model;

import lombok.Data;

@Data
public class InvestListReq {

    /**
     * 产品编号
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
     * 状态
     */
    private String status;

    /**
     * 起始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
}
