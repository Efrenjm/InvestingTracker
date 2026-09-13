package org.efrenjm.investingtracker.model.transaction;

import lombok.*;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AccountAllocation {
	private ObjectId account;

	private double amount;
}
