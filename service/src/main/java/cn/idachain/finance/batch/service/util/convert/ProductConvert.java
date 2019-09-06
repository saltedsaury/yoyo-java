package cn.idachain.finance.batch.service.util.convert;

import cn.idachain.finance.batch.common.dataobject.ProductAgreement;
import cn.idachain.finance.batch.common.dataobject.ProductInfo;
import cn.idachain.finance.batch.common.util.BlankUtil;
import cn.idachain.finance.batch.common.model.Product;

public class ProductConvert {

    public static Product convertToProduct(ProductInfo productInfo, ProductAgreement productAgreement){
        Product product = new Product();
        if(BlankUtil.isBlank(productInfo) || BlankUtil.isBlank(productAgreement)){
            return null;
        }
        product.setProductNo(productInfo.getProductNo());
        product.setProductName(productInfo.getProductName());
        product.setProductType(productInfo.getProductType());
        product.setCcy(productInfo.getCcy());
        product.setChannel(productInfo.getChannel());
        product.setEffectiveDate(productInfo.getEffectiveDate());
        product.setExpiryDate(productInfo.getExpiryDate());
        product.setFine(productAgreement.getFine());
        product.setFineType(productAgreement.getFineType());
        product.setCycleType(productAgreement.getCycleType());
        product.setGrad(productAgreement.getGrad());
        product.setMaxAmount(productAgreement.getMaxAmount());
        product.setMinAmount(productAgreement.getMinAmount());
        product.setPreRedeemFlag(productAgreement.getPreRedeemFlag());
        product.setSort(productInfo.getSort());
        product.setProductLabel(productInfo.getProductLabel());
        product.setProductLogo(productInfo.getProductLogo());
        product.setStatus(productInfo.getStatus());
        product.setVersion(productInfo.getVersion());
        product.setInterestCycle(productAgreement.getInterestCycle());
        product.setInterestMode(productAgreement.getInterestMode());
        product.setProfitScale(productAgreement.getProfitScale());
        product.setProfitPerCycle(productAgreement.getProfitPerCycle());
        product.setSurplusAmount(productAgreement.getSurplusAmount());
        product.setRaisedAmount(productAgreement.getRaisedAmount());
        product.setValueDate(productAgreement.getValueDate());
        product.setDueDate(productAgreement.getDueDate());
        product.setRules(productAgreement.getRules());
        product.setIntroduction(productAgreement.getIntroduction());

        return product;
    }
}
