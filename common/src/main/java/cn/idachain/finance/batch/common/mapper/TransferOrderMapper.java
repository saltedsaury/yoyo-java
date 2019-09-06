package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TransferOrderMapper extends SuperMapper<TransferOrder> {

    @Select("select * from transfer_order where status = #{status} " +
            "and NOW() > date_add(modified_time,interval 10 minute)")
    List<TransferOrder> selectListForConfirm(@Param("status") String status);

}