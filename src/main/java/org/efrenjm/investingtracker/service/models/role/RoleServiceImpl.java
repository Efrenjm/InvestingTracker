package org.efrenjm.investingtracker.service.models.role;

import org.efrenjm.investingtracker.model.Role.Role;
import org.efrenjm.investingtracker.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Flux<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Mono<Role> getRoleById(String id) {
        return roleRepository.findById(id);
    }

    @Override
    public Mono<Role> createRole(Role role) {
        return roleRepository.insert(role);
    }

    @Override
    public Mono<Role> updateRole(String id, Role role) {
        role.setId(id);
        return roleRepository.save(role);
    }

    @Override
    public Mono<Void> deleteRole(String id) {
        return null;
    }
}
