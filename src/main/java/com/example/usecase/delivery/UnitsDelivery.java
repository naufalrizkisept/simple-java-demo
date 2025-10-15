package com.example.usecase.delivery;

import com.example.model.api.units.request.InsertUnitsReq;
import com.example.model.api.units.request.UpdateUnitsReq;
import com.example.model.api.units.response.DeleteUnitsRes;
import com.example.model.api.units.response.FetchUnitsRes;
import com.example.model.api.units.response.InsertUnitsRes;
import com.example.model.api.units.response.ListUnitsRes;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.service.UnitsSvc;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitsDelivery {
    private final UnitsSvc unitsSvc;

    public ResponseEntity<ApiBaseResponse<InsertUnitsRes>> insertUnit(InsertUnitsReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.insertUnit(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<InsertUnitsRes>> updateUnitById(UpdateUnitsReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.updateUnitById(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<ListUnitsRes>> fetchAllUnits(Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.fetchAllUnits(metadata));
    }

    public ResponseEntity<ApiBaseResponse<ListUnitsRes>> fetchUnitsByDeptId(Long request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.fetchUnitsByDeptId(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchUnitsRes>> fetchUnitById(Long request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.fetchUnitById(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<DeleteUnitsRes>> deleteUnitById(Long id, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(unitsSvc.deleteUnitById(id, metadata));
    }
}
