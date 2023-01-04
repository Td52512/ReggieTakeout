package com.cwj.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cwj.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 员工信息 Mapper 接口
 * </p>
 *
 * @author cwj
 * @since 2022-12-30
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
