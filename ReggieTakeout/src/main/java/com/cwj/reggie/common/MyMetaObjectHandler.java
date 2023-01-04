package com.cwj.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//自定义元数据处理器
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //插入操作自动填充
        log.info("执行插入自动填充");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.get());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.get());
        log.info("执行人的id：{}",BaseContext.get());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //修改操作自动填充
        log.info("执行修改自动填充");
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.get());
        log.info("执行人的id：{}",BaseContext.get());
    }
}
