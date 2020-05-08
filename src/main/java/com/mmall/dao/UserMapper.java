package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /*校验用户名*/
    int checkUsername(String username);

    /*校验邮箱*/
    int checkEmail(String email);


    User selectLogin(@Param("username") String username,@Param("password") String password);

    String selectQuestionByUsername(@Param("username") String username);

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    //重置密码
    int updatePasswordByUsername(@Param("username") String username,@Param("passwordNew") String passwordNew);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailByUserId(@Param("email") String email,@Param("userId") Integer userId);
}