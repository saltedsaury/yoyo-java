package cn.idachain.finance.batch.service.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by liuhailin on 2019/2/2.
 */
@Setter
@Getter
@Builder
public class CexRequest {

    @JsonIgnore
    private String cexPassport;

    @JsonIgnore
    private String cexToken;

    private String deviceId;

    private String deviceSource;

    private String cexChannelNo;

    @JsonIgnore
    private String sign;

    private String outOrderNo;

    private Map<String, Object> data;

    @Override
    public String toString() {
        return "CexRequest{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceSource='" + deviceSource + '\'' +
                ", data=" + data +
                '}';
    }
}
