package org.efrenjm.investingtracker.domain.model.transaction;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Periodicity {
	private int amount;
	private String period;
}
