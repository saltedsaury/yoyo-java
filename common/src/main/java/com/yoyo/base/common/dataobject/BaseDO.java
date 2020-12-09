package com.yoyo.base.common.dataobject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>基础模型对象</p>
 *
 * @author wen
 * @version 1.0
 * @since 2018/6/8 11:56
 */
@Getter
@Setter
@ToString
class BaseDO implements Serializable{
    private static final long serialVersionUID = -8736677060714333317L;
    /**
     * 系统ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifiedTime;

}
