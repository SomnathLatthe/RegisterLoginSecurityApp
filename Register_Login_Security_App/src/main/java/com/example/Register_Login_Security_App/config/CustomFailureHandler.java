package com.example.Register_Login_Security_App.config;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.repository.UserRepo;
import com.example.Register_Login_Security_App.service.UserService;
import com.example.Register_Login_Security_App.service.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        User user = userRepo.findByEmail(email);
        if(user!=null)
        {
            if(user.isEnable())
            {
                if(user.isAccountNonLocked())
                {
                    if(user.getFailedAttempt()< UserServiceImpl.ATTEMPT_TIME-1)
                    {
                        userService.increaseFailedAttempt(user);
                    }
                    else {
                        userService.lock(user);
                        exception=new LockedException("Your account is locked! failed attempt 3");
                    }
                }else if(!user.isAccountNonLocked()){
                    if(userService.unlockAccountTimeExpired(user))
                    {
                        exception=new LockedException("Account is unlocked! Please try to login.");
                    }
                    else {
                        exception=new LockedException("Account is locked! Please try after sometime.");
                    }
                }
            }
            else {
                exception=new LockedException("Account is inactive... verify account !");
            }
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
