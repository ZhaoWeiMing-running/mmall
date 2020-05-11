package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.FileServiceI;
import com.mmall.service.ProductServiceI;
import com.mmall.service.UserServiceI;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author zwm
 * @create 2020/5/9  17:13
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private UserServiceI userServiceI;

    @Autowired
    private ProductServiceI productServiceI;

    @Autowired
    private FileServiceI fileServiceI;


    //新增和更新操作
    @RequestMapping("/save.do")
    public ServerResponse saveAndUpdateProduct(HttpSession session,Product product){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充早呢更加或修改的逻辑
            return productServiceI.saveAndUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

    //商品上下架
    @RequestMapping("/set_sale_status.do")
    public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充修改状态逻辑
            return productServiceI.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }


    //商品详情
    @RequestMapping("/detail.do")
    public ServerResponse getDetail(HttpSession session,Integer productId){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充商品详情逻辑
            return productServiceI.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }



    //商品列表
    @RequestMapping("/list.do")
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                    @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充商品列表逻辑
            return productServiceI.getList(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }


    //商品列表
    @RequestMapping("/search.do")
    public ServerResponse search(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                  String productName,
                                  Integer productId){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充商品搜索逻辑
            return productServiceI.search(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }


    //上传文件
    //根据HttpServletRequest上下文创建一个动态的相对路径出来
    @RequestMapping("/upload.do")
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request){
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，请登录");
        }
        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充上传文件逻辑
            //得到路径
            String path = request.getSession().getServletContext().getRealPath("upload");

            String targetFileName= fileServiceI.upload(file,path);
            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);

        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    //富文本图片上传
    @RequestMapping("/richtext_img_upload.do")
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
       Map resultMap= Maps.newHashMap();
        //强制登录
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

        //判断是否是管理员
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //填充上传文件逻辑
            //得到路径
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=fileServiceI.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            //获取文件url
            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            //修改头文件
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }

}
