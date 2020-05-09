package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryServiceI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author zwm
 * @create 2020/5/9  9:59
 */
@Service
public class CategoryServiceImpl implements CategoryServiceI {

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);


    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName,Integer parentId){
        //对参数校验
        if (parentId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category=new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        //刚创建完，这个分类是可用的true
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    //修改品类名称
    @Override
    public ServerResponse updateCategoryName(String categoryName, Integer categoryId) {
        //对参数校验
        if (categoryId==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改品类参数错误");
        }

        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("修改品类成功");
        }
        return ServerResponse.createByErrorMessage("修改品类失败");
    }

    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //判断集合不能为空
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     * 获取当前categoryId下的子节点还要继续查找子节点是否还有子节点，及所有子孙节点
     * 0--》10---》100
     * 如果传0，会返回10
     * 传10，会返回100
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer categoryId) {
        //创建一个set集合，初始化
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        //引用已经返回了
        if(categoryId != null){
            //遍历set的时候，把categoryId装到集合再返回去
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }


    //递归算法
    //要用category对象，需要重写equals和hashcode
    //参数增加这个Set<Category> categorySet，也就说我们把这个参数当做返回值返回给方法本身，然后那和这个方法本身的返回值再调用这个方法，当成这个方法的参数
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        //如果父节点有值往下走
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        //如果父节点不为空，先放到set集合中
        if (category!=null){
            categorySet.add(category);
        }
        //递归的时候要有结束点，拿parentId去查询所有的子节点，如果这个list是空的话，进不来for循环，然后就直接返回了
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //因为是用mabatis方法，所以不用进行判断，到那时不用的，会报空指针异常
        for (Category categoryItem: categoryList){
            //继续调用自己
            findChildCategory(categorySet,categoryItem.getId());
        }
        //把引用返回出去
        return categorySet;
        /*if (!CollectionUtils.isEmpty(categoryList)){
            for (Category categoryItem: categoryList){
                findChildCategory(categorySet,categoryItem.getId());
            }
        }*/

    }


}
