package com.cwj.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.reggie.common.R;
import com.cwj.reggie.dto.DishDto;
import com.cwj.reggie.entity.Category;
import com.cwj.reggie.entity.Dish;
import com.cwj.reggie.entity.DishFlavor;
import com.cwj.reggie.exception.CustomException;
import com.cwj.reggie.service.CategoryService;
import com.cwj.reggie.service.DishFlavorService;
import com.cwj.reggie.service.DishService;
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
 * 菜品管理 前端控制器
 * </p>
 *
 * @author cwj
 * @since 2023-01-01
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/page")
    //获取分页数据
    public R<Page> pagination(Integer page, Integer pageSize, String name) {
        Page<Dish> pageObj = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Dish::getUpdateTime)
                .like(StringUtils.isNotBlank(name), Dish::getName, name);
        dishService.page(pageObj, wrapper);
        //对象属性cope 第一个为原对象 第二个为接收值的对象 第三个为要忽略掉的属性 填属性名
        BeanUtils.copyProperties(pageObj,dishDtoPage,"records");
        //单独对records进行处理
        List<Dish> records = pageObj.getRecords();
        List<DishDto> dtoList = new ArrayList<>();
        records.forEach(item -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setCategoryName(category.getName());
            dtoList.add(dishDto);
        });

        dishDtoPage.setRecords(dtoList);

        return R.success(dishDtoPage);
    }

    @PostMapping
    //添加菜品
    public R<String> addDish(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }

    @GetMapping("/{id}")
    //获取修改数据
    public R<DishDto> getUpdateData(@PathVariable("id") Long id){
        Dish dish = dishService.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        Category category = categoryService.getById(dish.getCategoryId());
        dishDto.setCategoryName(category.getName());
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        dishDto.setFlavors(dishFlavorList);
        return R.success(dishDto);
    }

    @PutMapping
    //执行修改操作
    @Transactional
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> {
            item.setDishId(dishDto.getId());
        });
        dishFlavorService.saveBatch(flavors);

        return R.success("修改成功");
    }

    //禁售启售商品
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@RequestParam("ids") String ids,@PathVariable("status") Integer status){
        String[] strings = ids.split(",");
        Arrays.stream(strings).forEach(item -> {
            Dish dish = dishService.getById(item);
            dish.setStatus(status);
            dishService.updateById(dish);
        });

        return R.success("操作成功");
    }

    //删除商品
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") String ids){
        String[] strings = ids.split(",");
        List<String> list = Arrays.asList(strings);
        list.forEach(item -> {
            Dish dish = dishService.getById(item);
            if(dish.getStatus() == 1){
                throw new CustomException("商品下架之后才能删除");
            }
        });
        dishService.removeByIds(list);
        return R.success("操作成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> getCategory( Long categoryId,String name){
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null,Dish::getCategoryId,categoryId)
                .like(StringUtils.isNotBlank(name),Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .eq(Dish::getStatus,"1");
        List<Dish> dishes = dishService.list(wrapper);
        List<DishDto> dishDtos = new ArrayList<>();
       dishes.forEach(item -> {
           DishDto dishDto = new DishDto();
           BeanUtils.copyProperties(item,dishDto);

           LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
           wrapper1.eq(DishFlavor::getDishId,item.getId());
           List<DishFlavor> list = dishFlavorService.list(wrapper1);
           dishDto.setFlavors(list);
           dishDtos.add(dishDto);
       });
        return R.success(dishDtos);
    }

}
