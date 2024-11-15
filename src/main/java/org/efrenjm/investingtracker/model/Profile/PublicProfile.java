package org.efrenjm.investingtracker.model.Profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.time.ZonedDateTime;


@Getter @Setter
@ToString
@NoArgsConstructor
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
