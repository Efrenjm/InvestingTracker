package org.efrenjm.investingtracker.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class TransactionSummary {
	private String name;

	private String type;

	private Double totalAmount;

	private List<String> categories;

	private Date transactionDate;
}
