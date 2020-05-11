package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.FileServiceI;
import com.mmall.util.FTPUtil;
import org.apache.commons.net.ftp.FTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zwm
 * @create 2020/5/11  10:42
 */
@Service
public class FileServiceImpl implements FileServiceI {

    private Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file,String path){
        //原始文件名
        String filename = file.getOriginalFilename();

        //拿到扩展名，用户上传文件的文件名拼接    abc.jpg
        //最后一个以 . 的地方， +1是得到最后的扩展名，不然有 .
        String uploadExtensionName = filename.substring(filename.lastIndexOf(".") + 1);
        //获取文件上传名称
        String uploadFileName=UUID.randomUUID().toString()+"."+uploadExtensionName;
        logger.info("开始上传文件，上传的文件名:{},上传的路径:{},新文件名:{}",filename,path,uploadFileName);

        //目录文件
        File fileDir=new File(path);
        if (!fileDir.exists()){
            //赋予权限：可写
            fileDir.setWritable(true);
            //创建文件夹
            fileDir.mkdirs();
        }

        //创建文件
        File targetFile=new File(path,uploadFileName);

        try {

            //这个时候表示上传文件成功了
            file.transferTo(targetFile);


            //需要传到ftp服务器上，写一个ftp上传的工具类
             FTPUtil.uploadFile(Lists.newArrayList(targetFile));

             //上传完之后需要删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }



}
