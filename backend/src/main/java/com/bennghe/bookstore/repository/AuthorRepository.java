package com.bennghe.bookstore.repository;

import com.bennghe.bookstore.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findByName(String name);
    List<Author> findByNameContainingIgnoreCase(String name);
}
