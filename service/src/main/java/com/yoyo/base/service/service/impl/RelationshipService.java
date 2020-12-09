package com.yoyo.base.service.service.impl;

import com.yoyo.base.common.dataobject.Relationship;
import com.yoyo.base.service.dao.IRelationshipDao;
import com.yoyo.base.service.service.IRelationshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RelationshipService implements IRelationshipService {

    @Autowired
    private IRelationshipDao relationshipDao;

    @Override
    public List<Relationship> getRelationshipList(Relationship relationship) {
        return relationshipDao.getRelationshipList(relationship);
    }

}