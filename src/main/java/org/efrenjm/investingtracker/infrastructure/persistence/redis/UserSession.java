package org.efrenjm.investingtracker.infrastructure.persistence.redis;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.domain.model.user.User;
import org.efrenjm.investingtracker.domain.model.utils.SystemRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user-session")
public class UserSession {
    @Id private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private Set<SystemRole> roles;

    public Profile toProfile() {
        return new Profile(
                id,
                username,
                email,
                phoneNumber,
                firstName,
                middleName,
                lastName,
                profilePicture,
                roles);
    }

    public static UserSession fromProfile(Profile profile) {
        return new UserSession(
                profile.id(),
                profile.username(),
                profile.email(),
                profile.phoneNumber(),
                profile.firstName(),
                profile.middleName(),
                profile.lastName(),
                profile.profilePicture(),
                profile.roles());
    }

    public static UserSession fromUser(User user) {
        return new UserSession(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getProfilePicture(),
                user.getRoles());
    }
}
