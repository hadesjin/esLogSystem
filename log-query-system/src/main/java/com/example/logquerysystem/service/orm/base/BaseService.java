package com.example.logquerysystem.service.orm.base;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: houjinbiao
 * @Date: 2024/1/22 10:58
 */

public interface BaseService<T> {
    List<T> search(T record);

    List<T> search(T record, String order);

    PageInfo<T> search(T record, int page, int size);

    PageInfo<T> search(T record, int page, int size, String order);

    T findById(Integer id);

    T findOne(T record);

    int selectCount(T record);

    List<T> select(T record);

    PageInfo<T> select(T record, int page, int size);

    PageInfo<T> select(T record, int page, int size, String order);

    List<T> selectAll();

    PageInfo<T> selectAll(int page, int size);

    PageInfo<T> selectAll(int page, int size, String order);

    int insert(T record);

    int insertSelective(T record);

    int updateById(T record);

    int updateByIdSelective(T record);

    int delete(T record);

    int deleteById(Integer id);

    int deleteByPk(Object pk);

    PageInfo<T> selectByConditions(int page, int size, Map<String, Object> equalMap,
                                   Map<String, Object> likeMap, Class cls);

    PageInfo<T> selectByConditions(int page, int size, Map<String, Object> equalMap,
                                   Map<String, Object> likeMap, String order, Class cls);

    List<T> selectByCondition(Map<String, Object> equalMap, Map<String, Object> likeMap, Class cls);
}
