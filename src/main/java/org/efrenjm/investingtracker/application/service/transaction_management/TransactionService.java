package org.efrenjm.investingtracker.application.service.transaction_management;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.TransactionSummary;
import org.efrenjm.investingtracker.domain.model.transaction.Transaction;
import org.efrenjm.investingtracker.domain.ports.inbound.TransactionServicePort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.TransactionRepositoryPort;
import org.efrenjm.investingtracker.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionServicePort {
	private final TransactionRepositoryPort transactionRepository;

	public Mono<Transaction> getTransactionDetails(ObjectId transactionId) {
		return null;
	}

	public Mono<List<TransactionSummary>> getAccountTransactions(
			ObjectId accountId,
			Date startDate,
			Date endDate,
			int page,
			int size,
			UserEntity userEntity) {
		if (accountId == null) {

		}
		return null;
//		return transactionService.fetchAccountTransactions(accountId, startDate, endDate, 0, 10)
//				.collectList();
	}

	public Mono<Transaction> addTransaction() {
		return null;
	}

	public Mono<Transaction> updateTransaction(Transaction transaction) {
		return null;
	}

	public Mono<Void> deleteTransaction(String transactionId) {
		return null;
	}
}
