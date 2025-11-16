package eu.nicosworld.stoptaclop.config.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.nicosworld.stoptaclop.authentication.UserRepository;
import eu.nicosworld.stoptaclop.authentication.model.User;

@Service
public class CustomUserDetailService implements UserDetailsService {

  private UserRepository userRepository;

  public CustomUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "Utilisateur non trouvé avec email: " + username));

    // Conversion des rôles en authorities Spring Security
    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
  }
}
