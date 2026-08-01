package org.efrenjm.investingtracker.domain.service;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.model.user.CodeUsage;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.user.exceptions.*;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.efrenjm.investingtracker.domain.ports.outbound.security.PasswordEncoderPort;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserDomainService
{
	private final PasswordEncoderPort passwordEncoder;
	private final ValidationPort validationService;
	private final UserVerificationService userVerificationService;

	public User createUser(String username)
	{
		Date now = new Date();

		User newUser = User.builder()
				.username(username)
				.active(false)
				.createdAt(now)
				.updatedAt(now)
				.build();

		CodeUsage codeUsage = validationService.isValidEmail(username)
		                      ? CodeUsage.EMAIL_VERIFICATION
		                      : CodeUsage.PHONE_VERIFICATION;

		newUser.setVerificationRequest(userVerificationService.createRequest(codeUsage, username));
		return newUser;
	}

	public User resetUnverifiedPassword(User user, String newPassword)
	{
		if (!validationService.isValidPassword(newPassword))
		{
			throw new InvalidPasswordException();
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdatedAt(new Date());
		return user;
	}

	public User updateCredential(User user, CodeUsage codeUsage, String credential)
	{
		switch (codeUsage)
		{
			case EMAIL_VERIFICATION:
				if (!validationService.isValidEmail(credential))
				{
					throw new InvalidEmailException(credential);
				}
				break;
			case PHONE_VERIFICATION:
				if (!validationService.isValidPhone(credential))
				{
					throw new InvalidPhoneNumberException(credential);
				}
				break;
			case PASSWORD_RESET:
				String encodedPassword = passwordEncoder.encode(credential);
				if (!validationService.isValidPassword(credential))
				{
					throw new InvalidPasswordException();
				}
				if (user.getPassword() != null && user.getPassword().equals(encodedPassword))
				{
					throw new ReusedPasswordException();
				}
				credential = encodedPassword;
				break;
		}

		user.setVerificationRequest(userVerificationService.createRequest(codeUsage, credential));
		return user;
	}

	public User updatePassword(User user, String newPassword, String oldPassword)
	{
		if (user.getPassword() != null && !user.getPassword().isBlank())
		{
			if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPassword()))
			{
				throw new InvalidOldPasswordException();
			}
			if (passwordEncoder.matches(newPassword, user.getPassword()))
			{
				throw new ReusedPasswordException();
			}
		}

		if (!validationService.isValidPassword(newPassword))
		{
			throw new InvalidPasswordException();
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		user.setUpdatedAt(new Date());
		return user;
	}
}
