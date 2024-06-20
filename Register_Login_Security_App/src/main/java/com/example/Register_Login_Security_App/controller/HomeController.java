package com.example.Register_Login_Security_App.controller;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.repository.UserRepo;
import com.example.Register_Login_Security_App.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @ModelAttribute
    public void commonUser(Principal p,Model model)
    {//fetch user each time,when it comes in this controller
        if(p!=null)
        {
            String email=p.getName();
            User user=userRepo.findByEmail(email);
            model.addAttribute("user",user);
        }
    }

    @GetMapping("/")
    public String index()
    {
        return "index";
    }
    @GetMapping("/register")
    public String register()
    {
        return "register";
    }
    @GetMapping("/signin")
    public String signin()
    {
        return "login";
    }
    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user, HttpSession session, HttpServletRequest request)
    {
        String url=request.getRequestURL().toString();
//        System.out.println("Url:"+url+"  request.getServletPath(): "+request.getServletPath());
        url=url.replace(request.getServletPath(),"");
        System.out.println("after replacing getServletPath:"+url);

        User newUser=userService.saveUser(user,url);
        if(newUser!=null)
            session.setAttribute("msg","User registered Successfully !");
        else
            session.setAttribute("msg","Something went Wrong !");

        return "redirect:/register";
    }

    @GetMapping("/verify")
    public String verifyAccount(@Param("code") String code,Model model)
    {
        boolean flag= userService.verifyAccount(code);
        if(flag)
        {
            model.addAttribute("msg","Verified Successfully !");
        }
        else
        {
            model.addAttribute("msg","may be verification code is incorrect or already verified !");
        }
        return "message";
    }


    @GetMapping("/modifyForgotPass")
    public String modifyForgotPass()
    {
        return "user/modifyForgotPass";
    }

    @PostMapping("/forgotPass")
    public String forgotPass(@RequestParam("email") String email,
                             @RequestParam("mobileNo") String mobileNo,HttpSession session)
    {
        User user=userRepo.findByEmailAndMobileNo(email,mobileNo);
        if(user!=null)
        {
            return "redirect:/modifyResetPass/"+user.getId();
        }
        session.setAttribute("msg","Invalid Email & Mobile ");
        return "redirect:/modifyForgotPass";
    }

    @GetMapping("/modifyResetPass/{id}")
    public String modifyResetPass(@PathVariable int id,Model model)
    {
        model.addAttribute("id",id);
        return "user/modifyResetPass";
    }

    @PostMapping("/resetPass")
    public String resetPass(@RequestParam("newPass") String newPass,
                            @RequestParam("confirmPass") String confirmPass,@RequestParam("id") Integer id,HttpSession session)
    {
        User user=userRepo.findById(id).get();
        String encrytPwd= bCryptPasswordEncoder.encode(newPass);
        user.setPassword(encrytPwd);
        User pwdUpdateUser = userRepo.save(user);

        session.setAttribute("msg","Password changed successfully !");

        return "redirect:/modifyForgotPass";
    }
}
