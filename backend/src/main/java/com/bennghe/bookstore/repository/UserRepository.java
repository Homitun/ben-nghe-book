package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByIsActiveTrue();

    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.role.name = :roleName")
    List<User> findByRoleName(String roleName);
}
