package com.cwj.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.Employee;
import com.cwj.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 员工信息 前端控制器
 * </p>
 *
 * @author cwj
 * @since 2022-12-30
 */
@Slf4j
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //登陆请求
    @PostMapping("/employee/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        QueryWrapper<Employee> wrapper = new QueryWrapper<>();
        wrapper.eq("username", employee.getUsername());
        Employee flag = employeeService.getOne(wrapper);
        if (flag == null) {
            return R.error("该用户不存在");
        }

        //加密密码
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        if (!flag.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //状态验证
        Integer status = flag.getStatus();
        if (status != 1) {
            return R.error("该账户已被封禁 请注意日常使用行为");
        }

        //登陆成功
        request.getSession().setAttribute("employee", flag);
        return R.success(flag);
    }

    //登出请求
    @PostMapping("/employee/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");

        return R.success("success");
    }

    //添加员工
    @PostMapping("/employee")
    public R<String> addEmployee(HttpSession session, @RequestBody Employee employee) {
        Employee obj = (Employee) session.getAttribute("employee");
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //设置创建人
        //employee.setCreateUser(obj.getId());
        //设置修改人
        //employee.setUpdateUser(obj.getId());

        //保存
        boolean flag = employeeService.save(employee);

        if (!flag) {
            return R.error("未知错误！！");
        }
        return R.success("保存成功");
    }

    //修改员工信息
    @PutMapping("/employee")
    public R<String> updateStatus(HttpSession session, @RequestBody Employee employee) {

        Employee emp = (Employee) session.getAttribute("employee");
        if(!"admin".equals(emp.getUsername())){
            return R.error("权限不足！！");
        }
//        employee.setUpdateUser(emp.getId());
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    //处理分页
    @GetMapping("/employee/page")
    public R<Page<Employee>> pagination(Integer page, Integer pageSize, String name) {

        Page<Employee> pageObj = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageObj, wrapper);
        return R.success(pageObj);
    }

    //编辑页信息
    @GetMapping("/employee/{id}")
    public R<Employee> getEmpById(@PathVariable("id") String id) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getId, Long.parseLong(id));
        Employee emp = employeeService.getOne(wrapper);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("没有查询到该员工信息");
    }

}
