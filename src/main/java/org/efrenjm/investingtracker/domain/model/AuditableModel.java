package org.efrenjm.investingtracker.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class AuditableModel extends BaseModel
{
	protected String createdBy;

	protected String updatedBy;
}
