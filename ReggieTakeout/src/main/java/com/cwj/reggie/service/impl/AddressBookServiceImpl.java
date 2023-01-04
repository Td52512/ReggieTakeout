package com.cwj.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cwj.reggie.entity.AddressBook;
import com.cwj.reggie.mapper.AddressBookMapper;
import com.cwj.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void updateDefaultByUid(Long uid) {
        addressBookMapper.updateDefaultByUid(uid);
    }

    @Override
    public void setDefaultAddress(Long id) {
        addressBookMapper.setDefaultAddress(id);
    }
}
