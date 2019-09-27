package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.AccProd;

public interface IAccProdDao {
    int addAccProd(String accountNo, String prodNo, String accountType);

    AccProd getAccByProd(String prodNo, String accountType);
}
