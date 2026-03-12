package com.college.eventapp.Repository;

import com.college.eventapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import com.college.eventapp.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByEmail(String email);
}
