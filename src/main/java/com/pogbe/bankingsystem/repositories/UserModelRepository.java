package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserModelRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);

    Optional<UserModel> findByPhoneNumber(String phoneNumber);
}
