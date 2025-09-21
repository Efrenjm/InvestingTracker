package org.efrenjm.investingtracker.infrastructure.persistence.utils;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.utils.CollectionTransformer;

import java.util.Set;

public class MongoUtils
{
	private MongoUtils() {}

	public static String idToDomain(ObjectId id)
	{
		if (id == null)
		{
			return null;
		}
		return id.toHexString();
	}

	public static ObjectId idToEntity(String id)
	{
		try
		{
			return new ObjectId(id);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}

	public static Set<ObjectId> tryParseIds(Set<String> ids)
	{
		return CollectionTransformer.transform(ids, MongoUtils::idToEntity);
	}

	public static Set<String> collectIds(Set<ObjectId> ids)
	{
		return CollectionTransformer.transform(ids, MongoUtils::idToDomain);
	}
}
