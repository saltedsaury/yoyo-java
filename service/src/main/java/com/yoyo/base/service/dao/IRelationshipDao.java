package com.yoyo.base.service.dao;

import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.Relationship;

import java.util.List;

public interface IRelationshipDao {

    List<Relationship> getRelationshipList(Relationship relationship);
}
