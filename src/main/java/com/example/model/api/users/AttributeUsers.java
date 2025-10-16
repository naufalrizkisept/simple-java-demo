package com.example.model.api.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeUsers implements Serializable {
    private static final long serialVersionUID = -2509759730874397021L;

    private String name;
    private String email;
}
