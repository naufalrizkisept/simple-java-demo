package com.example.model.api.users.request;

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
public class UpdateUsersReq implements Serializable {
    private static final long serialVersionUID = 8402871486986979650L;

    private String email;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("unit_id")
    private Long unitId;

    private String name;
}
