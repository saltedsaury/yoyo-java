package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.model.Product;
import com.baomidou.mybatisplus.plugins.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IProductDao {

    List<Product> getProductsByStatus(List<String> status, Page page);

    Product getProductByNo(String productNo);

    int updateSurplusAmount(BigDecimal amount,String productNo);

    void updateProductByObj(Product product, String status);

    List<Product> getProductsByDate(String status, Date current);

    void updateProductValueDate(Product product, Date valueDate, Date dueDate);
}
