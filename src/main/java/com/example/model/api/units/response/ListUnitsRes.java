package com.example.model.api.units.response;

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
public class ListUnitsRes implements Serializable {
    private static final long serialVersionUID = 1751277102414765182L;

    @JsonProperty("list_units")
    List<FetchUnitsRes> listUnits;
}
