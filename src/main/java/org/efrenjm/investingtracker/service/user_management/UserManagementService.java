package org.efrenjm.investingtracker.service.user_management;

import lombok.AllArgsConstructor;
import org.efrenjm.investingtracker.exception.ResourceNotFound;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserManagementService implements IUserManagementService {
	private final ProfileRepository profileRepository;

	public Mono<Profile> findUser(String userId) {
		return profileRepository.findById(userId)
				.switchIfEmpty(Mono.error(new ResourceNotFound("Profile with id " + userId + " not found.")));
	}
}
