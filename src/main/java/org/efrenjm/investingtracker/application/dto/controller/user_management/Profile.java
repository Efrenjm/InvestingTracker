package org.efrenjm.investingtracker.application.dto.controller.user_management;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.efrenjm.investingtracker.domain.model.user.User;

@RequiredArgsConstructor
@Getter
@ToString
public class Profile {
    String id;
    String email;
    String phoneNumber;
    String firstName;
    String middleName;
    String lastName;
    String profilePicture;

    public Profile(User user) {
        id = user.getId();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        firstName = user.getFirstName();
        middleName = user.getMiddleName();
        lastName = user.getLastName();
        profilePicture = user.getProfilePicture();
    }
}
