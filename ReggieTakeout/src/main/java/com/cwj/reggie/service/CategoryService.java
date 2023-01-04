package com.cwj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    boolean remove(Long id);
}
