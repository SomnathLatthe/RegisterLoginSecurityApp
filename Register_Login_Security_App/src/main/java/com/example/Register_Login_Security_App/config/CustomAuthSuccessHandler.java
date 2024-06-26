package com.example.Register_Login_Security_App.config;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        CustomUser customUser=(CustomUser)authentication.getPrincipal();
        User user = customUser.getUser();
        if(user!=null)
        {
            userService.resetAttempt(user.getEmail());
        }
        if(roles.contains("ROLE_ADMIN"))
        {
            response.sendRedirect("/admin/profile");
        }
        else
        {
            response.sendRedirect("/user/profile");
        }
    }
}
