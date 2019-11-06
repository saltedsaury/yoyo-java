package cn.idachain.finance.batch.common.mapper;


import cn.idachain.finance.batch.common.base.SuperMapper;
import cn.idachain.finance.batch.common.dataobject.ProductAgreement;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

public interface ProductAgreementMapper extends SuperMapper<ProductAgreement> {

    @Update("update product_agreement set surplus_amount = #{amount} " +
            "where product_no = #{productNo} and surplus_amount > #{amount};")
    int updateSurplusAmount(@Param("amount") BigDecimal amount,@Param("productNo") String productNo);
}