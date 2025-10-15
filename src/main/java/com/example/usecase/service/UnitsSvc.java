package com.example.usecase.service;

import com.example.exception.UniqueException;
import com.example.model.api.units.request.FetchUnitsReq;
import com.example.model.api.units.request.InsertUnitsReq;
import com.example.model.api.units.request.UpdateUnitsReq;
import com.example.model.api.units.response.DeleteUnitsRes;
import com.example.model.api.units.response.FetchUnitsRes;
import com.example.model.api.units.response.InsertUnitsRes;
import com.example.model.api.units.response.ListUnitsRes;
import com.example.model.db.UnitsDao;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.repository.UnitsRepo;
import com.example.usecase.helper.ValidationHelper;
import com.example.usecase.kafka.producer.UnitsEventProducer;
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
public class UnitsSvc {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UnitsRepo unitsRepo;
    private final UnitsEventProducer eventProducer;

    private static final Long UNITS_CACHE_DETAIL_TTL = 600L;
    private static final String UNIT_CACHE_KEY = "unit:all";
    private static final String UNIT_CACHE_SUB_KEY = "unit:";

    @Transactional
    public ApiBaseResponse<InsertUnitsRes> insertUnit(InsertUnitsReq request, Metadata metadata) {
        log.info("Inserting a new unit. NAME: {}, ADDRESS: {}", request.getAttribute().getName(), request.getAttribute().getAddress());
        InsertUnitsRes response;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateInsertUnitsRequest(request);

            // Checking data already exists or not
            List<UnitsDao> valUnits = unitsRepo.fetchByDeptId(request.getDepartmentId().toString());
            if (!valUnits.isEmpty() && valUnits.stream().anyMatch(
                    unit -> unit.getName().equalsIgnoreCase(request.getAttribute().getName()))) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Insert to database
            ValidationHelper.checkUnitsName(request.getAttribute().getName());
            UnitsDao data = UnitsDao.builder()
                    .departmentId(request.getDepartmentId())
                    .name(request.getAttribute().getName())
                    .address(request.getAttribute().getAddress())
                    .build();
            UnitsDao newUnit = unitsRepo.save(data);

            // Mapping response
            newUnit = unitsRepo.fetchById(newUnit.getId().toString());
            response = MappingUtil.mapInsertUnitsRes(newUnit);

            // Send Kafka event
            eventProducer.sendUnitCreated(data, metadata.getRequestId());

            // Invalidate current cache
            Set<Object> keys = redisTemplate.opsForSet().members(UNIT_CACHE_KEY);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys.toString());
                log.info("Deleted {} keys from Redis", keys.size());
            }
            redisTemplate.delete(UNIT_CACHE_KEY);
        } catch (Exception e) {
            log.error("Error when inserting a new unit, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<InsertUnitsRes> updateUnitById(UpdateUnitsReq request, Metadata metadata) {
        log.info("Updating existing unit by ID: {}. NEW DEPARTMENT: {} | NEW NAME: {} | NEW ADDRESS: {}", request.getId(), request.getDepartmentId(), request.getAttribute().getName(), request.getAttribute().getAddress());
        InsertUnitsRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateUnitsRequest(request);

            // Checking data already exists or not
            ValidationHelper.checkUnitsName(request.getAttribute().getName());
            List<UnitsDao> valUnits = unitsRepo.fetchByDeptId(request.getDepartmentId().toString());
            if (!valUnits.isEmpty() && valUnits.stream().anyMatch(
                    unit -> unit.getName().equalsIgnoreCase(request.getAttribute().getName()))) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Update to database
            int isSuccess = unitsRepo.updateById(request.getDepartmentId().toString(),
                    request.getAttribute().getName(), request.getAttribute().getAddress(), request.getId().toString());
            log.info("Update value: {}", isSuccess);

            // Mapping response
            if (isSuccess > 0) {
                UnitsDao data = unitsRepo.fetchById(request.getId().toString());
                response = MappingUtil.mapInsertUnitsRes(data);

                // Send Kafka event
                eventProducer.sendUnitUpdated(data, metadata.getRequestId());

                // Update current cache
                String cacheKey = Strings.concat(UNIT_CACHE_SUB_KEY, String.valueOf(request.getId()));
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(UNITS_CACHE_DETAIL_TTL));

                log.info("Unit ID: {} successfully cached.", data.getId());
                redisTemplate.opsForSet().add(UNIT_CACHE_KEY, cacheKey);
                redisTemplate.expire(UNIT_CACHE_KEY, Duration.ofSeconds(UNITS_CACHE_DETAIL_TTL));
            }
        } catch (Exception e) {
            log.error("Error when updating existing unit, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    public ApiBaseResponse<ListUnitsRes> fetchAllUnits(Metadata metadata) {
        log.info("Fetching all units...");
        ListUnitsRes response;
        try {
            // Try to get cached keys first
            Set<Object> cachedKeys = redisTemplate.opsForSet().members(UNIT_CACHE_KEY);
            List<FetchUnitsRes> unitList = new ArrayList<>();

            if (!cachedKeys.isEmpty()) {
                log.info("Cache found for units, loading from Redis...");

                // Get all cached units data
                for (Object key : cachedKeys) {
                    Object cachedValue = redisTemplate.opsForValue().get(key);
                    if (cachedValue instanceof FetchUnitsRes) {
                        unitList.add((FetchUnitsRes) cachedValue);
                    } else {
                        // handle deserialized object (e.g., JSON string)
                        FetchUnitsRes mapped = MappingUtil.convertToFetchUnitsRes(cachedValue);
                        unitList.add(mapped);
                    }
                }

                // If cache returned results, skip DB
                if (!unitList.isEmpty()) {
                    response = new ListUnitsRes();
                    response.setListUnits(unitList);
                    log.info("Returning unit data from Redis cache ({} items).", unitList.size());
                    return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
                }
            }

            // Fetch all data
            log.info("Cache empty or missing. Fetching units from database...");
            List<UnitsDao> listUnits = unitsRepo.fetchAll();

            // Mapping response
            response = MappingUtil.mapListUnitsRes(listUnits);

            // Set cache
            for (UnitsDao unit : listUnits) {
                String cacheKey = Strings.concat(UNIT_CACHE_SUB_KEY, String.valueOf(unit.getId()));
                FetchUnitsRes res = MappingUtil.mapFetchUnitsRes(unit);
                redisTemplate.opsForValue().set(cacheKey, res, Duration.ofSeconds(UNITS_CACHE_DETAIL_TTL));

                redisTemplate.opsForSet().add(UNIT_CACHE_KEY, cacheKey);
                redisTemplate.expire(UNIT_CACHE_KEY, Duration.ofSeconds(UNITS_CACHE_DETAIL_TTL));
            }
            log.info("Units successfully cached.");
        } catch (Exception e) {
            log.error("Error when fetching all units, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    public ApiBaseResponse<ListUnitsRes> fetchUnitsByDeptId(Long deptId, Metadata metadata) {
        log.info("Fetching unit(s) by department ID: {}", deptId);
        ListUnitsRes response;
        try {
            // Mapping to request
            FetchUnitsReq request = FetchUnitsReq.builder()
                    .departmentId(deptId)
                    .build();

            // Checking mandatory field(s)
            ValidationHelper.validateFetchUnitsRequest(request);

            // Fetching from foreign ID
            List<UnitsDao> listUnits = unitsRepo.fetchByDeptId(request.getDepartmentId().toString());
            if (listUnits.isEmpty()) {
                log.warn("Units not found in database for DEPT ID: {}", deptId);
                return ResponseUtil.buildResponse(STATUS_CODE_NOT_FOUND, ERROR_DATA_NOT_FOUND, null);
            }

            // Mapping response
            response = MappingUtil.mapListUnitsRes(listUnits);
        } catch (Exception e) {
            log.error("Error when fetching unit(s), caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    public ApiBaseResponse<FetchUnitsRes> fetchUnitById(Long id, Metadata metadata) {
        log.info("Fetching unit by ID: {}", id);
        FetchUnitsRes response;
        String cacheKey = Strings.concat(UNIT_CACHE_SUB_KEY, String.valueOf(id));
        try {
            // Try to fetch from cache first
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

            log.info("Cache hit for unit ID {}. Loading from Redis...", id);

            // Deserialize cached object if necessary
            response = (cachedValue instanceof FetchUnitsRes)
                    ? (FetchUnitsRes) cachedValue
                    : MappingUtil.convertToFetchUnitsRes(cachedValue);

            if (response != null) {
                log.info("Returning unit data from Redis cache (ID: {}).", id);
                return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
            }

            // Mapping to request
            log.info("Cache empty or missing. Fetching units from database...");
            FetchUnitsReq request = FetchUnitsReq.builder()
                    .id(id)
                    .build();

            // Checking mandatory field(s)
            ValidationHelper.validateFetchUnitsRequest(request);

            // Fetching from primary ID
            UnitsDao data = unitsRepo.fetchById(request.getId().toString());
            if (data == null) {
                log.warn("Unit not found in database for ID: {}", id);
                return ResponseUtil.buildResponse(STATUS_CODE_NOT_FOUND, ERROR_DATA_NOT_FOUND, null);
            }

            // Mapping response
            response = MappingUtil.mapFetchUnitsRes(data);

            // Send Kafka event
            eventProducer.sendUnitFetched(data, metadata.getRequestId());
        } catch (Exception e) {
            log.error("Error when fetching unit, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<DeleteUnitsRes> deleteUnitById(Long id, Metadata metadata) {
        log.info("Deleting existing unit with ID: {}", id);
        DeleteUnitsRes response;
        String cacheKey = Strings.concat(UNIT_CACHE_SUB_KEY, String.valueOf(id));
        try {
            // Initial response mapping
            UnitsDao data = unitsRepo.fetchById(String.valueOf(id));
            response = MappingUtil.mapDeleteUnitsRes(data);

            // Check data before deleting
            ValidationHelper.checkDeleteUnits(data);

            // Delete from database
            unitsRepo.deleteById(String.valueOf(id));

            // Send Kafka event
            eventProducer.sendUnitDeleted(data, metadata.getRequestId());

            // Delete cache
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("Error when deleting existing unit, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }
}
