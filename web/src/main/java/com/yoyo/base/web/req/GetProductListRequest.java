package com.yoyo.base.web.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GetProductListRequest {

    /**
     * 活动id
     */
    private String activityId;

}
