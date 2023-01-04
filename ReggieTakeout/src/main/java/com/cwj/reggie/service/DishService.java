package com.cwj.reggie.service;

import com.cwj.reggie.dto.DishDto;
import com.cwj.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品管理 服务类
 * </p>
 *
 * @author cwj
 * @since 2023-01-01
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);
}
