package cn.idachain.finance.batch.service.dao;

import cn.idachain.finance.batch.common.dataobject.SystemDate;

public interface ISystemDateDao {
    SystemDate getSystemDateByType(String type);

    void updateSystemDate(SystemDate systemDate);
}
