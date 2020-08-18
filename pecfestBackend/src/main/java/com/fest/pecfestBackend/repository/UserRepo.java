package com.fest.pecfestBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.fest.pecfestBackend.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{

	List<User> findByEmail(String email);

	List<User> findByRequireAccommodationTrue();

}
