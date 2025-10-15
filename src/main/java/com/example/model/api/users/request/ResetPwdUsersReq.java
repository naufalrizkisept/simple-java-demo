package com.example.model.api.users.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwdUsersReq implements Serializable {
    private static final long serialVersionUID = 7985338901244717621L;

    private String email;
    private String password;
}
