package com.example.repository;

import com.example.model.db.UsersDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersRepo extends JpaRepository<UsersDao, Long> {
    @Query("SELECT u FROM UsersDao u LEFT JOIN FETCH u.department LEFT JOIN FETCH u.unit")
    List<UsersDao> fetchAll();

    @Query("SELECT u FROM UsersDao u LEFT JOIN FETCH u.department LEFT JOIN FETCH u.unit WHERE u.email = :email")
    UsersDao fetchByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE UsersDao u SET u.password = :password WHERE u.email = :email")
    int resetPassword(@Param("password") String password, @Param("email") String email);

    @Modifying
    @Query("UPDATE UsersDao u SET u.isActive = TRUE, u.sendMail = TRUE WHERE u.email = :email")
    int activateByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE UsersDao u SET u.isDeleted = TRUE WHERE u.email = :email")
    int deleteFlagByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE UsersDao u SET u.departmentId = :departmentId, u.unitId = :unitId, u.name = :name " +
            "WHERE u.email = :email")
    int updateByEmail(@Param("departmentId") String departmentId, @Param("unitId") String unitId, @Param("name") String name,
                      @Param("email") String email);
    @Modifying
    @Query("DELETE FROM UsersDao u WHERE u.email = :email")
    void deleteByEmail(@Param("email") String email);
}
