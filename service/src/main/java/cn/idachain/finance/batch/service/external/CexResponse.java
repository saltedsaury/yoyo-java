package cn.idachain.finance.batch.service.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liuhailin on 2019/2/2.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CexResponse {
    private String code;
    private Object data;
    private String msg;
    private String traceId;
}
