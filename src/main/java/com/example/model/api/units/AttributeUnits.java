package com.example.model.api.units;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeUnits implements Serializable {
    private static final long serialVersionUID = 8974824826526281114L;

    private String name;
    private String address;
}
