package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.AccProd;
import cn.idachain.finance.batch.common.mapper.AccProdMapper;
import cn.idachain.finance.batch.service.dao.IAccProdDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccProdDao implements IAccProdDao {

    @Autowired
    private AccProdMapper accProdMapper;

    @Override
    public int addAccProd(String accountNo, String prodNo, String accountType){
        AccProd accProd = new AccProd();
        accProd.setAccountNo(accountNo);
        accProd.setProductNo(prodNo);
        accProd.setAccountType(accountType);
        return accProdMapper.insert(accProd);
    }

    @Override
    public AccProd getAccByProd(String prodNo, String accountType){
        AccProd condition = new AccProd();
        condition.setAccountType(accountType);
        condition.setProductNo(prodNo);
        return accProdMapper.selectOne(condition);
    }
}
