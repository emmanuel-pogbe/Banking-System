package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.models.VerificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    void deleteAllByUser(UserModel userId);
}
