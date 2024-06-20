package com.example.Register_Login_Security_App.service;

import com.example.Register_Login_Security_App.entity.User;
import com.example.Register_Login_Security_App.repository.UserRepo;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public User saveUser(User user,String url) {
        String pwd=bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(pwd);
        user.setRole("ROLE_USER");
        user.setEnable(false);
        user.setVerificationCode(UUID.randomUUID().toString());

        user.setAccountNonLocked(true);
        user.setLockTime(null);
        user.setFailedAttempt(0);

        User newuser=userRepo.save(user);
        if(newuser!=null)
        {
            sendMail(newuser,url);
        }
        return newuser;
    }

    @Override
    public void removeSessionMsg() {
        HttpSession session=((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        session.removeAttribute("msg");
    }

    @Override
    public void sendMail(User user, String url) {
        String from="somnathlatthe@gmail.com";
        String to=user.getEmail();
        String subject="Account Verification";
        String sitUrl=url+"/verify?code="+user.getVerificationCode();
        String content="Dear "+user.getName()+",<br> Please click link below to Verify your registration:<br>" +
                "<h3><a href="+sitUrl+">VERIFY</a></h3> Thank you,<br> splatthe.";

//        System.out.println("in userserviceimpl sendMail to: "+to+" url:"+url);
        try
        {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom(from,"Becoder");
            helper.setTo(to);
            helper.setSubject(subject);

            helper.setText(content,true);
            mailSender.send(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyAccount(String verificationCode) {
        User user=userRepo.findByVerificationCode(verificationCode);
        if(user==null)
        {
            return false;
        }
        else
        {
            user.setEnable(true);
            user.setVerificationCode(null);
            userRepo.save(user);
            return true;
        }
        //return false;
    }

//    private static final long lock_duration_time=24*60*60*1000;
    private static final long lock_duration_time=120000;//2 min
    public static final long ATTEMPT_TIME=3;

    @Override
    public void increaseFailedAttempt(User user) {
        int attempt=user.getFailedAttempt()+1;
        userRepo.updateFailedAttempt(attempt,user.getEmail());
    }

    @Override
    public void resetAttempt(String email) {
        userRepo.updateFailedAttempt(0,email);
    }

    @Override
    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepo.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {
        long lockTimeIntMills=user.getLockTime().getTime();
        long currentTimeMillis=System.currentTimeMillis();
        if(lockTimeIntMills+lock_duration_time < currentTimeMillis)
        {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepo.save(user);
            return true;
        }

        return false;
    }
}
