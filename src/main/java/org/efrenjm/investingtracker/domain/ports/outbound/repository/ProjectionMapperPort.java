package org.efrenjm.investingtracker.domain.ports.outbound.repository;

import org.efrenjm.investingtracker.domain.dto.PublicProfile;
import org.efrenjm.investingtracker.domain.dto.Profile;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.PublicProfileProjection;
import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.SelfProfileProjection;

public interface ProjectionMapperPort {
	PublicProfile toPublicProfile(PublicProfileProjection projection);
	Profile toSelfProfile(SelfProfileProjection projection);
}
