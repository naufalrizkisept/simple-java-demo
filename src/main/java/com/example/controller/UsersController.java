package com.example.controller;

import com.example.model.api.users.request.InsertUsersReq;
import com.example.model.api.users.request.ResetPwdUsersReq;
import com.example.model.api.users.request.UpdateUsersReq;
import com.example.model.api.users.response.*;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.delivery.UsersDelivery;
import com.example.util.MetadataUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UsersController {
    private final UsersDelivery usersDelivery;

    @PostMapping("/register")
    public ResponseEntity<ApiBaseResponse<InsertUsersRes>> registerUser(@RequestBody InsertUsersReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.registerUser(request, metadata);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiBaseResponse<ResetPwdUsersRes>> resetPasswordUser(@RequestBody ResetPwdUsersReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.resetPasswordUser(request, metadata);
    }

    @PutMapping("/activate-user")
    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> activateUser(@RequestBody UpdateUsersReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.activateUser(request, metadata);
    }

    @PutMapping("/update/email")
    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> updateUserByEmail(@RequestBody UpdateUsersReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.updateUserByEmail(request, metadata);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiBaseResponse<ListUsersRes>> fetchAllUsers(HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.fetchAllUsers(metadata);
    }

    @GetMapping("/fetch/email")
    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> fetchUserByEmail(@RequestParam(name = "email") String request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.fetchUserByEmail(request, metadata);
    }

    @PutMapping("/delete-flag")
    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> deleteFlagUser(@RequestBody UpdateUsersReq request, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.deleteFlagUser(request, metadata);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiBaseResponse<DeleteUsersRes>> deleteUserByEmail(@RequestParam(name = "email") String email, HttpServletRequest servletRequest) {
        Metadata metadata = MetadataUtil.constructMetadata(servletRequest);
        return usersDelivery.deleteUserByEmail(email, metadata);
    }
}
