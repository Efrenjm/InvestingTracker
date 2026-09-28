package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.adapter;

import lombok.RequiredArgsConstructor;
import org.efrenjm.investingtracker.domain.ports.outbound.repository.TransactionRepositoryPort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {}
