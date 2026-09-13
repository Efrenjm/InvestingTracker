package org.efrenjm.investingtracker.model.profile;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Document(collection = "profiles")
public class Profile {
    @Id
    @Field("_id")
    private ObjectId id;

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

    @DBRef
    @JsonManagedReference
    private List<Organization> organizations;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("last_login")
    private Date lastLogin;

    @DBRef
    private List<Profile> friends;
}
