package com.example.model.api.users.response;

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
public class InsertUsersRes implements Serializable {
    private static final long serialVersionUID = -879087206044843721L;

    private String id;

    @JsonProperty("department_id")
    private String departmentId;

    @JsonProperty("unit_id")
    private String unitId;

    private AttributeUsers attribute;
}
