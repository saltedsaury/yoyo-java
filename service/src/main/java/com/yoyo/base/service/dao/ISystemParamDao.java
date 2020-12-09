package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.SystemParam;

public interface ISystemParamDao {

    Integer updateSystemParam(SystemParam systemParam);

    SystemParam getSystemParam(String paramId);
}
