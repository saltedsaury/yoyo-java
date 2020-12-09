package com.yoyo.base.service.service;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.SystemParam;

public interface ISystemParamService {

    boolean updateSystemParam(SystemParam systemParam);

    SystemParam getSystemParam(String paramId);
}
