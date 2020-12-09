package com.yoyo.base.common.dataobject;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@ToString
@Data
public class SystemParam extends BaseDO {

    private String paramId;

    private String paramName;

    private String remark;

    private String value;

}
