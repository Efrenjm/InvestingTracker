//package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.adapter;
//
//import org.efrenjm.investingtracker.domain.dto.PublicProfile;
//import org.efrenjm.investingtracker.domain.dto.Profile;
//import org.efrenjm.investingtracker.domain.ports.outbound.repository.ProjectionMapperPort;
//import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.PublicProfileProjection;
//import org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections.SelfProfileProjection;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ProjectionAdapter implements ProjectionMapperPort {
//	@Override
//	public PublicProfile toPublicProfile(PublicProfileProjection projection) {
//		return new PublicProfile(
//				projection.get_id(),
//				projection.getUsername(),
//				projection.getEmail(),
//				projection.getPhoneNumber(),
//				projection.getFirstName(),
//				projection.getMiddleName(),
//				projection.getLastName(),
//				projection.getProfilePicture()
//		);
//	}
//
//	@Override
//	public Profile toSelfProfile(SelfProfileProjection projection) {
//		return new Profile(
//				projection.get_id(),
//				projection.getUsername(),
//				projection.getEmail(),
//				projection.getPhoneNumber(),
//				projection.getFirstName(),
//				projection.getMiddleName(),
//				projection.getLastName(),
//				projection.getProfilePicture(),
//				projection.getRoles(),
//				projection.getPreferences()
//		);
//	}
//}
