package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by zwm
 */
public class PropertiesUtil {

    private static Logger logger=LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;


    static {
        String fileName="mmall.properties";
        props=new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (IOException e) {
            logger.info("读取配置文件异常",e);
        }
    }


    //工具类就是静态方法
    public static String getProperty(String key){
        //防止两边有空格
        String value=props.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return null;
        }
        return value.trim();
    }

    //重载
    public static String getProperty(String key,String defaultValue){
        //防止两边有空格
        String value=props.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            value=defaultValue;
        }
        return value.trim();
    }

}
