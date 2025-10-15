package com.example.usecase.helper;

import com.example.exception.UniqueException;
import com.example.model.api.departments.request.FetchDepartmentsReq;
import com.example.model.api.departments.request.InsertDepartmentsReq;
import com.example.model.api.departments.request.UpdateDepartmentsReq;
import com.example.model.api.units.request.FetchUnitsReq;
import com.example.model.api.units.request.InsertUnitsReq;
import com.example.model.api.units.request.UpdateUnitsReq;
import com.example.model.api.users.request.FetchUsersReq;
import com.example.model.api.users.request.InsertUsersReq;
import com.example.model.api.users.request.ResetPwdUsersReq;
import com.example.model.api.users.request.UpdateUsersReq;
import com.example.model.db.DepartmentsDao;
import com.example.model.db.UnitsDao;
import lombok.extern.slf4j.Slf4j;

import static com.example.constant.GeneralConstant.*;

@Slf4j
public class ValidationHelper {
    private ValidationHelper() {}

    // MANDATORY VALIDATION
    public static boolean checkUpperCase(String input) {
        return !input.chars().allMatch(Character::isUpperCase);
    }

    public static boolean checkComplexity(String input) {
        boolean hasSymbol = input.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        boolean hasDigit = input.chars().anyMatch(Character::isDigit);
        boolean hasUppercase = input.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = input.chars().anyMatch(Character::isLowerCase);

        return hasSymbol && hasDigit && hasUppercase && hasLowercase;
    }

    /* ----------------------------------------------------------------------------- */

    // DEPARTMENTS VALIDATION
    public static void validateFetchDepartmentsReq(FetchDepartmentsReq request) {
        if (request.getId() == null && request.getCode() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateInsertDepartmentsReq(InsertDepartmentsReq request) {
        if (request.getCode() == null || request.getAttribute().getKey() == null || request.getAttribute().getName() == null) {
            throw new UniqueException(STATUS_CODE_NOT_FOUND, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateUpdateDepartmentsReq(UpdateDepartmentsReq request) {
        if (request.getId() == null || request.getCode() == null || (request.getAttribute().getName() == null && request.getAttribute().getKey() == null)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void checkDepartmentsName(String name) {
        if (checkUpperCase(name)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_STRING_FORMAT);
        }
    }

    public static void checkUnitsInDepartments(DepartmentsDao data) {
        if (!data.getListUnits().isEmpty()) {
            throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_EXIST);
        }
    }

    /* ----------------------------------------------------------------------------- */

    // UNITS VALIDATION
    public static void validateInsertUnitsRequest(InsertUnitsReq request) {
        if (request.getDepartmentId() == null || request.getAttribute().getName() == null ||
                request.getAttribute().getAddress() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateUpdateUnitsRequest(UpdateUnitsReq request) {
        if (request.getId() == null || (request.getDepartmentId() == null && request.getAttribute().getName() == null && request.getAttribute().getAddress() == null)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateFetchUnitsRequest(FetchUnitsReq request) {
        if (request.getId() == null && request.getDepartmentId() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void checkUnitsName(String name) {
        if (checkUpperCase(name)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_STRING_FORMAT);
        }
    }

    public static void checkDeleteUnits(UnitsDao data) {
        if (data.getDepartment() != null && !data.getDepartment().getListUnits().isEmpty()) {
            throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_EXIST);
        }
    }

    /* ----------------------------------------------------------------------------- */

    // USERS VALIDATION
    public static void validateRegisterUsersRequest(InsertUsersReq request) {
        if (request.getAttribute() == null || request.getPassword() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateResetPwdUsersRequest(ResetPwdUsersReq request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateUpdateUsersRequest(UpdateUsersReq request) {
        if (request.getEmail() == null || (request.getDepartmentId() == null && request.getUnitId() == null && request.getName() == null)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void validateFetchUsersRequest(FetchUsersReq request) {
        if (request.getEmail() == null) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }

    public static void checkUsersName(String name) {
        if (checkUpperCase(name)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_STRING_FORMAT);
        }
    }

    public static void checkPassword(String password) {
        if (password.length() != 12 && checkComplexity(password)) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_STRING_FORMAT);
        }
    }

    public static void checkUsersEmail(String email) {
        if (!email.contains("@") || !email.contains(".com")) {
            throw new UniqueException(STATUS_CODE_BAD_REQUEST, ERROR_REQUEST_MANDATORY);
        }
    }
}
