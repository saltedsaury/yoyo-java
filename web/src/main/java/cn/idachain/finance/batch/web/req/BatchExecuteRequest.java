package cn.idachain.finance.batch.web.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BatchExecuteRequest {

    /**
     * 任务编号
     */
    private String batchCode;

    /**
     * 任务类型
     */
    private String batchType;
}
