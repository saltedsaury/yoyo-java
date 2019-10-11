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

    @Select("select * from transfer_order where id > " +
            "(select max(id) from transfer_order where transfer_time <= #{param1}) " +
            "and id <= #{param2} and status in (1, 3)")
    List<TransferOrder> selectRecordedOrderAfter(Long time, Long stopId);

    @Select("select * from transfer_order " +
            "where (transfer_time > #{param1} or charge_time is not null) and id <= #{param2}")
    List<TransferOrder> getOrderByRange(Long startTime, Long lastId);

}