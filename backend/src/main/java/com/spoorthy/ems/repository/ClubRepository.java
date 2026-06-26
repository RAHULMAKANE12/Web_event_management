package com.spoorthy.ems.repository;

import com.spoorthy.ems.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByClubName(String clubName);

    boolean existsByClubName(String clubName);
}
