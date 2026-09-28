package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public abstract class BaseTransactionCategory extends AuditableModel {
    public String description;
    public String color;
    public String icon;
}
