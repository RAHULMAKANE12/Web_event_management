package com.spoorthy.ems.repository;

import com.spoorthy.ems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByRollNumber(String rollNumber);

    Optional<User> findByEmailOrRollNumber(String email, String rollNumber);

    boolean existsByEmail(String email);

    boolean existsByRollNumber(String rollNumber);
}
