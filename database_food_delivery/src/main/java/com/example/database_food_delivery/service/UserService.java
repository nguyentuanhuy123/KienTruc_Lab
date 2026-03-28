package com.example.database_food_delivery.service;

import com.example.database_food_delivery.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addUser(User user) {
        String sql = user.getGender().equalsIgnoreCase("male") ?
                "INSERT INTO user_male (name,email,age) VALUES (?,?,?)" :
                "INSERT INTO user_female (name,email,age) VALUES (?,?,?)";

        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getAge());

        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        String detailSql = "INSERT INTO user_detail (user_id, gender, address, phone) VALUES (?,?,?,?)";
        jdbcTemplate.update(detailSql, id, user.getGender(), user.getAddress(), user.getPhone());
    }

    public User getUser(Integer id, String gender) {
        String sql = gender.equalsIgnoreCase("male") ?
                "SELECT * FROM user_male WHERE id = ?" :
                "SELECT * FROM user_female WHERE id = ?";

        User user = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("age"), null, null, null)
        );

        String detailSql = "SELECT * FROM user_detail WHERE user_id = ?";
        jdbcTemplate.queryForObject(detailSql, new Object[]{id}, (rs, rowNum) -> {
            user.setGender(rs.getString("gender"));
            user.setAddress(rs.getString("address"));
            user.setPhone(rs.getString("phone"));
            return null;
        });

        return user;
    }

    // ⚡ Thêm hàm lấy tất cả users
    public List<User> getAllUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM user_male", (rs, rowNum) ->
                new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("age"), "male", null, null)
        );

        List<User> femaleUsers = jdbcTemplate.query("SELECT * FROM user_female", (rs, rowNum) ->
                new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getInt("age"), "female", null, null)
        );

        users.addAll(femaleUsers);

        // Lấy chi tiết từ user_detail
        for (User user : users) {
            jdbcTemplate.query("SELECT * FROM user_detail WHERE user_id = ?", new Object[]{user.getId()}, (rs, rowNum) -> {
                user.setGender(rs.getString("gender"));
                user.setAddress(rs.getString("address"));
                user.setPhone(rs.getString("phone"));
                return null;
            });
        }

        return users;
    }
}