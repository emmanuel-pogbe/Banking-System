package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.models.UserModel;
import com.pogbe.bankingsystem.models.VerificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationDocumentRepository extends JpaRepository<VerificationDocument, Long> {
    void deleteAllByUser(UserModel userId);

    List<VerificationDocument> findAllByUser(UserModel user);
}
