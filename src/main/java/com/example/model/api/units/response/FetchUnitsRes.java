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
public class FetchUnitsRes implements Serializable {
    private static final long serialVersionUID = -9063064248251845511L;

    private String id;
    private String department;
    private AttributeUnits attribute;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
