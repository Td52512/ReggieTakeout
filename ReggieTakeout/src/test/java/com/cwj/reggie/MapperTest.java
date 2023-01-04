package com.cwj.reggie;

import com.cwj.reggie.mapper.EmployeeMapper;
import com.cwj.reggie.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapperTest {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Test
    public void test1(){
        System.out.println(employeeMapper.selectList(null).get(0));
    }
}
