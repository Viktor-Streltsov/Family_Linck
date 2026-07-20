package com.familylink.backend.situation;

import com.familylink.backend.family.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SituationRepository extends JpaRepository<Situation, UUID> {

    List<Situation> findByFamilyOrderByCreatedAtDesc(Family family);
}