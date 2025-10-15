package com.example.model.api.users.request;

import com.example.model.api.users.AttributeUsers;
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
public class InsertUsersReq implements Serializable {
    private static final long serialVersionUID = -8534698334587887405L;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("unit_id")
    private Long unitId;

    private AttributeUsers attribute;
    private String password;
}
