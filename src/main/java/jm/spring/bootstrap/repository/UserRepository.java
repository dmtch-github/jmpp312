package jm.spring.bootstrap.repository;

import jm.spring.bootstrap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findUserByEmail(String email);
}
