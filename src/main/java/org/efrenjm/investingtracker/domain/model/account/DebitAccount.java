package org.efrenjm.investingtracker.domain.model.account;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
public class DebitAccount extends BaseAccount
{
	private Double goal;
}
