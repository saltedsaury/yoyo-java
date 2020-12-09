package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.ProfitDetail;
import com.yoyo.base.common.dataobject.Relationship;
import com.yoyo.base.common.mapper.ProfitDetailMapper;
import com.yoyo.base.common.mapper.RelationshipMapper;
import com.yoyo.base.common.util.BlankUtil;
import com.yoyo.base.service.dao.IProfitDetailDao;
import com.yoyo.base.service.dao.IRelationshipDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RelationshipDao implements IRelationshipDao {

    @Autowired
    private RelationshipMapper relationshipMapper;

   /* public List<com.yoyo.base.common.dataobject.ProfitDetail> getProductsByActivityId(String activityId) {
        EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail> wrapper = new EntityWrapper<com.yoyo.base.common.dataobject.ProfitDetail>();
        wrapper.eq("activity_id",activityId);
        activityMappingMapper.selectList(wrapper);
        return activityMappingMapper.selectList(wrapper);
    }*/


    @Override
    public List<Relationship> getRelationshipList(Relationship relationship) {
        EntityWrapper<Relationship> wrapper = new EntityWrapper<Relationship>();
        if (null != relationship){
            if (BlankUtil.isBlank(relationship.getParentId())){
                wrapper.eq("parent_id",relationship.getParentId());
            }
        }
        return relationshipMapper.selectList(wrapper);
    }
}
