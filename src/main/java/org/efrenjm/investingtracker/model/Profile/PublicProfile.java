package org.efrenjm.investingtracker.model.Profile;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PublicProfile {
    @Id
    private String id;
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private Boolean active;
    private ZonedDateTime lastLogin;
}
