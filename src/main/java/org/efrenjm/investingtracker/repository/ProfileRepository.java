package org.efrenjm.investingtracker.repository;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.model.profile.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProfileRepository extends ReactiveMongoRepository<Profile, ObjectId> {
//	Flux<Profile> findAllById(Iterable<ObjectId> ids);
}
