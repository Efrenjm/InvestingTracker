package org.efrenjm.investingtracker.service.model.organization;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.dto.model.account.AccountSummary;
import org.efrenjm.investingtracker.model.organization.Organization;
import org.efrenjm.investingtracker.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class OrganizationService {
	private final OrganizationRepository organizationRepository;

	public Mono<Organization> saveOrganization(Organization organization) {
		organization.setUpdatedAt(new Date());
		return organizationRepository.save(organization);
	}

	public Flux<AccountSummary> fetchAccounts(ObjectId organizationId) {
		return organizationRepository.findById(organizationId)
				.flatMapIterable(Organization::getAccounts)
				.map(AccountSummary::new);
	}
}
