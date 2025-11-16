package com.minh.notify_service.service;

public interface EmailService {

    void sendEmail(String email, String title, String content);
}
