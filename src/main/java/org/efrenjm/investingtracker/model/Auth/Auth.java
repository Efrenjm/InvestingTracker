package org.efrenjm.investingtracker.model.Auth;

import lombok.*;
import org.efrenjm.investingtracker.model.Profile.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Document(collection = "auth")
public class Auth {
    @Id
    @Field("_id")
    private String id;

    private String username;

    private String email;

    @Field("phone_number")
    private String phoneNumber;

    private String password;

    private Profile profile;
}
