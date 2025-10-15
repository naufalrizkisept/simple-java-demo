package com.example.model.api.departments.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListDepartmentsRes implements Serializable {
    private static final long serialVersionUID = -7049603592654408092L;

    @JsonProperty("list_departments")
    List<FetchDepartmentsRes> listDepartments;
}
