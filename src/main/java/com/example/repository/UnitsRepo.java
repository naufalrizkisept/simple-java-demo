package com.example.repository;

import com.example.model.db.UnitsDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitsRepo extends JpaRepository<UnitsDao, Long> {
    @Query("SELECT u FROM UnitsDao u LEFT JOIN FETCH u.department")
    List<UnitsDao> fetchAll();

    @Query("SELECT u FROM UnitsDao u LEFT JOIN FETCH u.department WHERE u.id = :id")
    UnitsDao fetchById(@Param("id") String id);

    @Query("SELECT u FROM UnitsDao u LEFT JOIN FETCH u.department WHERE u.departmentId = :departmentId")
    List<UnitsDao> fetchByDeptId(@Param("departmentId") String departmentId);

    @Modifying
    @Query("UPDATE UnitsDao u SET u.departmentId = :departmentId, u.name = :name, u.address = :address, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    int updateById(@Param("departmentId") String departmentId, @Param("name") String name, @Param("address") String address, @Param("id") String id);

    @Modifying
    @Query("DELETE FROM UnitsDao u WHERE u.id = :id")
    void deleteById(@Param("id") String id);
}
