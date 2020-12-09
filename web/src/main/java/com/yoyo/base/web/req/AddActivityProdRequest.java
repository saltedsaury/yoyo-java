package com.yoyo.base.web.req;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AddActivityProdRequest {

    /**
     * 活动id
     */
    private String activityId;


    /**
     * 商品编号列表
     */
    private List<String> products;
}
