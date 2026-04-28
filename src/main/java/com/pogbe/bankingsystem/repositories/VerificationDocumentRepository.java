package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.models.VerificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    void deleteAllByUser(UserModel userId);

    Optional<VerificationDocument> findByIdAndUser(Long id, UserModel user);

    List<VerificationDocument> findAllByUser(UserModel user);
}
