package cn.idachain.finance.batch.service.dao.impl;

import cn.idachain.finance.batch.common.dataobject.AccInsurance;
import cn.idachain.finance.batch.common.mapper.AccInsuranceMapper;
import cn.idachain.finance.batch.service.dao.IAccInsuranceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccInsuranceDao implements IAccInsuranceDao {

    @Autowired
    private AccInsuranceMapper accInsuranceMapper;

    @Override
    public int addAccInsurance(String accountNo, String insuranceNo, String accountType){
        AccInsurance accProd = new AccInsurance();
        accProd.setAccountNo(accountNo);
        accProd.setInsuranceNo(insuranceNo);
        accProd.setAccountType(accountType);
        return accInsuranceMapper.insert(accProd);
    }

    @Override
    public AccInsurance getAccByInsurance(String insuranceNo, String accountType){
        AccInsurance condition = new AccInsurance();
        condition.setAccountType(accountType);
        condition.setInsuranceNo(insuranceNo);
        return accInsuranceMapper.selectOne(condition);
    }
}
