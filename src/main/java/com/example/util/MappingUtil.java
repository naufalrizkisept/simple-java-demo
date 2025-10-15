package com.example.util;

import com.example.model.api.departments.AttributeDepartments;
import com.example.model.api.departments.response.DeleteDepartmentsRes;
import com.example.model.api.departments.response.FetchDepartmentsRes;
import com.example.model.api.departments.response.ListDepartmentsRes;
import com.example.model.api.units.AttributeUnits;
import com.example.model.api.units.response.DeleteUnitsRes;
import com.example.model.api.units.response.FetchUnitsRes;
import com.example.model.api.units.response.InsertUnitsRes;
import com.example.model.api.units.response.ListUnitsRes;
import com.example.model.api.users.AttributeUsers;
import com.example.model.api.users.response.DeleteUsersRes;
import com.example.model.api.users.response.FetchUsersRes;
import com.example.model.api.users.response.InsertUsersRes;
import com.example.model.api.users.response.ListUsersRes;
import com.example.model.db.DepartmentsDao;
import com.example.model.db.UnitsDao;
import com.example.model.db.UsersDao;
import com.example.model.kibana.KibanaRequest;
import com.example.model.kibana.LogKibanaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.*;

import static com.example.constant.GeneralConstant.DATE_TIME_FORMAT;

public class MappingUtil {
    private MappingUtil() {}

    // DEPARTMENTS RESPONSE MAPPING
    public static FetchDepartmentsRes mapFetchDepartmentsRes(DepartmentsDao data) {
        AttributeDepartments attribute = AttributeDepartments.builder()
                .name(data.getName())
                .key(data.getKey())
                .build();

        return FetchDepartmentsRes.builder()
                .id(String.valueOf(data.getId()))
                .code(data.getCode())
                .attribute(attribute)
                .createdAt(DateUtil.localDateTimeToString(data.getCreatedAt(), DATE_TIME_FORMAT))
                .updatedAt(DateUtil.localDateTimeToString(data.getUpdatedAt(), DATE_TIME_FORMAT))
                .build();
    }

    public static ListDepartmentsRes mapListDepartmentsRes(List<DepartmentsDao> data) {
        List<FetchDepartmentsRes> listRes = Optional.ofNullable(data)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(MappingUtil::mapFetchDepartmentsRes)
                .toList();

        return ListDepartmentsRes.builder()
                .listDepartments(listRes)
                .build();
    }

    public static DeleteDepartmentsRes mapDeleteDepartmentsRes(DepartmentsDao data) {
        AttributeDepartments attribute = AttributeDepartments.builder()
                .name(data.getName())
                .key(data.getKey())
                .build();

        return DeleteDepartmentsRes.builder()
                .code(data.getCode())
                .attribute(attribute)
                .build();
    }

    public static FetchDepartmentsRes convertToFetchDepartmentsRes(Object cachedValue) {
        if (cachedValue == null) {
            return null;
        }

        // Correct type
        if (cachedValue instanceof FetchDepartmentsRes) {
            return (FetchDepartmentsRes) cachedValue;
        }

        // JSON string
        if (cachedValue instanceof String) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue((String) cachedValue, FetchDepartmentsRes.class);
            } catch (Exception e) {
                return null;
            }
        }

        // 3JDK serializer return a LinkedHashMap
        if (cachedValue instanceof Map) {
            try {
                Map<?, ?> map = (Map<?, ?>) cachedValue;

                // Attribute
                Map<?, ?> attrMap = (Map<?, ?>) map.get("attribute");
                AttributeDepartments attribute = AttributeDepartments.builder()
                        .name(Objects.toString(attrMap != null ? attrMap.get("name") : null, null))
                        .key(Objects.toString(attrMap != null ? attrMap.get("key") : null, null))
                        .build();

                return FetchDepartmentsRes.builder()
                        .id(Objects.toString(map.get("id"), null))
                        .code(Objects.toString(map.get("code"), null))
                        .attribute(attribute)
                        .createdAt(Objects.toString(map.get("createdAt"), null))
                        .updatedAt(Objects.toString(map.get("updatedAt"), null))
                        .build();

            } catch (Exception e) {
                return null;
            }
        }

        // Fallback — unknown format
        return null;
    }

    /* ----------------------------------------------------------------------------- */

    // UNITS RESPONSE MAPPING
    public static FetchUnitsRes mapFetchUnitsRes(UnitsDao data) {
        AttributeUnits attribute = AttributeUnits.builder()
                .name(data.getName())
                .address(data.getAddress())
                .build();

        return FetchUnitsRes.builder()
                .id(String.valueOf(data.getId()))
                .department(data.getDepartment() != null ? data.getDepartment().getName() : String.valueOf(data.getDepartmentId()))
                .attribute(attribute)
                .createdAt(DateUtil.localDateTimeToString(data.getCreatedAt(), DATE_TIME_FORMAT))
                .updatedAt(DateUtil.localDateTimeToString(data.getUpdatedAt(), DATE_TIME_FORMAT))
                .build();
    }

    public static ListUnitsRes mapListUnitsRes(List<UnitsDao> data) {
        List<FetchUnitsRes> listRes = Optional.ofNullable(data)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(MappingUtil::mapFetchUnitsRes)
                .toList();

        return ListUnitsRes.builder()
                .listUnits(listRes)
                .build();
    }

    public static InsertUnitsRes mapInsertUnitsRes(UnitsDao data) {
        AttributeUnits attribute = AttributeUnits.builder()
                .address(data.getAddress())
                .name(data.getName())
                .build();

        return InsertUnitsRes.builder()
                .id(String.valueOf(data.getId()))
                .departmentId(String.valueOf(data.getDepartmentId()))
                .attribute(attribute)
                .build();
    }

    public static DeleteUnitsRes mapDeleteUnitsRes(UnitsDao data) {
        AttributeUnits attribute = AttributeUnits.builder()
                .address(data.getAddress())
                .name(data.getName())
                .build();

        return DeleteUnitsRes.builder()
                .departmentId(String.valueOf(data.getDepartmentId()))
                .attribute(attribute)
                .build();
    }

    public static FetchUnitsRes convertToFetchUnitsRes(Object cachedValue) {
        if (cachedValue == null) {
            return null;
        }

        // Correct type
        if (cachedValue instanceof FetchUnitsRes) {
            return (FetchUnitsRes) cachedValue;
        }

        // JSON string
        if (cachedValue instanceof String) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue((String) cachedValue, FetchUnitsRes.class);
            } catch (Exception e) {
                return null;
            }
        }

        // 3JDK serializer return a LinkedHashMap
        if (cachedValue instanceof Map) {
            try {
                Map<?, ?> map = (Map<?, ?>) cachedValue;

                // Attribute
                Map<?, ?> attrMap = (Map<?, ?>) map.get("attribute");
                AttributeUnits attribute = AttributeUnits.builder()
                        .name(Objects.toString(attrMap != null ? attrMap.get("name") : null, null))
                        .address(Objects.toString(attrMap != null ? attrMap.get("address") : null, null))
                        .build();

                return FetchUnitsRes.builder()
                        .id(Objects.toString(map.get("id"), null))
                        .department(Objects.toString(map.get("department"), null))
                        .attribute(attribute)
                        .createdAt(Objects.toString(map.get("createdAt"), null))
                        .updatedAt(Objects.toString(map.get("updatedAt"), null))
                        .build();

            } catch (Exception e) {
                return null;
            }
        }

        // Fallback — unknown format
        return null;
    }

    /* ----------------------------------------------------------------------------- */

    // USERS RESPONSE MAPPING
    public static FetchUsersRes mapFetchUsersRes(UsersDao data) {
        AttributeUsers attribute = AttributeUsers.builder()
                .name(data.getName())
                .email(data.getEmail())
                .build();

        return FetchUsersRes.builder()
                .id(String.valueOf(data.getId()))
                .department(data.getDepartment() != null ? data.getDepartment().getName() : String.valueOf(data.getDepartmentId()))
                .unit(data.getUnit() != null ? data.getUnit().getName() : String.valueOf(data.getUnitId()))
                .attribute(attribute)
                .isActive(data.isActive())
                .isDeleted(data.isDeleted())
                .sendMail(data.isSendMail())
                .createdAt(DateUtil.localDateTimeToString(data.getCreatedAt(), DATE_TIME_FORMAT))
                .updatedAt(DateUtil.localDateTimeToString(data.getUpdatedAt(), DATE_TIME_FORMAT))
                .build();
    }

    public static ListUsersRes mapListUsersRes(List<UsersDao> data) {
        List<FetchUsersRes> listRes = Optional.ofNullable(data)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(MappingUtil::mapFetchUsersRes)
                .toList();

        return ListUsersRes.builder()
                .listUsers(listRes)
                .build();
    }

    public static InsertUsersRes mapInsertUsersRes(UsersDao data) {
        AttributeUsers attribute = AttributeUsers.builder()
                .name(data.getName())
                .email(data.getEmail())
                .build();

        return InsertUsersRes.builder()
                .id(String.valueOf(data.getId()))
                .departmentId(String.valueOf(data.getDepartmentId()))
                .unitId(String.valueOf(data.getUnitId()))
                .attribute(attribute)
                .build();
    }

    public static DeleteUsersRes mapDeleteUsersRes(UsersDao data) {
        AttributeUsers attribute = AttributeUsers.builder()
                .name(data.getName())
                .email(data.getEmail())
                .build();

        return DeleteUsersRes.builder()
                .departmentId(String.valueOf(data.getDepartmentId()))
                .unitId(String.valueOf(data.getUnitId()))
                .attribute(attribute)
                .build();
    }

    public static FetchUsersRes convertToFetchUsersRes(Object cachedValue) {
        if (cachedValue == null) {
            return null;
        }

        // Correct type
        if (cachedValue instanceof FetchUsersRes) {
            return (FetchUsersRes) cachedValue;
        }

        // JSON string
        if (cachedValue instanceof String) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue((String) cachedValue, FetchUsersRes.class);
            } catch (Exception e) {
                return null;
            }
        }

        // 3JDK serializer return a LinkedHashMap
        if (cachedValue instanceof Map) {
            try {
                Map<?, ?> map = (Map<?, ?>) cachedValue;

                // Attribute
                Map<?, ?> attrMap = (Map<?, ?>) map.get("attribute");
                AttributeUsers attribute = AttributeUsers.builder()
                        .name(Objects.toString(attrMap != null ? attrMap.get("name") : null, null))
                        .email(Objects.toString(attrMap != null ? attrMap.get("email") : null, null))
                        .build();

                return FetchUsersRes.builder()
                        .id(Objects.toString(map.get("id"), null))
                        .department(Objects.toString(map.get("department"), null))
                        .unit(Objects.toString(map.get("unit"), null))
                        .attribute(attribute)
                        .createdAt(Objects.toString(map.get("createdAt"), null))
                        .updatedAt(Objects.toString(map.get("updatedAt"), null))
                        .build();

            } catch (Exception e) {
                return null;
            }
        }

        // Fallback — unknown format
        return null;
    }

    /* ----------------------------------------------------------------------------- */
}
