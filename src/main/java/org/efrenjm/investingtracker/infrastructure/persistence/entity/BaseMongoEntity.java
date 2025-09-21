package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.BaseModel;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseMongoEntity
{
	@Id
	@Field("_id") protected ObjectId id;
	@Field("created_at") protected Date createdAt;
	@Field("updated_at") protected Date updatedAt;

	public static <B extends BaseMongoEntityBuilder<?, ?>, D extends BaseModel> B populateBaseEntityFields(B builder, D domainObject)
	{
		if (domainObject == null)
			return builder;

		builder
				.id(MongoUtils.idToEntity(domainObject.getId()))
				.createdAt(domainObject.getCreatedAt())
				.updatedAt(domainObject.getUpdatedAt());
		return builder;
	}

	public <B extends BaseModel.BaseModelBuilder<?, ?>> B populateBaseDomainFields(B builder)
	{
		if (id == null)
			id = new ObjectId();

		builder
				.id(MongoUtils.idToDomain(id))
				.createdAt(createdAt)
				.updatedAt(updatedAt);
		return builder;
	}
}
