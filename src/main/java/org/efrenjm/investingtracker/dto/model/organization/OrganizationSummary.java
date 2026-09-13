package org.efrenjm.investingtracker.dto.model.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.efrenjm.investingtracker.model.organization.role.UserRole;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class OrganizationSummary {
	private String id;
	private String name;
	private String description;
	private List<UserRole> members;
}
