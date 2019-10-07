package cn.idachain.finance.batch.service.service.dto;

import cn.idachain.finance.batch.common.enums.Direction;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author kun
 * @version 2019/10/7 16:25
 */
@Data
@Accessors(chain = true)
public class TransferProcessDTO {

    private boolean success;

    private Direction direction;

    private Long outerTransferTime;

    private Long innerTransferTime;

    private TransferProcessDTO() { }

    public static TransferProcessDTO success() {
        return new TransferProcessDTO().setSuccess(true);
    }

    public static TransferProcessDTO fail() {
        return new TransferProcessDTO().setSuccess(false);
    }
}
