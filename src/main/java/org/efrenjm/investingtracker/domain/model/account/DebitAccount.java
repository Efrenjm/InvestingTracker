//package org.efrenjm.investingtracker.domain.model.account;
//
//import lombok.*;
//
//import java.util.Date;
//import java.util.List;
//
//@AllArgsConstructor
//@Builder
//@Getter
//@Setter
//@ToString
//public class DebitAccount extends BaseAccount {
//	private Double goal;
//
//	public static Account defaultAccount(String accountId, String walletId) {
//		Date now = new Date();
//		return Account.builder()
//				.id(accountId)
//				.name("Personal")
//				.description("Personal account")
//				.walletId(walletId)
//				.sharingWallets(List.of())
//				.type(AccountType.DEBIT)
//				.available(0.0)
//				.tags(List.of())
//				.createdAt(now)
//				.updatedAt(now)
//				.goal(0.0)
//				.build();
//	}
//}
