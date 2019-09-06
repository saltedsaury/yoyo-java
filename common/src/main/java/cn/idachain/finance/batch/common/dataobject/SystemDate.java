package cn.idachain.finance.batch.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
public class SystemDate {
    private Integer id;

    private Date systemDate;

    private String dateType;

    private String status;

    private String remark;

    private Date createTime;

    private Date modifiedTime;

}