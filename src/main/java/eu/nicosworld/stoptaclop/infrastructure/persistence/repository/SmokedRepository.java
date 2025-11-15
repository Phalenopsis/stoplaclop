package eu.nicosworld.stoptaclop.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.AuthenticatedUser;
import eu.nicosworld.stoptaclop.infrastructure.persistence.entity.Smoked;
import eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay;

public interface SmokedRepository extends JpaRepository<Smoked, Long> {

  long countByUser(AuthenticatedUser user);

  @Query("SELECT MIN(s.date) FROM Smoked s WHERE s.user = :user")
  LocalDateTime findFirstDateByUser(@Param("user") AuthenticatedUser user);

  @Query(
      "SELECT new eu.nicosworld.stoptaclop.infrastructure.web.dto.SmokedCountByDay("
          + "CAST(s.date AS java.time.LocalDate), COUNT(s)) "
          + "FROM Smoked s "
          + "WHERE s.user = :user AND s.date >= :oneWeekAgo "
          + "GROUP BY CAST(s.date AS java.time.LocalDate) "
          + "ORDER BY CAST(s.date AS java.time.LocalDate) ASC")
  List<SmokedCountByDay> countSmokedByDay(
      @Param("user") AuthenticatedUser user, @Param("oneWeekAgo") LocalDateTime oneWeekAgo);

  Optional<Smoked> findTopByUserOrderByDateDesc(AuthenticatedUser user);
}
