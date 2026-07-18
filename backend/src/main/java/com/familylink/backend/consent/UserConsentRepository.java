package com.familylink.backend.consent;

import com.familylink.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserConsentRepository extends JpaRepository<UserConsent, UUID> {

    Optional<UserConsent> findByUserAndConsentTypeAndRevokedAtIsNull(User user, ConsentType type);

    boolean existsByUserAndConsentTypeAndGrantedTrueAndRevokedAtIsNull(User user, ConsentType type);
}
