package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * @author zwm
 * @create 2020/5/9  9:59
 */
public interface CategoryServiceI {


    ServerResponse addCategory(String categoryName, Integer parentId);


    ServerResponse updateCategoryName(String categoryName, Integer categoryId);


    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId);

}
