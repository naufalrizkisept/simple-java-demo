package com.example.model.kafka.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersData {
    private Long id;
    private Long departmentId;
    private Long unitId;
    private String name;
    private String email;
    private boolean isActive;
    private boolean isDeleted;
    private boolean sendMail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
