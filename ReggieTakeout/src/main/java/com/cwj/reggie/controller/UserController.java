package com.cwj.reggie.controller;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cwj.reggie.common.R;
import com.cwj.reggie.entity.User;
import com.cwj.reggie.service.UserService;
import com.cwj.reggie.utils.Sample;
import com.cwj.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //发送短信验证码
    @PostMapping("/sendMsg")
    public R<String> send(@RequestBody User user, HttpSession session) throws Exception {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotBlank(phone)) {
            //随机生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4) + "";
            log.info("code：{}", code);
            //调用阿里云api发送短信
            if ("15871240437".equals(phone)) {
                Sample.sendMsg(code);
            }
            //将生成的验证码保存到session
            session.setAttribute(phone, code);
            return R.success("手机验证码发送成功");
        }

        return R.success("请填写正确的手机号码");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,Object> map,HttpSession session) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        String sessionCode = (String) session.getAttribute(phone);
        if(sessionCode == null || !sessionCode.equals(code)){
            return R.error("验证码错误");
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User user = userService.getOne(wrapper);
        if (user == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }

        if(user.getStatus() == 0){
            return R.error("该手机号已被禁用 请注意日常使用行为！！");
        }
        session.setAttribute("user",user);

        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("success");
    }

}
