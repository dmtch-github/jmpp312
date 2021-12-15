package jm.spring.bootstrap.repository;

import jm.spring.bootstrap.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    public Role findRoleByName(String name);
}
