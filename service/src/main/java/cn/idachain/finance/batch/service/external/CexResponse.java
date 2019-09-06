package cn.idachain.finance.batch.service.external;

import lombok.Builder;
import lombok.Data;

/**
 * Created by liuhailin on 2019/2/2.
 */
@Data
@Builder
public class CexResponse {
    private String code;
    private Object data;
    private String msg;
    private String traceId;
}
