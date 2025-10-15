package com.example.repository;

import com.example.model.db.DepartmentsDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentsRepo extends JpaRepository<DepartmentsDao, Long> {
    @Query("SELECT d FROM DepartmentsDao d")
    List<DepartmentsDao> fetchAll();

    @Query("SELECT d FROM DepartmentsDao d WHERE d.id = :id")
    DepartmentsDao fetchById(@Param("id") String id);

    @Query("SELECT d FROM DepartmentsDao d WHERE d.code = :code")
    DepartmentsDao fetchByCode(@Param("code") String code);

    @Modifying
    @Query("UPDATE DepartmentsDao d SET d.name = :name, d.key = :key, d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    int updateById(@Param("name") String name, @Param("key") String key, @Param("id") String id);

    @Modifying
    @Query("UPDATE DepartmentsDao d SET d.name = :name, d.key = :key, d.updatedAt = CURRENT_TIMESTAMP WHERE d.code = :code")
    int updateByCode(@Param("name") String name, @Param("key") String key, @Param("code") String code);

    @Modifying
    @Query("DELETE FROM DepartmentsDao d WHERE d.id = :id")
    void deleteById(@Param("id") String id);
}
