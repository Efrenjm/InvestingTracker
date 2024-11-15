package org.efrenjm.investingtracker.service.models.profile;

import org.efrenjm.investingtracker.model.Profile.Profile;
import org.efrenjm.investingtracker.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Flux<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public Mono<Profile> getProfileById(String id) {
        return profileRepository.findById(id);
    }

    @Override
    public Mono<Profile> createProfile(Profile profile) {
        return profileRepository.insert(profile);
    }

    @Override
    public Mono<Profile> updateProfile(String id, Profile profile) {
        profile.setId(id);
        return profileRepository.save(profile);
    }

    @Override
    public Mono<Void> deleteProfile(String id) {
        return profileRepository.deleteById(id);
    }
}
