package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class SystemBatch {
    private Integer id;

    private String batchCode;

    private String batchDesc;

    private String preBatchCode;

    private String status;

    private Date finishDate;

    private String batchType;

    private Date createTime;

    private Date modifiedTime;

}