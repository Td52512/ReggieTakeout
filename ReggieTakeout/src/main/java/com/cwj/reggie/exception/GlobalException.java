package com.cwj.reggie.exception;

import com.cwj.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

//只拦截控制器发生的异常
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalException {

    //处理用户已存在异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> AddEmpExceptionHandler(Exception e) {
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            String[] s = message.split(" ");
            int i = 0;
            for (int j = 0; j < s.length; j++) {
                if(s[j].equals("entry")){
                    i = j + 1;
                }
            }
            log.info("发生了唯一约束异常 异常信息为：{}", message);
            return R.error("用户名为：" + s[i] + " 的用户已存在 请重新填写！！");
        }

        return R.error("未知错误");
    }

    //处理菜品分类/套餐关联商品异常
    @ExceptionHandler(CustomException.class)
    public R<String> DelClassifyExceptionHandler(Exception e){
        String message = e.getMessage();
        return R.error(message);
    }
}
