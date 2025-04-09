package com.shiro.controller;

import com.shiro.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("myController")
public class MyController {
    @Autowired
    private UserService userService;
    //跳转到登录页面
    @GetMapping("login")
    public String login(){
        return "login";
    }
    @GetMapping("register")
    public String register(){
        return "register";
    }
    @PostMapping("userRegister")
    public String userRegister(@RequestParam("username") String username, @RequestParam("password") String password,Model model){
        //1.判断输入是否合法
        // 判断用户名和密码长度
        if (username.length() > 20 || password.length() > 20) {
            model.addAttribute("errorregisterMessage", "用户名和密码长度不能超过20个字符"); // 将注册失败的错误消息添加到Model中
            return "register"; // 返回到注册页面
        }

        // 判断用户名和密码是否符合规则
        if (!username.matches("^\\w+$") || !password.matches("^\\w+$")) {
            model.addAttribute("errorregisterMessage", "用户名和密码只能由字母、数字和下划线构成"); // 将注册失败的错误消息添加到Model中
            return "register"; // 返回到注册页面
        }
        //2.加密password
        //使用父类进行加密
        SimpleHash decode_password=new SimpleHash("Md5",password,"salt",3);
//        System.out.println("带盐的md5三次加密="+simpleHash.toHex());
        //3. 注册
        try {
            int key = userService.registerUser(username, String.valueOf(decode_password));
            System.out.print(key);
            if (key > 0) {
                userService.initialisingRoles();
                model.addAttribute("successregisterMessage", "注册成功"); // 将注册成功的提示信息添加到Model中
                return "login"; // 重定向到登录页面
            } else {
                model.addAttribute("errorregisterMessage", "该用户名已经注册"); // 将注册失败的错误消息添加到Model中
                return "register"; // 返回到注册页面
            }
        }catch (Exception e){
            model.addAttribute("errorregisterMessage", "该用户名已经注册"); // 将注册失败的错误消息添加到Model中
            return "register"; // 返回到注册页面
        }
    }
    @GetMapping("userLogin")
    public String userLogin(String username, String password,@RequestParam(defaultValue = "false") boolean rememberMe,HttpSession session,Model model){
        //1. 获取subject对象
        Subject subject = SecurityUtils.getSubject();
        //2. 封装请求数据到token对象中
        AuthenticationToken token=new UsernamePasswordToken(username,password,rememberMe);
        //3. 调用login方法进行登录认证
        try {
            subject.login(token);
//            return "登录成功";
            session.setAttribute("user", token.getPrincipal().toString());
            model.addAttribute("successloginMessage", "登录成功"); // 将注册成功的提示信息添加到Model中
            return "main"; // 重定向到主页面
//        } catch(AbstractMethodError e){
        }catch(Exception e){
//            e.printStackTrace();
            model.addAttribute("errorloginMessage", "用户名或密码错误"); // 将注册成功的提示信息添加到Model中
            return "login"; // 重定向到登录页面
        }
    }
    //登录认证验证cookie
    @GetMapping("userLoginRm")
    public String userLogin(HttpSession session){
        session.setAttribute("user","rememberMe");
        return "main";
    }
    //登录认证验证角色
    @RequiresRoles("admin")
    @GetMapping("userLoginRoles")
    @ResponseBody
    public String userLoginRoles() {
        System.out.println("登录验证角色");
        return "Verify role successfully";
    }
    //登录认证验证权限
    @RequiresPermissions("user:delete")
    @GetMapping("userPermissions")
    @ResponseBody
    public String userLoginPermissions() {
        System.out.println("登录认证验证权限");
        return "Verify permission successfully";
    }
    @GetMapping("save")
    public String save(){
        return "save";
    }

    @GetMapping("get")
    public String get(){
        return "get";
    }

    @RequiresRoles("admin")
    @GetMapping("delete")
    public String delete(){
        return "delete";
    }

    @GetMapping("main")
    public String main(){
        return "main";
    }
}
