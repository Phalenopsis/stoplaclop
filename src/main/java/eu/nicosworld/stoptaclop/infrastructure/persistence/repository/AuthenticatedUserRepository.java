package eu.nicosworld.stoptaclop.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;

public interface AuthenticatedUserRepository extends JpaRepository<AuthenticatedUser, Long> {}
