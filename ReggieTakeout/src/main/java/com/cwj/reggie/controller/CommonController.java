package com.cwj.reggie.controller;

import com.cwj.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.upload.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(@RequestPart("file") MultipartFile multipartFile)throws Exception{
        if(multipartFile != null){
            String filename = multipartFile.getOriginalFilename();
            String suffix = filename.substring(filename.indexOf("."));
            filename = UUID.randomUUID() + suffix;

            File file = new File(basePath);
            if(!file.exists()){
                file.mkdir();
            }
            multipartFile.transferTo(new File(basePath + File.separator + filename));
            return R.success(filename);
        }

        return R.success("未上传");
    }

    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response)throws Exception{

        //设置响应类型为图片类型
        response.setContentType("image/jpeg");

        //输入流 读取文件内容
        FileInputStream fileInputStream = new FileInputStream(basePath + File.separator + name);

        //输出流 输出数据到浏览器
        ServletOutputStream outputStream = response.getOutputStream();
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,len);
            outputStream.flush();
        }

        outputStream.close();
        fileInputStream.close();


    }
}
