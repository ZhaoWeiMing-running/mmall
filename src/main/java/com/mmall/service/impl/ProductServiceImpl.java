package com.mmall.service.impl;

import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ProductServiceI;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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


}
