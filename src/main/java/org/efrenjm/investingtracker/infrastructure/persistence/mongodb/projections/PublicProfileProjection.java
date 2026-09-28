package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import org.springframework.beans.factory.annotation.Value;

public interface PublicProfileProjection {
    @Value("#{target['_id']}")
    String getId();

    String getUsername();

    String getEmail();

    String getPhoneNumber();

    String getFirstName();

    String getMiddleName();

    String getLastName();

    String getProfilePicture();
}
