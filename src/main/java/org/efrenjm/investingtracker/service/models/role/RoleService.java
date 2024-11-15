package org.efrenjm.investingtracker.service.models.role;

import org.efrenjm.investingtracker.model.Role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleService {
    Flux<Role> getAllRoles();

    Mono<Role> getRoleById(String id);

    Mono<Role> createRole(Role role);

    Mono<Role> updateRole(String id, Role role);

    Mono<Void> deleteRole(String id);
}
