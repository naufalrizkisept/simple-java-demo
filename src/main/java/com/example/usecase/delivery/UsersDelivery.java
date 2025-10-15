package com.example.usecase.delivery;

import com.example.model.api.users.request.InsertUsersReq;
import com.example.model.api.users.request.ResetPwdUsersReq;
import com.example.model.api.users.request.UpdateUsersReq;
import com.example.model.api.users.response.*;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.usecase.service.UsersSvc;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersDelivery {
    private final UsersSvc usersSvc;

    public ResponseEntity<ApiBaseResponse<InsertUsersRes>> registerUser(InsertUsersReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.registerUser(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<ResetPwdUsersRes>> resetPasswordUser(ResetPwdUsersReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.resetPasswordUser(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> activateUser(UpdateUsersReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.activateUser(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> updateUserByEmail(UpdateUsersReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.updateUserByEmail(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<ListUsersRes>> fetchAllUsers(Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.fetchAllUsers(metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> fetchUserByEmail(String request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.fetchUserByEmail(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<FetchUsersRes>> deleteFlagUser(UpdateUsersReq request, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.deleteFlagUser(request, metadata));
    }

    public ResponseEntity<ApiBaseResponse<DeleteUsersRes>> deleteUserByEmail(String email, Metadata metadata) {
        return ResponseUtil.buildHttpResponse(usersSvc.deleteUserByEmail(email, metadata));
    }
}
