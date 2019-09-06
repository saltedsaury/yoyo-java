package cn.idachain.finance.batch.service.external.model;

import lombok.Builder;
import lombok.Data;

/**
 * Created by yuanjiaintao on 2019/2/12.
 */
@Data
@Builder
public class UnfreezeParam {
    private String outOrderNo;
    private String freezeOutOrderNo;
}
