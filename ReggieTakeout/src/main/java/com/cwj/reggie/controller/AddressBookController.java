package com.cwj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.AddressBook;
import com.cwj.reggie.entity.User;
import com.cwj.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    //获取地址信息
    public R<List<AddressBook>> getAddressBookInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, uid);
        List<AddressBook> list = addressBookService.list(wrapper);
        return R.success(list);
    }

    @PostMapping
    //新增地址
    public R<String> addAddress(@RequestBody AddressBook addressBook, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        addressBook.setUserId(uid);
        addressBookService.save(addressBook);

        return R.success("添加成功");
    }

    @PutMapping("/default")
    //修改默认地址
    @Transactional
    public R<String> setDefaultAddress(@RequestBody Map<String, Object> map, HttpSession session) {
        Long id = Long.parseLong(map.get("id").toString());

        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        addressBookService.updateDefaultByUid(uid);
        addressBookService.setDefaultAddress(id);
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    //获取修改信息
    public R<AddressBook> getUpdateInfo(@PathVariable("id") Long id){
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        AddressBook addressBook = addressBookService.getById(id);
        log.info("标签：{}",addressBook.getLabel());
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("操作成功");
    }

    //获取默认地址
    @GetMapping("/default")
    public R<AddressBook> getAddressByUid(HttpSession session){
        User user = (User) session.getAttribute("user");
        Long uid = user.getId();
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,uid)
                .eq(AddressBook::getIsDefault,"1");
        AddressBook addressBook = addressBookService.getOne(wrapper);
        return R.success(addressBook);
    }

}
