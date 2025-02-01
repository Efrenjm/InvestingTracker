package org.efrenjm.investingtracker.service.user_management;

import org.efrenjm.investingtracker.dto.authentication.RegisterRequestDTO;
import org.efrenjm.investingtracker.model.Auth.Auth;
import reactor.core.publisher.Mono;

public interface IUserManagementService {
	Mono<String> login(String username, String password);
	Mono<Auth> register(RegisterRequestDTO user);
}
