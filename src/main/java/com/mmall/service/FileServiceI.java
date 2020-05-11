package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zwm
 * 文件处理接口
 * @create 2020/5/11  10:42
 */
public interface FileServiceI {

    String upload(MultipartFile file, String path);
}
