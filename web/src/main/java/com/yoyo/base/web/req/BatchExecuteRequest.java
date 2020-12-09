package com.yoyo.base.web.req;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BatchExecuteRequest {

    /**
     * 任务编号
     */
    private String channelId;

}
