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
public class UnitsData {
    private Long id;
    private Long departmentId;
    private String name;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
