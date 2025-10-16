package com.example.model.api.departments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDepartments implements Serializable {
    private static final long serialVersionUID = 4851156828272054155L;

    private String name;
    private String key;
}
