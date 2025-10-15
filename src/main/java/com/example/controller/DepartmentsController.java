package com.example.controller;

import com.example.model.api.departments.request.InsertDepartmentsReq;
import com.example.model.api.departments.request.UpdateDepartmentsReq;
import com.example.model.api.departments.response.DeleteDepartmentsRes;
import com.example.model.api.departments.response.FetchDepartmentsRes;
import com.example.model.api.departments.response.ListDepartmentsRes;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.delivery.DepartmentsDelivery;
import com.example.util.MetadataUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/department")
public class DepartmentsController {
    private final DepartmentsDelivery departmentsDelivery;

    @PostMapping("/create")
    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> insertDepartment(@RequestBody InsertDepartmentsReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.insertDepartment(request, metadata);
    }

    @PutMapping("/update/id")
    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> updateDepartmentById(@RequestBody UpdateDepartmentsReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.updateDepartmentById(request, metadata);
    }

    @PutMapping("/update/code")
    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> updateDepartmentByCode(@RequestBody UpdateDepartmentsReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.updateDepartmentByCode(request, metadata);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiBaseResponse<ListDepartmentsRes>> fetchAllDepartments(HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.fetchAllDepartments(metadata);
    }

    @GetMapping("/fetch/id")
    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> fetchDepartmentById(@RequestParam(name = "department-id") Long request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.fetchDepartmentById(request, metadata);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiBaseResponse<DeleteDepartmentsRes>> deleteDepartmentByCode(@RequestParam(name = "department-id") Long id, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return departmentsDelivery.deleteDepartmentById(id, metadata);
    }
}
