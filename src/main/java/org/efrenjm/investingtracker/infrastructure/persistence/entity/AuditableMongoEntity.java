package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class AuditableMongoEntity extends BaseMongoEntity
{
	@Field("created_by") protected ObjectId createdBy;
	@Field("updated_by") protected ObjectId updatedBy;

	public static <B extends AuditableMongoEntityBuilder<?, ?>, D extends AuditableModel> B populateAuditableEntityFields(B builder, D domainObject)
	{
		if (domainObject == null)
			return builder;

		builder
				.createdBy(MongoUtils.idToEntity(domainObject.getCreatedBy()))
				.updatedBy(MongoUtils.idToEntity(domainObject.getUpdatedBy()));
		return populateBaseEntityFields(builder, domainObject);
	}

	public <B extends AuditableModel.AuditableModelBuilder<?, ?>> B populateAuditableDomainFields(B builder)
	{
		builder
				.createdBy(MongoUtils.idToDomain(createdBy))
				.updatedBy(MongoUtils.idToDomain(updatedBy));
		return populateBaseDomainFields(builder);
	}
}
