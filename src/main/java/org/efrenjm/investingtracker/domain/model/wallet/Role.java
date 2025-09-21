package org.efrenjm.investingtracker.domain.model.wallet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.efrenjm.investingtracker.domain.model.AuditableModel;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Role extends AuditableModel
{
    private String description;

    private Permissions permissions;

    @Builder.Default
    private Set<String> members = new HashSet<>();

    public void addMember(String member)
    {
        if (members == null)
        {
            members = new HashSet<>();
        }
        if (members.contains(member))
        {
            throw new IllegalArgumentException("member: " + member + "already exists in this role");
        }
        members.add(member);
    }

    public void removeMember(String member)
    {
        if (members == null)
        {
            members = new HashSet<>();
        }
        if (!members.contains(member))
        {
            throw new IllegalArgumentException("member: " + member + "doesn't exist in this role");
        }
        members.remove(member);
    }

    @Builder
    @Getter
    @Setter
    @ToString
    public static class Permissions
    {
        private CRUDPermissions accounts;
        private CRUDPermissions transactions;
        private CRUDPermissions rules;
        private CRUDPermissions members;
        private RUDPermissions wallet;

        @AllArgsConstructor
        @SuperBuilder
        @Getter
        @Setter
        @ToString
        public static class RUDPermissions
        {
            private boolean view;
            private boolean edit;
            private boolean remove;
        }

        @Getter
        @Setter
        @ToString
        @SuperBuilder
        public static class CRUDPermissions extends RUDPermissions
        {
            private boolean add;
        }
    }
}
