package com.pogbe.bankingsystem.repositories;

import com.pogbe.bankingsystem.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserModelRepository extends JpaRepository<UserModel, Long> {
}
