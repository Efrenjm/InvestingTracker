package org.efrenjm.investingtracker.infrastructure.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class AuditableMongoEntity extends BaseMongoEntity
{
	@CreatedBy
	@Field("created_by") protected String createdBy;

	@LastModifiedBy
	@Field("updated_by") protected String updatedBy;

	public static <B extends AuditableMongoEntityBuilder<?, ?>, D extends AuditableModel> B populateAuditableEntityFields(B builder, D domainObject)
	{
		if (domainObject == null)
			return builder;

		builder
				.createdBy(domainObject.getCreatedBy())
				.updatedBy(domainObject.getUpdatedBy());
		return populateBaseEntityFields(builder, domainObject);
	}

	public <B extends AuditableModel.AuditableModelBuilder<?, ?>> B populateAuditableDomainFields(B builder)
	{
		builder
				.createdBy(createdBy)
				.updatedBy(updatedBy);
		return populateBaseDomainFields(builder);
	}
}
