package org.efrenjm.investingtracker.model.Auth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Document(collection = "auth")
public class Auth {
    @Id
    @Getter private String id;
    @Getter private String username;
    @Getter private String email;
    @Getter private String phoneNumber;
    private String password;
    private List<Token> activeTokens;
}
