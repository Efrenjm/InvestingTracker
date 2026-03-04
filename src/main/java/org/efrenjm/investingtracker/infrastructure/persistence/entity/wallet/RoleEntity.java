package org.efrenjm.investingtracker.infrastructure.persistence.entity.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.AuditableModel;
import org.efrenjm.investingtracker.domain.model.wallet.Role;
import org.efrenjm.investingtracker.infrastructure.persistence.utils.MongoUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RoleEntity extends AuditableModel
{
	@Field("description") private String description;
	@Field("permissions") private PermissionsEntity permissions;
	@Field("members") private Set<ObjectId> members;

	public static RoleEntity fromDomain(Role role)
	{
		if (role == null)
			return null;

		return RoleEntity.builder()
				.description(role.getDescription())
				.permissions(PermissionsEntity.fromDomain(role.getPermissions()))
				.members(MongoUtils.tryParseIds(role.getMembers()))
				.build();
	}

	public Role toDomain()
	{
		return Role.builder()
				.description(description)
				.permissions(Optional.ofNullable(permissions).map(PermissionsEntity::toDomain).orElse(null))
				.members(MongoUtils.collectIds(members))
				.build();
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class PermissionsEntity
	{
		@Field("accounts") private EntityCRUDPermissions accounts;
		@Field("transactions") private EntityCRUDPermissions transactions;
		@Field("rules") private EntityCRUDPermissions rules;
		@Field("members") private EntityCRUDPermissions members;
		@Field("wallet") private EntityRUDPermissions wallet;

		public static PermissionsEntity fromDomain(Role.Permissions permissions)
		{
			if (permissions == null)
				return null;

			return PermissionsEntity.builder()
					.accounts(EntityCRUDPermissions.fromDomain(permissions.getAccounts()))
					.transactions(EntityCRUDPermissions.fromDomain(permissions.getTransactions()))
					.rules(EntityCRUDPermissions.fromDomain(permissions.getRules()))
					.members(EntityCRUDPermissions.fromDomain(permissions.getMembers()))
					.wallet(EntityRUDPermissions.fromDomain(permissions.getWallet()))
					.build();
		}

		public Role.Permissions toDomain()
		{
			return Role.Permissions.builder()
					.accounts(Optional.ofNullable(accounts).map(EntityCRUDPermissions::toDomain).orElse(null))
					.transactions(Optional.ofNullable(transactions).map(EntityCRUDPermissions::toDomain).orElse(null))
					.rules(Optional.ofNullable(rules).map(EntityCRUDPermissions::toDomain).orElse(null))
					.members(Optional.ofNullable(members).map(EntityCRUDPermissions::toDomain).orElse(null))
					.wallet(Optional.ofNullable(wallet).map(EntityRUDPermissions::toDomain).orElse(null))
					.build();
		}

		@AllArgsConstructor
		@SuperBuilder
		@Getter
		@Setter
		@ToString
		public static class EntityRUDPermissions
		{
			@Field("view") protected boolean view;
			@Field("edit") protected boolean edit;
			@Field("remove") protected boolean remove;

			public static EntityRUDPermissions fromDomain(Role.Permissions.RUDPermissions permissions)
			{
				if (permissions == null)
					return null;

				return populatePermissionsEntityFields(EntityRUDPermissions.builder(), permissions)
						.build();
			}

			public Role.Permissions.RUDPermissions toDomain()
			{
				return populatePermissionsDomainFields(Role.Permissions.RUDPermissions.builder())
						.build();
			}

			protected static <B extends EntityRUDPermissionsBuilder<?, ?>, D extends Role.Permissions.RUDPermissions> B populatePermissionsEntityFields(
					B builder,
					D permissions
			)
			{
				builder
						.view(permissions.isView())
						.edit(permissions.isEdit())
						.remove(permissions.isRemove());
				return builder;
			}

			protected <B extends Role.Permissions.RUDPermissions.RUDPermissionsBuilder<?, ?>> B populatePermissionsDomainFields(B builder)
			{
				builder
						.view(view)
						.edit(edit)
						.remove(remove);
				return builder;
			}
		}

		@SuperBuilder
		@Getter
		@Setter
		@ToString
		public static class EntityCRUDPermissions extends EntityRUDPermissions
		{
			@Field("add") private boolean add;

			public static EntityCRUDPermissions fromDomain(Role.Permissions.CRUDPermissions permissions)
			{
				if (permissions == null)
					return null;

				return populatePermissionsEntityFields(EntityCRUDPermissions.builder(), permissions)
						.add(permissions.isAdd())
						.build();
			}

			@Override
			public Role.Permissions.CRUDPermissions toDomain()
			{
				return populatePermissionsDomainFields(Role.Permissions.CRUDPermissions.builder())
						.add(add)
						.build();
			}
		}
	}
}
