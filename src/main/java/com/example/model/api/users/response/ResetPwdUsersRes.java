package com.example.model.api.users.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwdUsersRes implements Serializable {
    private static final long serialVersionUID = -8935446658500252521L;

    private String status;
}
