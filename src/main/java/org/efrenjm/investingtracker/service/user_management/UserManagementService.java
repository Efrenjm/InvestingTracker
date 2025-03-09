package org.efrenjm.investingtracker.service.user_management;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.user_management.ProfileUpdateRequestDTO;
import org.efrenjm.investingtracker.exception.user_management.OrganizationNotFoundException;
import org.efrenjm.investingtracker.exception.user_management.ProfileNotFoundException;
import org.efrenjm.investingtracker.model.auth_credentials.AuthCredentials;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.efrenjm.investingtracker.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserManagementService implements IUserManagementService {
	private final ProfileRepository profileRepository;
	private final OrganizationRepository organizationRepository;

	public Mono<Profile> updateProfile(Profile userProfile, ProfileUpdateRequestDTO updateRequest) {
		userProfile.setFirstName(updateRequest.getFirstName());
		userProfile.setMiddleName(updateRequest.getMiddleName());
		userProfile.setLastName(updateRequest.getLastName());
		userProfile.setProfilePicture(updateRequest.getProfilePicture());
		userProfile.setUpdatedAt(new Date());

		return profileRepository.save(userProfile); /* TODO: catch error */
	}

	/* TODO: Delete personal organization and authentication method */
	public Mono<Void> deleteUser(AuthCredentials userCredentials) {
		Profile userProfile = userCredentials.getProfile();
		ObjectId userId = userProfile.getId();
		List<Organization> organizations = userCredentials.getProfile().getOrganizations();
		List<Profile> friends = userCredentials.getProfile().getFriends();

		friends.forEach(friend -> friend.getFriends().remove(userProfile));

		organizations.forEach(organization -> {
			if (organization.getMembers().size() == 1)
				organizationRepository.delete(organization).subscribe(); /* TODO: Delete accounts */
			else
				organization.getMembers()
						.removeIf(member -> member.getUser().getId().equals(userId));
		});

		return profileRepository.existsById(userId)
				.switchIfEmpty(Mono.error(new ProfileNotFoundException(userId.toString())))
				.then(profileRepository.deleteById(userId));
	}

	public Flux<Profile> getFriends(Profile userProfile) {
		return profileRepository.findAllById(userProfile.getFriends().stream()
				.map(Profile::getId)
				.collect(Collectors.toList()));
	}

	public Mono<Profile> addFriend(Profile userProfile, ObjectId friendId) {
		return profileRepository.findById(friendId)
				/* TODO: Add logic to invite friends */
				.switchIfEmpty(Mono.error(new ProfileNotFoundException(friendId.toString())))
				.flatMap(friendProfile -> {
					userProfile.getFriends().add(friendProfile);
					return profileRepository.save(userProfile);
				});
	}

	public Mono<Profile> removeFriend(Profile userProfile, ObjectId friendToRemoveId) {
		List<Profile> updatedFriendsList = userProfile.getFriends().stream()
				.filter(friend -> !friend.getId().equals(friendToRemoveId))
				.toList();

		userProfile.setFriends(updatedFriendsList);

		return profileRepository.save(userProfile);
	}

	public Flux<Organization> getOrganizations(Profile userProfile) {
		return organizationRepository.findAllById(userProfile.getOrganizations().stream()
				.map(Organization::getId)
				.collect(Collectors.toList()));
	}

	public Mono<Profile> joinOrganization(Profile userProfile, ObjectId organizationId) {
		return organizationRepository.findById(organizationId)
				.switchIfEmpty(Mono.error(new OrganizationNotFoundException(organizationId.toString())))
				.flatMap(organization -> {
					userProfile.getOrganizations().add(organization);
					return profileRepository.save(userProfile);
				});
	}

	public Mono<Profile> quitOrganization(Profile userProfile, ObjectId organizationId) {
		List<Organization> updatedOrganizationList = userProfile.getOrganizations().stream()
				.filter(organization -> !organization.getId().equals(organizationId))
				.toList();

		userProfile.setOrganizations(updatedOrganizationList);

		return profileRepository.save(userProfile);
	}
}
