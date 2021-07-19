package ru.kireev.ATM.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.ATM.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
