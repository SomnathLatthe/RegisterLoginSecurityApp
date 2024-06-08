package com.example.Register_Login_Security_App.repository;

import com.example.Register_Login_Security_App.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Integer>
{
    public User findByEmail(String email);
    public User findByVerificationCode(String code);
}
