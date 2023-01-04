package com.cwj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.Category;
import com.cwj.reggie.entity.Employee;
import com.cwj.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/category")
    public R<String> addClassify(@RequestBody Category category) {

        boolean b = categoryService.save(category);
        if (!b) {
            return R.error("未知错误");
        }
        return R.success("添加成功");
    }

    @PutMapping("/category")
    public R<String> updateClassify(@RequestBody Category category) {

        boolean b = categoryService.updateById(category);
        if (!b) {
            return R.error("未知错误");
        }
        return R.success("修改成功");
    }

    @DeleteMapping("/category")
    public R<String> deleteClassify(Long ids) {
        boolean b = categoryService.remove(ids);

        if (!b) {
            return R.error("未知错误");
        }
        return R.success("删除成功");
    }


    //处理分页
    @GetMapping("/category/page")
    public R<Page<Category>> pagination(Integer page, Integer pageSize) {

        Page<Category> pageObj = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(pageObj, wrapper);
        return R.success(pageObj);
    }

    @GetMapping("/category/list")
    public R<List<Category>> getAllCategory(Integer type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null,Category::getType, type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }
}
