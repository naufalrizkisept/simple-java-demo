package com.example.usecase.delivery;

import com.example.model.api.departments.request.InsertDepartmentsReq;
import com.example.model.api.departments.request.UpdateDepartmentsReq;
import com.example.model.api.departments.response.DeleteDepartmentsRes;
import com.example.model.api.departments.response.FetchDepartmentsRes;
import com.example.model.api.departments.response.ListDepartmentsRes;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.service.DepartmentsSvc;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentsDelivery {
    private final DepartmentsSvc departmentsSvc;

    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> insertDepartment(InsertDepartmentsReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.insertDepartment(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> updateDepartmentById(UpdateDepartmentsReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.updateDepartmentById(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> updateDepartmentByCode(UpdateDepartmentsReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.updateDepartmentByCode(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<ListDepartmentsRes>> fetchAllDepartments(Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.fetchAllDepartments(metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchDepartmentsRes>> fetchDepartmentById(Long request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.fetchDepartmentById(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<DeleteDepartmentsRes>> deleteDepartmentById(Long id, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(departmentsSvc.deleteDepartmentById(id, metadata));
    }
}
