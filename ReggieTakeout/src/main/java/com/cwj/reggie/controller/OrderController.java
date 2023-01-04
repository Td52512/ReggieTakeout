package com.cwj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.reggie.common.BaseContext;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.*;
import com.cwj.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/submit")
    @Transactional
    public R<String> settlement(@RequestBody Orders orders,HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();

        //查询用户信息
//        User user = userService.getById(uid);
        //查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //查询购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, uid);
        List<ShoppingCart> carts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        //订单明细
        List<OrderDetail> orderDetails = new ArrayList<>();
        //订单编号
        Long orderNum = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        //生成订单详情
        carts.forEach(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(orderNum);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            //算总价
            //单价乘份数 然后再封装成int
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            orderDetails.add(orderDetail);
        });

        //生成订单
        orders.setId(orderNum);
        orders.setNumber(orderNum + "");
        orders.setUserId(uid);
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserName(user.getName());
        orders.setAmount(new BigDecimal(amount.get()));
        //保存订单
        ordersService.save(orders);

        //保存订单详情
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,uid);
        shoppingCartService.remove(wrapper);
        return R.success("操作成功");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> getOrderInfo(Integer page, Integer pageSize, HttpSession session){

        Page<Orders> ordersPage = new Page<>(page, pageSize);
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,uid);
        ordersService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }

    @GetMapping("/page")
    //后台数据
    public R<Page<Orders>> getOrderData(Integer page,Integer pageSize,Long number,String beginTime,String endTime){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(number != null,Orders::getNumber,number)
                        .between(StringUtils.isNotBlank(beginTime),Orders::getOrderTime,beginTime,endTime);
        ordersService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }

}
