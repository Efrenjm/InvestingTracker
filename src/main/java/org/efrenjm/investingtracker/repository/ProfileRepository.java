package org.efrenjm.investingtracker.repository;

import org.efrenjm.investingtracker.model.profile.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, String>
{ }
