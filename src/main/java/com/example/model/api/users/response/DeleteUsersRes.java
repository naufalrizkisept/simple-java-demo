package com.example.model.api.users.response;

import com.example.model.api.users.AttributeUsers;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUsersRes implements Serializable {
    private static final long serialVersionUID = 4092594190294373024L;

    @JsonProperty("department_id")
    private String departmentId;

    @JsonProperty("unit_id")
    private String unitId;

    private AttributeUsers attribute;
}
