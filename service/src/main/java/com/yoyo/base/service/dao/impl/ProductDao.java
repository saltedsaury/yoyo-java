package com.yoyo.base.service.dao.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yoyo.base.common.dataobject.ActivityProduct;
import com.yoyo.base.common.dataobject.Product;
import com.yoyo.base.common.mapper.ActivityProductMapper;
import com.yoyo.base.common.mapper.ProductMapper;
import com.yoyo.base.service.dao.IActivityProductDao;
import com.yoyo.base.service.dao.IProductDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ProductDao implements IProductDao {

    @Autowired
    private ProductMapper productMapper;


}
