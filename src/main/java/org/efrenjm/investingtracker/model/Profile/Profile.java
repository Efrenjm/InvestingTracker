package org.efrenjm.investingtracker.model.Profile;

import lombok.*;
import org.efrenjm.investingtracker.model.Organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "profiles")
public class Profile {
    @Id
    @Field("_id")
    private String id;

    private String username;

    private String email;

    @Field("phone_number")
    private String phoneNumber;

    @Field("first_name")
    private String firstName;

    @Field("middle_name")
    private String middleName;

    @Field("last_name")
    private String lastName;

    @Field("profile_picture")
    private String profilePicture;

    private List<Organization> organizations;

    private Boolean active;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;

    @Field("last_login")
    private ZonedDateTime lastLogin;

    private List<PublicProfile> friends;
}
