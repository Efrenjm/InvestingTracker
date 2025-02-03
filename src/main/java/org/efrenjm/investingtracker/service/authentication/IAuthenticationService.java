package org.efrenjm.investingtracker.service.authentication;

import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.profile.Profile;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
	Mono<String> login(String email, String phone, String password);

	Mono<Profile> register(RegisterRequestDTO user);
}
