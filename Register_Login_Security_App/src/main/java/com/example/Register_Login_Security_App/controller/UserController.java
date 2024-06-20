package com.example.Register_Login_Security_App.controller;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
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
        return "user/user_profile";
    }

    @GetMapping("/modifyPass")
    public String modifyPassword()
    {
        return "user/modifyPass";
    }
    @PostMapping("/updatePass")
    public String updatePassword(Principal p, @RequestParam("oldPass") String oldPass,
                                 @RequestParam("newPass") String newPass, HttpSession session)
    {
        String email=p.getName();
        User loginUser=userRepo.findByEmail(email);
        boolean passFlag=bCryptPasswordEncoder.matches(oldPass, loginUser.getPassword());
        if(passFlag)
        {
            loginUser.setPassword(bCryptPasswordEncoder.encode(newPass));
            User updatedPassUser=userRepo.save(loginUser);

            session.setAttribute("msg","Password Updated Successfully !");
            /*if(updatedPassUser!=null)
            {
                session.setAttribute("msg","Password Updated Successfully !");
            }
            else {
                session.setAttribute("msg","Something wrong on server...");
            }*/
        }
        else {
            session.setAttribute("msg","Old password is incorrect...");
        }
        return "redirect:/user/modifyPass";
    }

}
