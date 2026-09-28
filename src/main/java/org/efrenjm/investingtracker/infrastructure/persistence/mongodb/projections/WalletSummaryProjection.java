package org.efrenjm.investingtracker.infrastructure.persistence.mongodb.projections;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.efrenjm.investingtracker.domain.dto.WalletSummary;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class WalletSummaryProjection {
    private ObjectId id;
    private String name;
    private String description;
    private List<List<RoleSummaryProjection>> roles;

    public WalletSummary toDomain() {
        return WalletSummary.builder()
                .id(id.toString())
                .name(name)
                .description(description)
                .roles(
                        roles.stream()
                                .map(
                                        role ->
                                                role.stream()
                                                        .map(RoleSummaryProjection::toDomain)
                                                        .toList())
                                .toList())
                .build();
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    public static class RoleSummaryProjection {
        private String name;
        private List<MemberSummaryProjection> members;

        public WalletSummary.RoleSummary toDomain() {
            return WalletSummary.RoleSummary.builder()
                    .name(name)
                    .members(members.stream().map(MemberSummaryProjection::toDomain).toList())
                    .build();
        }
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    @ToString
    public static class MemberSummaryProjection {
        private String id;
        private String username;
        private String firstName;
        private String middleName;
        private String lastName;
        private String profilePicture;

        public WalletSummary.MemberSummary toDomain() {
            return WalletSummary.MemberSummary.builder()
                    .id(id)
                    .username(username)
                    .firstName(firstName)
                    .middleName(middleName)
                    .lastName(lastName)
                    .profilePicture(profilePicture)
                    .build();
        }
    }
}
