package com.cwj.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
    int updateDefaultByUid(Long uid);

    int setDefaultAddress(Long id);
}
