package com.example.Register_Login_Security_App.repository;

import com.example.Register_Login_Security_App.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User,Integer>
{
    public User findByEmail(String email);
    public User findByVerificationCode(String code);
    @Query("update User u set failedAttempt=?1 where email=?2")
    @Modifying
    public void updateFailedAttempt(int attempt,String email);

    public User findByEmailAndMobileNo(String email,String mobileNo);
}
