package org.efrenjm.investingtracker.service.user_management;

import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.Profile.Profile;
import reactor.core.publisher.Mono;

public interface IAuthenticationService {
	Mono<String> login(String email, String phone, String password);

	Mono<Profile> register(RegisterRequestDTO user);
}
