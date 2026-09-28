package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

class PublicProfileProjectionTest {
    @Test
    void getIdMapsTheMongoIdWithoutChangingItsFieldName() throws ReflectiveOperationException {
        ObjectId id = new ObjectId("000000000000000000000001");
        Map<String, Object> row =
                Map.of(
                        "_id", id,
                        "username", "sample-user",
                        "email", "user@example.invalid",
                        "phoneNumber", "0000000000",
                        "firstName", "Sample",
                        "middleName", "Example",
                        "lastName", "User",
                        "profilePicture", "https://example.invalid/avatar.png");
        PublicProfileProjection projection =
                new SpelAwareProxyProjectionFactory()
                        .createProjection(PublicProfileProjection.class, row);

        Method getter = PublicProfileProjection.class.getMethod("getId");
        assertEquals(id.toHexString(), getter.invoke(projection));
    }
}
