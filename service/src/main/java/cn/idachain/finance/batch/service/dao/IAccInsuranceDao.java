package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccInsurance;

public interface IAccInsuranceDao {
    int addAccInsurance(String accountNo, String insuranceNo, String accountType);

    AccInsurance getAccByInsurance(String insuranceNo, String accountType);
}
