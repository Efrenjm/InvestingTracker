package org.efrenjm.investingtracker.model.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.efrenjm.investingtracker.model.organization.role.Role;
import org.efrenjm.investingtracker.model.organization.rule.Rule;
import org.efrenjm.investingtracker.model.organization.transaction_category.TransactionCategory;

import java.util.List;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class OrganizationConfig {
	private List<Role> roles;

	private List<Rule> rules;

	private List<TransactionCategory> transactionCategories;
}
