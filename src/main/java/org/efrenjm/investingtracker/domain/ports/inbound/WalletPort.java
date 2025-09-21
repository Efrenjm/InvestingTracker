package org.efrenjm.investingtracker.domain.ports.inbound;

import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.model.wallet.Wallet;

public interface WalletPort
{
	Wallet createWallet(Wallet wallet);
	Wallet getWalletById(ObjectId id);
}
