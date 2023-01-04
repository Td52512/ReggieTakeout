package com.cwj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cwj.reggie.common.R;
import com.cwj.reggie.dto.DishDto;
import com.cwj.reggie.entity.ShoppingCart;
import com.cwj.reggie.entity.User;
import com.cwj.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getCartData(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, uid);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        shoppingCart.setUserId(uid);
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(ShoppingCart::getUserId, uid);

        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one == null) {
            //新增
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        } else {
            //修改数量
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ShoppingCart::getNumber, one.getNumber() + 1)
                    .eq(one.getDishId() != null, ShoppingCart::getDishId, one.getDishId())
                    .eq(one.getSetmealId() != null, ShoppingCart::getSetmealId, one.getSetmealId());
            shoppingCartService.update(updateWrapper);
        }
        return R.success("添加成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId())
                .eq(ShoppingCart::getUserId, uid);

        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one.getNumber() == 1) {
            shoppingCartService.removeById(one.getId());
        } else {
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ShoppingCart::getNumber, one.getNumber() - 1)
                    .eq(one.getDishId() != null, ShoppingCart::getDishId, one.getDishId())
                    .eq(one.getSetmealId() != null, ShoppingCart::getSetmealId, one.getSetmealId());
            shoppingCartService.update(updateWrapper);
        }
        return R.success("操作成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, uid);
        shoppingCartService.remove(wrapper);
        return R.success("操作成功");
    }
}
