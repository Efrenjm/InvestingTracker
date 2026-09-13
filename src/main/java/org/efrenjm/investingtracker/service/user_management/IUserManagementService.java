package org.efrenjm.investingtracker.service.user_management;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.controller.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.dto.model.organization.OrganizationSummary;
import org.efrenjm.investingtracker.model.user.User;
import org.efrenjm.investingtracker.dto.model.user.PublicProfile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserManagementService {
	Mono<User> updateProfile(User user, ProfileUpdateRequestDTO updateRequest);

	Mono<Void> deleteUser(User userCredentials);

	Flux<PublicProfile> getFriends(User user);

	Mono<User> addFriend(User user, ObjectId friendId);

	Mono<User> removeFriend(User user, ObjectId friendToRemoveId);

	Flux<OrganizationSummary> getOrganizations(User user);

	Mono<User> joinOrganization(User user, ObjectId organizationId);

	Mono<User> quitOrganization(User user, ObjectId organizationId);
}
