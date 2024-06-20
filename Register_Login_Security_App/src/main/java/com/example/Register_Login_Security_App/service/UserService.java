package com.example.Register_Login_Security_App.service;

import com.example.Register_Login_Security_App.entity.User;

public interface UserService {
    public User saveUser(User user,String url);

    public void removeSessionMsg();

    public void sendMail(User user,String url);
    public boolean verifyAccount(String verificationCode);
    public void increaseFailedAttempt(User user);
    public void resetAttempt(String email);
    public void lock(User user);
    public boolean unlockAccountTimeExpired(User user);
}
