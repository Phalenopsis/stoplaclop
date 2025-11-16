package eu.nicosworld.stoptaclop.authentication;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.nicosworld.stoptaclop.authentication.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
