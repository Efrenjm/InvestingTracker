package org.efrenjm.investingtracker.service.user_management;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.profile.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserManagementService {
	Mono<Profile> updateProfile(Profile userProfile, ProfileUpdateRequestDTO updateRequest);

	Mono<Void> deleteUser(AuthCredentials userCredentials);

	Flux<Profile> getFriends(Profile userProfile);

	Mono<Profile> addFriend(Profile userProfile, ObjectId friendId);

	Mono<Profile> removeFriend(Profile userProfile, ObjectId friendToRemoveId);

	Flux<Organization> getOrganizations(Profile userProfile);

	Mono<Profile> joinOrganization(Profile userProfile, ObjectId organizationId);

	Mono<Profile> quitOrganization(Profile userProfile, ObjectId organizationId);
}
