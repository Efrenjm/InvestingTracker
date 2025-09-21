package org.efrenjm.investingtracker.domain.model.account;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseAccount extends AuditableModel
{
	protected String name;

	protected String description;

	protected String walletId;

	@Builder.Default
	protected Set<String> sharingWallets = new HashSet<>();

	protected AccountType type;

	@Builder.Default
	protected Double available = 0.0;

	@Builder.Default
	protected Set<String> tags = new HashSet<>();

	protected AccountConfig accountConfig;

	@Builder.Default
	protected Set<String> rules = new HashSet<>();


	public Optional<Set<String>> getSharingWallets()
	{
		return Optional.ofNullable(sharingWallets);
	}

	public Optional<Set<String>> getTags()
	{
		return Optional.ofNullable(tags);
	}

	public Optional<Set<String>> getRules()
	{
		return Optional.ofNullable(rules);
	}

//	public void setName(String name, String updaterUserId)
//	{
//		this.name = name;
//		touchAccount(updaterUserId);
//	}
//
//	public void setDescription(String description, String updaterUserId)
//	{
//		this.description = description;
//		touchAccount(updaterUserId);
//	}
//
//	public void setWalletId(String walletId, String updaterUserId)
//	{
//		this.walletId = walletId;
//		touchAccount(updaterUserId);
//	}

	public void addSharingWallet(String walletId, String updaterUserId)
	{
		ensureSharingWallets();
		if (sharingWallets.contains(walletId))
		{
			throw new IllegalArgumentException("Wallet " + walletId + " already has view access to this account.");
		}

		sharingWallets.add(walletId);
	}

	public void removeSharingWallet(String walletId, String updaterUserId)
	{
		ensureSharingWallets();
		if (!sharingWallets.contains(walletId))
		{
			throw new IllegalArgumentException("Wallet " + walletId + " don't have view access to this account.");
		}

		sharingWallets.remove(walletId);
	}

//	public void setAvailable(Double available, String updaterUserId)
//	{
//		this.available = available;
//		touchAccount(updaterUserId);
//	}

	public void addTag(String tag, String updaterUserId)
	{
		ensureTags();
		if (tags.contains(tag))
		{
			throw new IllegalArgumentException("Tag " + tag + " already exists in the account.");
		}

		tags.add(tag);
	}

	public void removeTag(String tag, String updaterUserId)
	{
		ensureTags();
		if (!tags.contains(tag))
		{
			throw new IllegalArgumentException("Tag " + tag + " doesn't exist in the account.");
		}

		tags.remove(tag);
	}

	private void ensureSharingWallets()
	{
		if (sharingWallets == null)
		{
			sharingWallets = new HashSet<>();
		}
	}

	private void ensureTags()
	{
		if (tags == null)
		{
			tags = new HashSet<>();
		}
	}
}
