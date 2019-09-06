package cn.idachain.finance.batch.service.external.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <p>批次划转请求参数</p>
 *
 * @author yehe
 * @version 1.0
 * @since 2019/3/16 18:50
 */
@Data
@Builder
public class BatchTransferParam {

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转入明细
     */
    private List<TransferInfoData> channelBatchTransferInItemList;
}
