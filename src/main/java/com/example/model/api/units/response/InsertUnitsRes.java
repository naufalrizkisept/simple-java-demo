package com.example.model.api.units.response;

import com.example.model.api.units.AttributeUnits;
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
public class InsertUnitsRes implements Serializable {
    private static final long serialVersionUID = -3589939792237427026L;

    private String id;

    @JsonProperty("department_id")
    private String departmentId;

    private AttributeUnits attribute;
}
