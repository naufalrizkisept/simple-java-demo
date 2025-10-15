package com.example.model.api.units.request;

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
public class FetchUnitsReq implements Serializable {
    private static final long serialVersionUID = 1057402157517700205L;

    private Long id;

    @JsonProperty("department_id")
    private Long departmentId;
}
