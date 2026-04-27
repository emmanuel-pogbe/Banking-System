package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.constants.TransactionType;
import com.pogbe.bankingsystem.models.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    @Query("""
            SELECT tr
            FROM TransactionRecord tr
            WHERE ((tr.senderAccount.id = :accountId
                    AND tr.transactionType = com.pogbe.bankingsystem.constants.TransactionType.DEBIT)
               OR (tr.receiverAccount.id = :accountId
                    AND tr.transactionType = com.pogbe.bankingsystem.constants.TransactionType.CREDIT))
              AND (:type IS NULL OR tr.transactionType = :type)
            """)
    Page<TransactionRecord> findStatementByAccountId(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            Pageable pageable
    );
}
