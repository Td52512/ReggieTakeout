package com.cwj.reggie.dto;

import com.cwj.reggie.entity.Setmeal;
import com.cwj.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
