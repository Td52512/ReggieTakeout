package com.cwj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cwj.reggie.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void updateDefaultByUid(Long uid);

    void setDefaultAddress(Long id);
}
