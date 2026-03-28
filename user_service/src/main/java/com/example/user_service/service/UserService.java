package com.example.user_service.service;

import com.example.user_service.dto.UserResponse;
import com.example.user_service.entity.UserDetail;
import com.example.user_service.entity.UserFemale;
import com.example.user_service.entity.UserMale;
import com.example.user_service.repository.UserDetailRepository;
import com.example.user_service.repository.UserFemaleRepository;
import com.example.user_service.repository.UserMaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMaleRepository maleRepo;

    @Autowired
    private UserFemaleRepository femaleRepo;

    @Autowired
    private UserDetailRepository detailRepo;

    // ================= GET USER =================
    public UserResponse getById(Long id) {

        UserDetail detail = detailRepo.findById(id).orElseThrow();

        UserResponse res = new UserResponse();
        res.setGender(detail.getGender());
        res.setAddress(detail.getAddress());
        res.setPhone(detail.getPhone());

        if ("male".equalsIgnoreCase(detail.getGender())) {
            UserMale user = maleRepo.findById(id).orElseThrow();

            res.setId(user.getId());
            res.setName(user.getName());
            res.setEmail(user.getEmail());
            res.setAge(user.getAge());

        } else {
            UserFemale user = femaleRepo.findById(id).orElseThrow();

            res.setId(user.getId());
            res.setName(user.getName());
            res.setEmail(user.getEmail());
            res.setAge(user.getAge());
        }

        return res;
    }

    // ================= CREATE USER =================
    public UserResponse create(UserResponse req) {

        UserDetail detail = new UserDetail();
        detail.setGender(req.getGender());
        detail.setAddress(req.getAddress());
        detail.setPhone(req.getPhone());

        if ("male".equalsIgnoreCase(req.getGender())) {
            UserMale male = new UserMale();
            male.setName(req.getName());
            male.setEmail(req.getEmail());
            male.setAge(req.getAge());

            male = maleRepo.save(male);

            detail.setUserId(male.getId());
            detailRepo.save(detail);

            req.setId(male.getId());

        } else {
            UserFemale female = new UserFemale();
            female.setName(req.getName());
            female.setEmail(req.getEmail());
            female.setAge(req.getAge());

            female = femaleRepo.save(female);

            detail.setUserId(female.getId());
            detailRepo.save(detail);

            req.setId(female.getId());
        }

        return req;
    }

    // ================= DELETE =================
    public void delete(Long id) {
        UserDetail detail = detailRepo.findById(id).orElseThrow();

        if ("male".equalsIgnoreCase(detail.getGender())) {
            maleRepo.deleteById(id);
        } else {
            femaleRepo.deleteById(id);
        }

        detailRepo.deleteById(id);
    }
}