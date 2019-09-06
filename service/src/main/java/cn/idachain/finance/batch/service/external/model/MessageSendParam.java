package cn.idachain.finance.batch.service.external.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Created by liuhailin on 2019/2/16.
 */
@Data
@Builder
public class MessageSendParam {
    private Long userNo;
    private String messageTemplateType;
    private Map<String, Object> valuesMap;

}
