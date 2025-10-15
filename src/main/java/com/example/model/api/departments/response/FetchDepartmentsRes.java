package com.example.model.api.departments.response;

import com.example.model.api.departments.AttributeDepartments;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchDepartmentsRes implements Serializable {
    private static final long serialVersionUID = -2765942539932745996L;

    private String id;
    private String code;
    private AttributeDepartments attribute;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
