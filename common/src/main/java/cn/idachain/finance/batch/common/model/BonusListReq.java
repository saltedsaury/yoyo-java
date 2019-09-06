package cn.idachain.finance.batch.common.model;

import lombok.Data;

@Data
public class BonusListReq {

    private String productNo;

    private String customerNo;

    private String status; //30天内还款 已还款

    private String type;//收益  本金
}
