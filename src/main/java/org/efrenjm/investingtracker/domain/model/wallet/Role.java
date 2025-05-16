package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Role {
    private String name;
    private String description;
    private Permissions permissions;
    @Builder.Default
    private List<String> members = new ArrayList<>();

    public void addMember(String member) {
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(member);
    }

    public static List<List<Role>> defaultRoles() {
        return new ArrayList<>(List.of(
                new ArrayList<>(List.of(owner())),
                new ArrayList<>(List.of(manager())),
                new ArrayList<>(List.of(viewer()))
        ));
    }

    public static Role owner() {
        return Role.builder()
                .name("Owner")
                .description("Role with all permissions")
                .permissions(Permissions.owner())
                .build();
    }

    public static Role manager() {
        return Role.builder()
                .name("Manager")
                .description("Role for general use, all permissions except for removing accounts or editing wallet")
                .permissions(Permissions.manager())
                .build();
    }

    public static Role viewer() {
        return Role.builder()
                .name("Viewer")
                .description("Role for viewing only, with no additional permissions")
                .permissions(Permissions.viewer())
                .build();
    }

    @Builder
    @Getter
    @Setter
    @ToString
    public static class Permissions {
        private CRUDPermissions accounts;
        private CRUDPermissions transactions;
        private CRUDPermissions rules;
        private CRUDPermissions members;
        private RUDPermissions wallet;

        public static Permissions owner() {
            return Permissions.builder()
                    .accounts(CRUDPermissions.all())
                    .transactions(CRUDPermissions.all())
                    .rules(CRUDPermissions.all())
                    .members(CRUDPermissions.all())
                    .wallet(CRUDPermissions.all())
                    .build();
        }

        public static Permissions manager() {
            return Permissions.builder()
                    .accounts(CRUDPermissions.allExceptRemove())
                    .transactions(CRUDPermissions.all())
                    .rules(CRUDPermissions.all())
                    .members(CRUDPermissions.all())
                    .wallet(CRUDPermissions.viewOnly())
                    .build();
        }

        public static Permissions viewer() {
            return Permissions.builder()
                    .accounts(CRUDPermissions.viewOnly())
                    .transactions(CRUDPermissions.viewOnly())
                    .rules(CRUDPermissions.viewOnly())
                    .members(CRUDPermissions.viewOnly())
                    .wallet(CRUDPermissions.viewOnly())
                    .build();
        }

        @AllArgsConstructor
        @SuperBuilder
        @Getter
        @Setter
        @ToString
        public static class RUDPermissions {
            private boolean view;
            private boolean edit;
            private boolean remove;
        }

        @Getter
        @Setter
        @ToString
        @SuperBuilder
        public static class CRUDPermissions extends RUDPermissions {
            private boolean add;

            public static CRUDPermissions all() {
                return CRUDPermissions.builder().add(true).edit(true).view(true).remove(true).build();
            }

            public static CRUDPermissions allExceptRemove() {
                return CRUDPermissions.builder().add(true).edit(true).view(true).remove(false).build();
            }

            public static CRUDPermissions viewOnly() {
                return CRUDPermissions.builder().add(false).edit(false).view(true).remove(false).build();
            }
        }
    }
}
