package com.familylink.backend.situation;

import com.familylink.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SituationParticipantRepository extends JpaRepository<SituationParticipant, UUID> {

    Optional<SituationParticipant> findBySituationAndUser(Situation situation, User user);

    List<SituationParticipant> findBySituation(Situation situation);
}