package com.yoyo.base.common.base;


import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 注意这个类不要让 mp 扫描到
 */
public interface SuperMapper<T> extends BaseMapper<T> {
    // 这里可以放一些公共的方法
}
