package cn.idachain.finance.batch.service.service.dto;

import cn.idachain.finance.batch.common.enums.AccountType;
import cn.idachain.finance.batch.common.enums.Direction;
import lombok.Data;

import java.util.List;

/**
 * @author kun
 * @version 2019/10/8 14:56
 */
@Data
public class AccountFlowDTO {

    private List<FlowDetail> flowDetails;

    private Long transferTime;

    @Data
    public static class FlowDetail {

        private Direction direction;

        private String accountNo;

        private String currency;

        private AccountType accountType;

        // org -> accountTransType
        private String subTypeCode;

        private String amount;
    }
}
