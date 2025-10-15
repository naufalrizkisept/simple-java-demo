package com.example.controller;

import com.example.model.api.units.request.InsertUnitsReq;
import com.example.model.api.units.request.UpdateUnitsReq;
import com.example.model.api.units.response.DeleteUnitsRes;
import com.example.model.api.units.response.FetchUnitsRes;
import com.example.model.api.units.response.InsertUnitsRes;
import com.example.model.api.units.response.ListUnitsRes;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.delivery.UnitsDelivery;
import com.example.util.MetadataUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/unit")
public class UnitsController {
    private final UnitsDelivery unitsDelivery;

    @PostMapping("/create")
    public ResponseEntity<ApiBaseResponse<InsertUnitsRes>> insertUnit(@RequestBody InsertUnitsReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.insertUnit(request, metadata);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiBaseResponse<InsertUnitsRes>> updateUnitById(@RequestBody UpdateUnitsReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.updateUnitById(request, metadata);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiBaseResponse<ListUnitsRes>> fetchAllUnits(HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.fetchAllUnits(metadata);
    }

    @GetMapping("/fetch/dept")
    public ResponseEntity<ApiBaseResponse<ListUnitsRes>> fetchUnitsByDeptId(@RequestParam(name = "department-id") Long request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.fetchUnitsByDeptId(request, metadata);
    }

    @GetMapping("/fetch/id")
    public ResponseEntity<ApiBaseResponse<FetchUnitsRes>> fetchUnitById(@RequestParam(name = "unit-id") Long request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.fetchUnitById(request, metadata);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiBaseResponse<DeleteUnitsRes>> deleteUnitById(@RequestParam(name = "unit-id") Long id, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return unitsDelivery.deleteUnitById(id, metadata);
    }
}
