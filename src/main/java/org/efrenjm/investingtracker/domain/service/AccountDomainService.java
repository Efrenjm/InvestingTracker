package org.efrenjm.investingtracker.domain.service;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.model.account.*;
import org.efrenjm.investingtracker.domain.ports.outbound.utils.IdGeneratorPort;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class AccountDomainService
{
	private final IdGeneratorPort idGenerator;

	public DebitAccount createDebitAccount(String name, String description)
	{
		Date now = new Date();
		return DebitAccount.builder()
				.id(idGenerator.generateId())
				.name(name)
				.description(description)
				.type(AccountType.DEBIT)
				.available(0.0)
				.goal(0.0)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}

	public CreditAccount createCreditAccount(String name, String description, Double debt, Double creditLimit)
	{
		Date now = new Date();
		return CreditAccount.builder()
				.id(idGenerator.generateId())
				.name(name)
				.description(description)
				.type(AccountType.CREDIT)
				.available(0.0)
				.currentDebt(debt)
				.creditLimit(creditLimit)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}

	public AssetAccount createAssetAccount(String name, String description, String asset, Double currentPrice, Double averageCost, Double goal)
	{
		Date now = new Date();
		return AssetAccount.builder()
				.id(idGenerator.generateId())
				.name(name)
				.description(description)
				.type(AccountType.ASSET)
				.available(0.0)
				.asset(asset)
				.currentPrice(currentPrice)
				.averageCost(averageCost)
				.goal(goal)
				.createdAt(now)
				.updatedAt(now)
				.build();
	}
}
