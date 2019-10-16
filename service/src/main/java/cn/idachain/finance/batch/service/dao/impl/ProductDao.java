package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.ProductAgreement;
import cn.idachain.finance.batch.common.dataobject.ProductInfo;
import cn.idachain.finance.batch.common.mapper.ProductAgreementMapper;
import cn.idachain.finance.batch.common.mapper.ProductInfoMapper;
import cn.idachain.finance.batch.common.model.Product;
import cn.idachain.finance.batch.service.dao.IProductDao;
import cn.idachain.finance.batch.service.util.convert.ProductConvert;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ProductDao implements IProductDao {

    @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private ProductAgreementMapper productAgreementMapper;

    @Override
    public List<Product> getProductsByStatus(List<String> status, Page page){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("status",status);
        List<ProductInfo> products = null;
        if (page != null){
            products = productInfoMapper.selectPage(page,wrapper);
        }else {
            products = productInfoMapper.selectList(wrapper);
        }

        List<Product> result = new ArrayList<Product>();
        for(ProductInfo product : products){
            ProductAgreement agreementWrapper = new ProductAgreement();
            agreementWrapper.setProductNo(product.getProductNo());
            ProductAgreement productAgreement = productAgreementMapper.selectOne(agreementWrapper);
            Product temp = ProductConvert.convertToProduct(product,productAgreement);
            result.add(temp);
        }
        return result;
    }

    @Override
    public Product getProductByNo (String productNo){
        ProductInfo infoWrapper = new ProductInfo();
        infoWrapper.setProductNo(productNo);
        ProductInfo productInfo = productInfoMapper.selectOne(infoWrapper);
        ProductAgreement agreementWrapper = new ProductAgreement();
        agreementWrapper.setProductNo(productNo);
        ProductAgreement productAgreement = productAgreementMapper.selectOne(agreementWrapper);
        Product product = ProductConvert.convertToProduct(productInfo,productAgreement);
        return product;
    }

    @Override
    public int updateSurplusAmount(BigDecimal amount,String productNo) {
        return productAgreementMapper.updateSurplusAmount(amount,productNo);
    }

    @Override
    public void updateProductByObj(Product product,String status){
        EntityWrapper<ProductInfo> infoWrapper = new EntityWrapper<ProductInfo>();
        infoWrapper.eq("product_no",product.getProductNo());
        infoWrapper.eq("status",product.getStatus());

        ProductInfo info = new ProductInfo();
        info.setStatus(status);
        productInfoMapper.update(info,infoWrapper);
    }

    @Override
    public List<Product> getProductsByDate(String status,Date current){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.le("due_date",current);
        List<ProductAgreement> agreements = productAgreementMapper.selectList(wrapper);

        List<Product> result = new ArrayList<Product>();
        for(ProductAgreement agreement : agreements){
            ProductInfo infoWrapper = new ProductInfo();
            infoWrapper.setProductNo(agreement.getProductNo());
            infoWrapper.setStatus(status);
            ProductInfo info = productInfoMapper.selectOne(infoWrapper);
            if (info != null) {
                Product temp = ProductConvert.convertToProduct(info, agreement);
                result.add(temp);
            }
        }
        return result;
    }

    @Override
    public void updateProductValueDate(Product product,Date valueDate,Date dueDate){
        EntityWrapper<ProductAgreement> wrapper = new EntityWrapper<ProductAgreement>();
        wrapper.eq("product_no",product.getProductNo());

        ProductAgreement agreement = new ProductAgreement();
        agreement.setValueDate(valueDate);
        agreement.setDueDate(dueDate);
        productAgreementMapper.update(agreement,wrapper);
    }
}
