package com.cwj.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.reggie.entity.Category;
import com.cwj.reggie.entity.Dish;
import com.cwj.reggie.entity.Setmeal;
import com.cwj.reggie.exception.CustomException;
import com.cwj.reggie.mapper.CategoryMapper;
import com.cwj.reggie.mapper.DishMapper;
import com.cwj.reggie.mapper.SetmealMapper;
import com.cwj.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public boolean remove(Long id){
        //判断是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        Integer count1 = dishMapper.selectCount(dishLambdaQueryWrapper);
        if(count1 > 0){
            throw new CustomException("当前分类下关联了菜品 不能删除");
        }

        //判断是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        Integer count2 = setmealMapper.selectCount(setmealLambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomException("当前套餐下关联了菜品 不能删除");
        }


        int i = categoryMapper.deleteById(id);

        return i != 0;

    }
}
