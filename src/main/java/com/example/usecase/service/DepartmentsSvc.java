package com.example.usecase.service;

import com.example.exception.UniqueException;
import com.example.model.api.departments.request.FetchDepartmentsReq;
import com.example.model.api.departments.request.InsertDepartmentsReq;
import com.example.model.api.departments.request.UpdateDepartmentsReq;
import com.example.model.api.departments.response.DeleteDepartmentsRes;
import com.example.model.api.departments.response.FetchDepartmentsRes;
import com.example.model.api.departments.response.ListDepartmentsRes;
import com.example.model.db.DepartmentsDao;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.repository.DepartmentsRepo;
import com.example.usecase.helper.ValidationHelper;
import com.example.usecase.kafka.producer.DepartmentsEventProducer;
import com.example.util.MappingUtil;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.constant.GeneralConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentsSvc {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DepartmentsRepo departmentsRepo;
    private final DepartmentsEventProducer eventProducer;

    private static final Long DEPARTMENTS_CACHE_DETAIL_TTL = 600L;
    private static final String DEPARTMENT_CACHE_KEY = "department:all";
    private static final String DEPARTMENT_CACHE_SUB_KEY = "department:";

    @Transactional
    public ApiBaseResponse<FetchDepartmentsRes> insertDepartment(InsertDepartmentsReq request, Metadata metadata) {
        log.info("Inserting a new department. CODE: {} | NAME: {} | KEY: {}", request.getCode(), request.getAttribute().getName(), request.getAttribute().getName());
        FetchDepartmentsRes response;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateInsertDepartmentsReq(request);

            // Check data already exists or not
            DepartmentsDao valDept = departmentsRepo.fetchByCode(request.getCode());
            if (valDept != null) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Insert to database
            ValidationHelper.checkDepartmentsName(request.getAttribute().getName());
            DepartmentsDao data = DepartmentsDao.builder()
                    .code(request.getCode())
                    .name(request.getAttribute().getName())
                    .key(request.getAttribute().getKey())
                    .build();
            DepartmentsDao newDept = departmentsRepo.save(data);

            // Mapping response
            newDept = departmentsRepo.fetchByCode(newDept.getCode());
            response = MappingUtil.mapFetchDepartmentsRes(newDept);

            // Send Kafka event
            eventProducer.sendDepartmentCreated(newDept, metadata.getRequestId());

            // Invalidate current cache
            Set<Object> keys = redisTemplate.opsForSet().members(DEPARTMENT_CACHE_KEY);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys.toString());
                log.info("Deleted {} keys from Redis", keys.size());
            }
            redisTemplate.delete(DEPARTMENT_CACHE_KEY);
        } catch (Exception e) {
            log.error("Error when inserting a new department, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<FetchDepartmentsRes> updateDepartmentById(UpdateDepartmentsReq request, Metadata metadata) {
        log.info("Updating existing department by ID: {}. NEW NAME: {} | NEW KEY: {} | NEW CODE: {}", request.getId(), request.getAttribute().getName(), request.getAttribute().getKey(), request.getCode());
        FetchDepartmentsRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateDepartmentsReq(request);

            // Checking data already exists or not
            ValidationHelper.checkDepartmentsName(request.getAttribute().getName());
            DepartmentsDao valDept = departmentsRepo.fetchByCode(request.getCode());
            if (valDept != null) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Update to database
            int isSuccess = departmentsRepo.updateById(request.getAttribute().getName(), request.getAttribute().getName(), String.valueOf(request.getId()));
            log.info("Update value: {}", isSuccess);

            if (isSuccess > 0) {
                DepartmentsDao data = departmentsRepo.fetchById(String.valueOf(request.getId()));
                response = MappingUtil.mapFetchDepartmentsRes(data);

                // Send Kafka event
                eventProducer.sendDepartmentUpdated(data, metadata.getRequestId());

                // Update current cache
                String cacheKey = Strings.concat(DEPARTMENT_CACHE_SUB_KEY, String.valueOf(request.getId()));
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));

                log.info("Department ID: {} successfully cached.", data.getId());
                redisTemplate.opsForSet().add(DEPARTMENT_CACHE_KEY, cacheKey);
                redisTemplate.expire(DEPARTMENT_CACHE_KEY, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));
            }
        } catch (Exception e) {
            log.error("Error when updating existing department, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<FetchDepartmentsRes> updateDepartmentByCode(UpdateDepartmentsReq request, Metadata metadata) {
        log.info("Updating existing department by code: {}. NEW NAME: {} | NEW KEY: {}", request.getCode(), request.getAttribute().getName(), request.getAttribute().getKey());
        FetchDepartmentsRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateDepartmentsReq(request);

            // Checking data already exists or not
            ValidationHelper.checkDepartmentsName(request.getAttribute().getName());
            DepartmentsDao valDept = departmentsRepo.fetchByCode(request.getCode());
            if (valDept != null) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Update to database
            int isSuccess = departmentsRepo.updateByCode(request.getAttribute().getName(), request.getAttribute().getKey(), request.getCode());
            log.info("Update value: {}", isSuccess);

            // Mapping response
            if (isSuccess > 0) {
                DepartmentsDao data = departmentsRepo.fetchByCode(request.getCode());
                response = MappingUtil.mapFetchDepartmentsRes(data);

                // Send Kafka event
                eventProducer.sendDepartmentUpdated(data, metadata.getRequestId());

                // Invalidate current cache
                String cacheKey = Strings.concat(DEPARTMENT_CACHE_SUB_KEY, String.valueOf(request.getId()));
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));
                redisTemplate.opsForSet().add(DEPARTMENT_CACHE_KEY, cacheKey);
                redisTemplate.expire(DEPARTMENT_CACHE_KEY, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));

                log.info("Department ID {} successfully cached.", data.getId());
            }
        } catch (Exception e) {
            log.error("Error when updating existing department, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    public ApiBaseResponse<ListDepartmentsRes> fetchAllDepartments(Metadata metadata) {
        log.info("Fetching all departments...");
        ListDepartmentsRes response;
        try {
            // Try to get cached keys first
            Set<Object> cachedKeys = redisTemplate.opsForSet().members(DEPARTMENT_CACHE_KEY);
            List<FetchDepartmentsRes> departmentList = new ArrayList<>();

            if (!cachedKeys.isEmpty()) {
                log.info("Cache found for departments, loading from Redis...");

                // Get all cached department data
                for (Object key : cachedKeys) {
                    Object cachedValue = redisTemplate.opsForValue().get(key);
                    if (cachedValue instanceof FetchDepartmentsRes) {
                        departmentList.add((FetchDepartmentsRes) cachedValue);
                    } else {
                        // handle deserialized object (e.g., JSON string)
                        FetchDepartmentsRes mapped = MappingUtil.convertToFetchDepartmentsRes(cachedValue);
                        departmentList.add(mapped);
                    }
                }

                // If cache returned results, skip DB
                if (!departmentList.isEmpty()) {
                    response = new ListDepartmentsRes();
                    response.setListDepartments(departmentList);
                    log.info("Returning department data from Redis cache ({} items).", departmentList.size());
                    return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
                }
            }

            // Fetch all data from database
            log.info("Cache empty or missing. Fetching departments from database...");
            List<DepartmentsDao> listDept = departmentsRepo.fetchAll();

            // Mapping response
            response = MappingUtil.mapListDepartmentsRes(listDept);

            // Set cache
            for (DepartmentsDao dept : listDept) {
                String cacheKey = Strings.concat(DEPARTMENT_CACHE_SUB_KEY, String.valueOf(dept.getId()));
                FetchDepartmentsRes res = MappingUtil.mapFetchDepartmentsRes(dept);
                redisTemplate.opsForValue().set(cacheKey, res, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));

                redisTemplate.opsForSet().add(DEPARTMENT_CACHE_KEY, cacheKey);
                redisTemplate.expire(DEPARTMENT_CACHE_KEY, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));
            }
            log.info("Departments successfully cached.");
        } catch (Exception e) {
            log.error("Error when fetching all departments, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    public ApiBaseResponse<FetchDepartmentsRes> fetchDepartmentById(Long id, Metadata metadata) {
        log.info("Fetching department data by ID: {}", id);
        FetchDepartmentsRes response;
        String cacheKey = Strings.concat(DEPARTMENT_CACHE_SUB_KEY, String.valueOf(id));
        try {
            // Try to fetch from cache first
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

            log.info("Cache hit for department ID {}. Loading from Redis...", id);

            // Deserialize cached object if necessary
            response = (cachedValue instanceof FetchDepartmentsRes)
                    ? (FetchDepartmentsRes) cachedValue
                    : MappingUtil.convertToFetchDepartmentsRes(cachedValue);

            if (response != null) {
                log.info("Returning department data from Redis cache (ID: {}).", id);
                return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
            }

            // Fetch all data from database
            log.info("Cache empty or missing. Fetching departments from database...");

            // Mapping to request
            FetchDepartmentsReq request = FetchDepartmentsReq.builder()
                    .id(id)
                    .build();

            // Checking mandatory field(s)
            ValidationHelper.validateFetchDepartmentsReq(request);

            // Fetch data from ID
            DepartmentsDao data = departmentsRepo.fetchById(request.getId().toString());
            if (data == null) {
                log.warn("Department not found in database for ID: {}", id);
                return ResponseUtil.buildResponse(STATUS_CODE_NOT_FOUND, ERROR_DATA_NOT_FOUND, null);
            }

            // Mapping response
            response = MappingUtil.mapFetchDepartmentsRes(data);

            // Send Kafka event
            eventProducer.sendDepartmentFetched(data, metadata.getRequestId());

            // Set cache
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));

            redisTemplate.opsForSet().add(DEPARTMENT_CACHE_KEY, cacheKey);
            redisTemplate.expire(DEPARTMENT_CACHE_KEY, Duration.ofSeconds(DEPARTMENTS_CACHE_DETAIL_TTL));
            log.info("Department ID {} successfully cached.", id);
        } catch (Exception e) {
            log.error("Error when fetching department data, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<DeleteDepartmentsRes> deleteDepartmentById(Long id, Metadata metadata) {
        log.info("Deleting existing department with ID: {}", id);
        DeleteDepartmentsRes response;
        String cacheKey = Strings.concat(DEPARTMENT_CACHE_SUB_KEY, String.valueOf(id));
        try {
            // Initial response mapping
            DepartmentsDao data = departmentsRepo.fetchById(String.valueOf(id));
            response = MappingUtil.mapDeleteDepartmentsRes(data);

            // Check data before deleting
            ValidationHelper.checkUnitsInDepartments(data);

            // Delete from database
            departmentsRepo.deleteById(String.valueOf(id));

            // Send Kafka event
            eventProducer.sendDepartmentDeleted(data, metadata.getRequestId());

            // Delete cache
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("Error when deleting existing department, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }
}
