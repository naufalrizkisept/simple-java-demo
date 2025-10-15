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
public class UpdateDepartmentsReq implements Serializable {
    private static final long serialVersionUID = -1038322689904149075L;

    private Long id;
    private String code;
    private AttributeDepartments attribute;
}
