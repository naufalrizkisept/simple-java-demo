package com.example.model.api.units.request;

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
public class UpdateUnitsReq implements Serializable {
    private static final long serialVersionUID = 6150056400415123455L;

    private Long id;

    @JsonProperty("department_id")
    private Long departmentId;

    private AttributeUnits attribute;
}
