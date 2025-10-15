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
public class FetchUsersRes implements Serializable {
    private static final long serialVersionUID = 6800694832649796735L;

    private String id;
    private String department;
    private String unit;
    private AttributeUsers attribute;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

    @JsonProperty("send_mail")
    private boolean sendMail;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
