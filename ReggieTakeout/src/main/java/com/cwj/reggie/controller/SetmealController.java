package com.cwj.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.reggie.common.R;
import com.cwj.reggie.dto.SetmealDto;
import com.cwj.reggie.entity.Category;
import com.cwj.reggie.entity.Setmeal;
import com.cwj.reggie.entity.SetmealDish;
import com.cwj.reggie.exception.CustomException;
import com.cwj.reggie.service.CategoryService;
import com.cwj.reggie.service.SetmealDishService;
import com.cwj.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 套餐 前端控制器
 * </p>
 *
 * @author cwj
 * @since 2023-01-01
 */
@RestController
@RequestMapping("/setmeal")
@SuppressWarnings("all")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    //返回分页信息
    public R<Page> getPaginationData(Integer page, Integer pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        List<SetmealDto> list = new ArrayList<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);
        setmealService.page(setmealPage, wrapper);
        BeanUtils.copyProperties(setmealDtoPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        records.forEach(item -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryName);
            list.add(setmealDto);
        });
        setmealDtoPage.setTotal(setmealPage.getTotal());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @PostMapping
    @Transactional
    //添加套餐信息
    public R<String> add(@RequestBody SetmealDto setmealDto) {
        setmealService.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item -> {
            item.setSetmealId(setmealDto.getId());
        });
        setmealDishService.saveBatch(setmealDishes);
        return R.success("添加成功");
    }

    //获取套餐修改信息
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable("id") Long id) {
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(wrapper);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    //修改套餐信息
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateById(setmealDto);
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(wrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item -> {
            item.setSetmealId(setmealId);
        });

        setmealDishService.saveBatch(setmealDishes);
        return R.success("操作成功");
    }

    //停售/启售
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, String ids) {
        String[] strings = ids.split(",");
        Arrays.stream(strings).forEach(item -> {
            Setmeal setmeal = setmealService.getById(item);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        });
        return R.success("操作成功");
    }

    //删除套餐
    @DeleteMapping
    @Transactional
    public R<String> delete(String ids){
        String[] strings = ids.split(",");
        List<String> list = Arrays.asList(strings);
        list.forEach(item -> {
            Setmeal setmeal = setmealService.getById(item);
            if(setmeal.getStatus() == 1){
                throw new CustomException("套餐下架之后才能删除！！");
            }
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId,item);
            setmealDishService.remove(wrapper);
        });
        setmealService.removeByIds(list);

        return R.success("操作成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealInfo(Long categoryId){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId,categoryId);
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }
}
