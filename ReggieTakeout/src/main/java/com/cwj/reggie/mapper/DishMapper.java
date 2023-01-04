package com.cwj.reggie.mapper;

import com.cwj.reggie.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author cwj
 * @since 2023-01-01
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
