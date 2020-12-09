package com.yoyo.base.service.service;

import com.yoyo.base.common.dataobject.Relationship;

import java.util.List;

public interface IRelationshipService {

    List<Relationship> getRelationshipList(Relationship relationship);
}
