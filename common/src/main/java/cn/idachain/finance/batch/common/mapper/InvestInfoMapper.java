package cn.idachain.finance.batch.common.mapper;

import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.InvestInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface InvestInfoMapper extends SuperMapper<InvestInfo> {

    @Select("<script>" +
            " select sum(amount) " +
            " from invest_info " +
            " where status in " +
            " <foreach collection='status' item='item' open='(' separator=',' close=')'> " +
            " #{item} </foreach> " +
            " and customer_no = #{uid} " +
            " and ccy = #{ccy} group by customer_no" +
            "</script>")
    BigDecimal sumTotalAmountByStatus(@Param("status") List<String> status,
                                      @Param("uid") String uid, @Param("ccy") String ccy);

    @Update("<script>update invest_info set reconciled = 1 where trade_no in " +
            "<foreach collection='collection' item='no' separator=',' open='(' close=')'>" +
            "#{no}</foreach></script>")
    int markReconciled(Collection<String> orderNos);
}