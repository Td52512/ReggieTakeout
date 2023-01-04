package com.cwj.reggie.service.impl;

import com.cwj.reggie.dto.DishDto;
import com.cwj.reggie.entity.Dish;
import com.cwj.reggie.entity.DishFlavor;
import com.cwj.reggie.mapper.DishFlavorMapper;
import com.cwj.reggie.mapper.DishMapper;
import com.cwj.reggie.service.DishFlavorService;
import com.cwj.reggie.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author cwj
 * @since 2023-01-01
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品信息
        dishMapper.insert(dishDto);
        //获取菜品id 封装到口味信息中
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(f -> {
            f.setDishId(dishId);
        });
        //保存口味信息
        dishFlavorService.saveBatch(flavors);
    }
}
