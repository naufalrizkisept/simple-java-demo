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
public class DepartmentsData {
    private Long id;
    private String code;
    private String name;
    private String key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}