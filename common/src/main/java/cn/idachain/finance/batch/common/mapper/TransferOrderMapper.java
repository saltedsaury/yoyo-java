package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.TransferOrder;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TransferOrderMapper extends SuperMapper<TransferOrder> {

    @Select("<script>" +
            "select * from transfer_order where status = #{status} " +
            " and process_status in " +
            " <foreach collection='process' item='item' open='(' separator=',' close=')'> " +
            " #{item} </foreach> " +
            "and NOW() > date_add(create_time,interval 1 minute)" +
            "</script>")
    List<TransferOrder> selectListForConfirm(@Param("status") String status, @Param("process") List<String> process, Page page);

    @Select("select id from transfer_order order by id desc limit 1")
    Long lastId();

}