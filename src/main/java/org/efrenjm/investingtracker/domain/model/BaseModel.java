package org.efrenjm.investingtracker.domain.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseModel
{
	protected String id;

	protected Date createdAt;

	protected Date updatedAt;
}
