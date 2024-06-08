package com.example.Register_Login_Security_App.controller;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @ModelAttribute
    public void commonUser(Principal p, Model model)
    {
        if(p!=null)
        {
            String email=p.getName();
            User user=userRepo.findByEmail(email);
            model.addAttribute("user",user);
        }
    }

    @GetMapping("/profile")
    public String profile()
    {
        return "user_profile";
    }
}
