package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.BaseModel;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseMongoEntity {
    @Id
    @Field("_id")
    protected ObjectId id;

    @CreatedDate
    @Field("created_at")
    protected Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    protected Instant updatedAt;

    public static <B extends BaseMongoEntityBuilder<?, ?>, D extends BaseModel>
            B populateBaseEntityFields(B builder, D domainObject) {
        if (domainObject == null) {
            return builder;
        }

        builder.id(MongoUtils.idToEntity(domainObject.getId()))
                .createdAt(
                        domainObject.getCreatedAt() != null
                                ? domainObject.getCreatedAt().toInstant()
                                : null)
                .updatedAt(
                        domainObject.getUpdatedAt() != null
                                ? domainObject.getUpdatedAt().toInstant()
                                : null);
        return builder;
    }

    public <B extends BaseModel.BaseModelBuilder<?, ?>> B populateBaseDomainFields(B builder) {
        if (id == null) {
            id = new ObjectId();
        }

        builder.id(MongoUtils.idToDomain(id))
                .createdAt(createdAt != null ? Date.from(createdAt) : null)
                .updatedAt(updatedAt != null ? Date.from(updatedAt) : null);
        return builder;
    }
}
