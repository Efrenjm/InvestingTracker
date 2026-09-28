package org.efrenjm.investingtracker.infrastructure.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.validator.routines.EmailValidator;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.ValidationPort;
import org.springframework.stereotype.Component;

@Component
public class ValidationOperations implements ValidationPort {
    @Override
    public boolean isValidEmail(String possibleEmail) {
        return EmailValidator.getInstance().isValid(possibleEmail);
    }

    @Override
    public boolean isValidPhone(String possiblePhone) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phone =
                    phoneNumberUtil.parse(
                            possiblePhone,
                            Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
            return phoneNumberUtil.isValidNumber(phone);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
