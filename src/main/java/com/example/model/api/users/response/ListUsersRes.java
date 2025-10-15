package com.example.model.api.users.response;

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
public class ListUsersRes implements Serializable {
    private static final long serialVersionUID = 3541538741079606L;

    @JsonProperty("list_users")
    List<FetchUsersRes> listUsers;
}
