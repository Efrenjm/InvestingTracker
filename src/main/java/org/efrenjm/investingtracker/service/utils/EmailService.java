package org.efrenjm.investingtracker.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService{
	private final JavaMailSender mailSender;

	public void sendVerificationEmail(String toEmail, String verificationToken) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(toEmail);
		message.setSubject("Please verify your email address");
		message.setText("Use the following code to verify your email: " + verificationToken);

		mailSender.send(message);
	}
}
