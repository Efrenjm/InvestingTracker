package org.efrenjm.investingtracker.domain.model.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AccountType {
    DEBIT("debit"),
    CREDIT("credit"),
    ASSET("asset");
    private final String type;
}
