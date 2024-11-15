package org.efrenjm.investingtracker.model.Auth;

import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Token {
    public String token;
    public ZonedDateTime expiracyDate;
}
