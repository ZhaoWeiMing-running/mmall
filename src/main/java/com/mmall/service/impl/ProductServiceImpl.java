package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.CategoryServiceI;
import com.mmall.service.ProductServiceI;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zwm
 * @create 2020/5/9  17:31
 */
@Service
public class ProductServiceImpl implements ProductServiceI {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryServiceI categoryServiceI;

    @Override
    public ServerResponse saveAndUpdateProduct(Product product){
        //如果产品不为空情况下
        if (product!=null){
            //获取前端传来的子图字符串用,进行分割
            //然后第一块代码是将这个字符串按照,来分割成字符串数组，就可以获得所有子图
            //然后把第一张子图作为主图来存储
            if (StringUtils.isNotBlank(product.getMainImage())){
                //用，分割成字符串数组
                String[] subImageArray=product.getSubImages().split(",");
                if (subImageArray.length>0){
                    //将第一张图作为主图
                    product.setMainImage(subImageArray[0]);
                }
            }
            //根据id判断，如果传过来id不为空，则是更新操作，否则是新增操作
            if (product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }else {
                int rowCount = productMapper.insert(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccessMessage("添加商品成功");
                }
                return ServerResponse.createByErrorMessage("添加商品失败");
            }
        }
        //其他情况就是参数不正确
        return ServerResponse.createByErrorMessage("添加或更新产品参数有误");
    }


    /**
     * 产品上下架
     */
    @Override
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId==null || status==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product=new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }


    /**
     * 商品详情
     */
    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        //校验
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //查询所有商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.createByErrorMessage("商品已下架或已删除");
        }

        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    //封装一个productDetailVo
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost,从配置文件获取,图片的地址，连接ftp，需要写一个配置工具类PropertiesUtil
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));


        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category==null){
            productDetailVo.setParentCategoryId(0);  //默认是根节点
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //时间需要用joda-time封装
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }



    //商品列表
    @Override
    public ServerResponse<PageInfo> getList(int pageNum,int pageSize){

        //startPage---start
        //填充自己的sql查询逻辑
        //pageHelper--收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList= Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo=assembleProductListVo(product);
            //将list添加进去
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    //Json需要返回的字段
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
        //返回全部
        return productListVo;
    }


    //商品搜索
    @Override
    public ServerResponse<PageInfo> search(String productName,Integer productId,int pageNum,int pageSize){

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        //分页
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        //产品为空
        if (product==null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //状态校验
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo=assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }


    //前台分页列表展示+模糊搜索
    @Override
    public ServerResponse<PageInfo> list(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        //校验keyword和categoryId都为null的情况
        if (StringUtils.isBlank(keyword) && categoryId==null){
            //参数有误
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //创建分类子集的集合
        List<Integer> categoryIdList=new ArrayList<>();

        if (categoryId!=null){
            //查出所有分类
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //没有该分类，并且没有关键字，这个时候返回一个空集，不报错，这个不是报错行为
            if (category==null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList=Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            //递归查出所有子类
            categoryIdList = categoryServiceI.getCategoryAndChildrenById(category.getId()).getData();
        }

        /**
         * 以下部分不理解
         */

        PageHelper.startPage(pageNum, pageSize);
        //动态排序处理
        //如果排序不为空，并且
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                //和前端约定用_分割
                String[] orderByArray = orderBy.split("_");
                //order排序格式price asc,中间有空格
                PageHelper.orderBy(orderByArray[0]+' '+orderByArray[1]);
            }
        }

        //categoryIdList已经被赋值； ，上面也有一个new，如果我们传一个空集合进去，那么他不是空，sql中in里面就没有值，如果没有值的话，那么就查不出结果，所以要对keyword和这个集合进行校验
        List<Product> productList=productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);


        List<ProductListVo> productListVoList=Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo=assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        //返回的是sql查询的
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
