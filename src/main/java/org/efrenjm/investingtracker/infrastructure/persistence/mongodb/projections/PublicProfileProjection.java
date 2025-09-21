package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

@SuppressWarnings("java:S100")
public interface PublicProfileProjection {
	String get_id();
	String getUsername();
	String getEmail();
	String getPhoneNumber();
	String getFirstName();
	String getMiddleName();
	String getLastName();
	String getProfilePicture();
}
