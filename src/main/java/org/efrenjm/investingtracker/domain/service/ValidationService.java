package org.efrenjm.investingtracker.domain.service;

import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.inbound.ValidationPort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationService implements ValidationPort
{
	private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?])(?=\\S+$).{8,}$";
	private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	private final org.efrenjm.investingtracker.domain.ports.outbound.utils.ValidationPort validationOperations;

	public boolean isValidEmail(String possibleEmail)
	{
		return validationOperations.isValidEmail(possibleEmail);
	}

	public boolean isValidPhone(String possiblePhone)
	{
		return validationOperations.isValidPhone(possiblePhone);
	}

	public boolean isValidPassword(String password)
	{
		return pattern.matcher(password).matches();
	}
}
