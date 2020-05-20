package fudan.se.lab2.repository;

import fudan.se.lab2.domain.Conference;
import fudan.se.lab2.domain.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleName(String roleName);
}

