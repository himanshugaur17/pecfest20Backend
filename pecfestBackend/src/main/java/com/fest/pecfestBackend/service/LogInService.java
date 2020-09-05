package com.fest.pecfestBackend.service;

import com.fest.pecfestBackend.entity.User;
import com.fest.pecfestBackend.repository.UserRepo;
import com.fest.pecfestBackend.request.LogInRequest;
import com.fest.pecfestBackend.request.ResetPasswordRequest;
import com.fest.pecfestBackend.response.WrapperResponse;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

@Service
public class LogInService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailSenderService emailSenderService;

    public WrapperResponse authenticateUser(LogInRequest logInRequest){
        String hashedPassword=Hashing.sha512().hashString(logInRequest.getPassword(), StandardCharsets.UTF_8).toString();
        User user=userRepo.findByEmailAndPassword(logInRequest.getEmailId(),hashedPassword);
        if(Objects.isNull(user))
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Oops wrong password/email!").build();
        else
            return WrapperResponse.builder().build();
    }
    public WrapperResponse sendVerificationCode(String emailId) {
        Random rand = new Random();
        int verificationCode = rand.nextInt(900000) + 100000;
        emailSenderService.sendEmail(createEmailMessage(verificationCode,emailId));
        return WrapperResponse.builder().build();
    }

    private SimpleMailMessage createEmailMessage(int verificationCode,String emailId) {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(emailId);
        message.setSubject("Reset Password");
        message.setText("The verification code for resetting the password is: "+verificationCode);
        return message;
    }

    public WrapperResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        return WrapperResponse.builder().build();
    }
}
