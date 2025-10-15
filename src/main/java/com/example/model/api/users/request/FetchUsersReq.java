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
public class FetchUsersReq implements Serializable {
    private static final long serialVersionUID = -8187908146204263493L;

    private Long id;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("unit_id")
    private Long unitId;

    private String email;
}
