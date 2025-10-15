package com.example.model.api.departments.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchDepartmentsReq implements Serializable {
    private static final long serialVersionUID = -2931809675332250352L;

    private Long id;
    private String code;
}
