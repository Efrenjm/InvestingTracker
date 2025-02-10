package org.efrenjm.investingtracker.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Value("${app.base-url}")
	private String baseUrl;

	@Autowired
	private JavaMailSender mailSender;

	public void sendVerificationEmail(String toEmail, String verificationToken) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(toEmail);
		message.setSubject("Please verify your email address");
		message.setText("Use the following code to verify your email: " + verificationToken);

		mailSender.send(message);
	}
}
