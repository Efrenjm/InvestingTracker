package org.efrenjm.investingtracker.service.models.profile;

import org.efrenjm.investingtracker.model.Profile.Profile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileService {
    Flux<Profile> getAllProfiles();

    Mono<Profile> getProfileById(String id);

    Mono<Profile> createProfile(Profile profile);

    Mono<Profile> updateProfile(String id, Profile profile);

    Mono<Void> deleteProfile(String id);
}
