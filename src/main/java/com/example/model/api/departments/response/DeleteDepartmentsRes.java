package com.example.model.api.departments.response;

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
public class DeleteDepartmentsRes implements Serializable {
    private static final long serialVersionUID = -7119555361680606954L;

    private String code;
    private AttributeDepartments attribute;
}
