package com.example.model.api.departments.request;

import com.example.model.api.departments.AttributeDepartments;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsertDepartmentsReq implements Serializable {
    private static final long serialVersionUID = 769613854636492516L;

    private String code;
    private AttributeDepartments attribute;
}
