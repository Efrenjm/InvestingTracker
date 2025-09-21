package org.efrenjm.investingtracker.infrastructure.utils;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("mongoIdGenerator")
public class MongoIdGenerator implements IdGeneratorPort
{
	@Override
	public String generateId()
	{
		return new ObjectId().toHexString();
	}

	@Override
	public boolean isValidId(String id)
	{
		return id != null && ObjectId.isValid(id);
	}
}
