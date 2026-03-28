package com.example.user_service.repository;

import com.example.user_service.entity.UserDetail;
import com.example.user_service.entity.UserFemale;
import com.example.user_service.entity.UserMale;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFemaleRepository extends JpaRepository<UserFemale, Long> {}