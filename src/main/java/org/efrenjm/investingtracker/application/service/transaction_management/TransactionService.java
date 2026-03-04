package org.efrenjm.investingtracker.application.service.transaction_management;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.dto.TransactionSummary;
import org.efrenjm.investingtracker.domain.model.transaction.Transaction;
import org.efrenjm.investingtracker.domain.ports.inbound.TransactionPort;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.TransactionRepositoryPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService implements TransactionPort
{
	private final TransactionRepositoryPort transactionRepository;

	// TODO: Implement getTransactionDetails - fetch transaction by ID and verify user access
	public Mono<Transaction> getTransactionDetails(String transactionId) {
		return Mono.error(new UnsupportedOperationException("getTransactionDetails not yet implemented"));
	}

	// TODO: Implement getAccountTransactions - fetch transactions for an account with pagination
	public Mono<List<TransactionSummary>> getAccountTransactions(
			String accountId,
			Date startDate,
			Date endDate,
			int page,
			int size) {
		return Mono.error(new UnsupportedOperationException("getAccountTransactions not yet implemented"));
	}

	// TODO: Implement addTransaction - validate accounts, create via domain service
	public Mono<Transaction> addTransaction() {
		return Mono.error(new UnsupportedOperationException("addTransaction not yet implemented"));
	}

	// TODO: Implement updateTransaction - validate permissions, update via domain service
	public Mono<Transaction> updateTransaction(Transaction transaction) {
		return Mono.error(new UnsupportedOperationException("updateTransaction not yet implemented"));
	}

	// TODO: Implement deleteTransaction - validate permissions, delete transaction
	public Mono<Void> deleteTransaction(String transactionId) {
		return Mono.error(new UnsupportedOperationException("deleteTransaction not yet implemented"));
	}
}
