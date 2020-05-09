package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.CategoryServiceI;
import com.mmall.service.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author zwm
 * @create 2020/5/9  9:09
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private UserServiceI userServiceI;

    @Autowired
    private CategoryServiceI categoryServiceI;

    /**
     * 增加节点
     */
    @RequestMapping("/add_category.do")
    public ServerResponse addCategory(HttpSession session,@RequestParam("categoryName") String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断是否登录
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        //如果登录了，判断是否是管理员，是管理员处理分类逻辑，不是管理员则无权限
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //表示是管理员，进行业务处理
            return categoryServiceI.addCategory(categoryName, parentId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }


    /**
     * 修改品类名称
     */
    @RequestMapping("/set_category_name.do")
    public ServerResponse SetCategoryName(HttpSession session,@RequestParam("categoryName") String categoryName,@RequestParam("categoryId")Integer categoryId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断是否登录
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if (userServiceI.checkAdminRole(user).isSuccess()){
            return categoryServiceI.updateCategoryName(categoryName,categoryId);
        }
        return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
    }


    //查询品类
    @RequestMapping("get_category.do")
    public ServerResponse<List<Category>> getCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断是否登录
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if (userServiceI.checkAdminRole(user).isSuccess()){
            return categoryServiceI.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    //查询所有子节点，递归
    @RequestMapping("get_deep_category.do")
    public ServerResponse<List<Integer>> getDeepCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId){
        //从session中获取当前对象
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //判断是否登录
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请先登录");
        }
        if (userServiceI.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点的id
            return categoryServiceI.getCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }



    }





}
