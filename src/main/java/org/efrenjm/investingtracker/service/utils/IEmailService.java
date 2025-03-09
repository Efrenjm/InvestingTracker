package org.efrenjm.investingtracker.service.utils;

public interface IEmailService {
	void sendVerificationEmail(String toEmail, String verificationToken);
}
