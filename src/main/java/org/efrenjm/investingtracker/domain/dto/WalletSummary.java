package org.efrenjm.investingtracker.domain.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletSummary {
	private String id;
	private String name;
	private String description;
	private List<List<RoleSummary>> roles;

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class RoleSummary {
		private String name;
		private List<MemberSummary> members;
	}

	@AllArgsConstructor
	@Builder
	@Getter
	@Setter
	@ToString
	public static class MemberSummary {
		private String id;
		private String username;
		private String firstName;
		private String middleName;
		private String lastName;
		private String profilePicture;
	}
}
