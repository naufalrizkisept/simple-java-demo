package com.example.usecase.service;

import com.example.exception.UniqueException;
import com.example.model.api.users.request.FetchUsersReq;
import com.example.model.api.users.request.InsertUsersReq;
import com.example.model.api.users.request.ResetPwdUsersReq;
import com.example.model.api.users.request.UpdateUsersReq;
import com.example.model.api.users.response.*;
import com.example.model.db.UsersDao;
import com.example.model.internal.ApiBaseResponse;
import com.example.model.internal.Metadata;
import com.example.repository.UsersRepo;
import com.example.usecase.helper.ValidationHelper;
import com.example.usecase.kafka.producer.UsersEventProducer;
import com.example.util.Base64Util;
import com.example.util.MappingUtil;
import com.example.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UsersSvc {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UsersRepo usersRepo;
    private final UsersEventProducer eventProducer;
    private final PasswordEncoder passwordEncoder;

    private static final Long USER_CACHE_DETAIL_TTL = 600L;
    private static final String USER_CACHE_KEY = "user:all";
    private static final String USER_CACHE_SUB_KEY = "user:";

    @Transactional
    public ApiBaseResponse<InsertUsersRes> registerUser(InsertUsersReq request, Metadata metadata) {
        log.info("Registering new user...");
        InsertUsersRes response;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateRegisterUsersRequest(request);
            ValidationHelper.checkUsersEmail(request.getAttribute().getEmail());

            // Checking email already exists or not
            UsersDao valEmail = usersRepo.fetchByEmail(request.getAttribute().getEmail());
            if (valEmail != null) {
                throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_DATA_DUPLICATE);
            }

            // Decode password
            ValidationHelper.checkPassword(request.getPassword());
            String rawPassword = Base64Util.decode(request.getPassword());
            log.info("Raw password: {}", rawPassword);

            // Insert to database
            ValidationHelper.checkUsersName(request.getAttribute().getName());
            UsersDao data = UsersDao.builder()
                    .email(request.getAttribute().getEmail())
                    .password(passwordEncoder.encode(rawPassword))
                    .departmentId(request.getDepartmentId())
                    .name(request.getAttribute().getName())
                    .unitId(request.getUnitId())
                    .build();
            UsersDao newUser = usersRepo.save(data);

            // Mapping response
            newUser = usersRepo.fetchByEmail(newUser.getEmail());
            response = MappingUtil.mapInsertUsersRes(newUser);

            // Send Kafka event
            eventProducer.sendUserCreated(data, metadata.getRequestId());

            // Invalidate current cache
            Set<Object> keys = redisTemplate.opsForSet().members(USER_CACHE_KEY);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys.toString());
                log.info("Deleted {} keys from Redis", keys.size());
            }
            redisTemplate.delete(USER_CACHE_KEY);
        } catch (UniqueException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error when registering user, caused by: " + e.getMessage());
            throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_SYSTEM_ERROR);
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<ResetPwdUsersRes> resetPasswordUser(ResetPwdUsersReq request, Metadata metadata) {
        log.info("Start resetting password by email: {}", request.getEmail());
        ResetPwdUsersRes response = ResetPwdUsersRes.builder()
                .status(ERROR_ACCESS)
                .build();
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateResetPwdUsersRequest(request);

            // Checking data is null or not by email
            UsersDao data = usersRepo.fetchByEmail(request.getEmail());
            if (data == null) {
                throw new UniqueException(STATUS_CODE_NOT_FOUND, ERROR_DATA_NOT_FOUND);
            }

            // Decode password
            String rawPassword = Base64Util.decode(request.getPassword());
            log.info("New raw Password: {}", rawPassword);

            String encodedPassword = passwordEncoder.encode(rawPassword);
            int isSuccess = usersRepo.resetPassword(request.getEmail(), encodedPassword);
            log.info("Reset password value: {}", isSuccess);

            if (isSuccess > 0) {
                response.setStatus(STATUS_MESSAGE_SUCCESS);
            }
        } catch (UniqueException e) {
            log.error("Error when resetting password due to: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error when reset, exception: " + e.getMessage());
            throw new UniqueException(STATUS_CODE_SYSTEM_ERROR, ERROR_SYSTEM_ERROR);
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<FetchUsersRes> activateUser(UpdateUsersReq request, Metadata metadata) {
        log.info("Activate user by email: {}", request.getEmail());
        FetchUsersRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateUsersRequest(request);

            // Update to database
            int isSuccess = usersRepo.activateByEmail(request.getEmail());
            log.info("Update user value: {}", isSuccess);

            // Mapping response
            if (isSuccess > 0) {
                UsersDao data = usersRepo.fetchByEmail(request.getEmail());
                response = MappingUtil.mapFetchUsersRes(data);

                // Send Kafka event
                eventProducer.sendUserUpdated(data, metadata.getRequestId());

                // Update current cache
                String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, request.getEmail());
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));

                log.info("User email {} successfully cached.", data.getEmail());
                redisTemplate.opsForSet().add(USER_CACHE_KEY, cacheKey);
                redisTemplate.expire(USER_CACHE_KEY, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));
            }
        } catch (Exception e) {
            log.error("Error when activating user, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<FetchUsersRes> updateUserByEmail(UpdateUsersReq request, Metadata metadata) {
        log.info("Updating user by email: {}", request.getEmail());
        FetchUsersRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateUsersRequest(request);
            ValidationHelper.checkUsersName(request.getName());

            // Update to database
            int isSuccess = usersRepo.updateByEmail(request.getDepartmentId().toString(), request.getUnitId().toString(),
                    request.getName(), request.getEmail());
            log.info("Update value: {}", isSuccess);

            // Mapping response
            if (isSuccess > 0) {
                UsersDao data = usersRepo.fetchByEmail(request.getEmail());
                response = MappingUtil.mapFetchUsersRes(data);

                // Send Kafka event
                eventProducer.sendUserUpdated(data, metadata.getRequestId());

                // Update current cache
                String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, request.getEmail());
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));

                log.info("User email: {} successfully cached.", data.getEmail());
                redisTemplate.opsForSet().add(USER_CACHE_KEY, cacheKey);
                redisTemplate.expire(USER_CACHE_KEY, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));
            }
        } catch (Exception e) {
            log.error("Error when updating user by email, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    public ApiBaseResponse<ListUsersRes> fetchAllUsers(Metadata metadata) {
        log.info("Fetching all users...");
        ListUsersRes response;
        try {
            // Try to get cached keys first
            Set<Object> cachedKeys = redisTemplate.opsForSet().members(USER_CACHE_KEY);
            List<FetchUsersRes> userList = new ArrayList<>();

            if (!cachedKeys.isEmpty()) {
                log.info("Cache found for units, loading from Redis...");

                // Get all cached users data
                for (Object key : cachedKeys) {
                    Object cachedValue = redisTemplate.opsForValue().get(key);
                    if (cachedValue instanceof FetchUsersRes) {
                        userList.add((FetchUsersRes) cachedValue);
                    } else {
                        // handle deserialized object (e.g., JSON string)
                        FetchUsersRes mapped = MappingUtil.convertToFetchUsersRes(cachedValue);
                        userList.add(mapped);
                    }
                }

                // If cache returned results, skip DB
                if (!userList.isEmpty()) {
                    response = new ListUsersRes();
                    response.setListUsers(userList);
                    log.info("Returning user data from Redis cache ({} items).", userList.size());
                    return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
                }
            }

            // Fetch all data
            log.info("Cache empty or missing. Fetching users from database...");
            List<UsersDao> listUsers = usersRepo.findAll();

            // Mapping response
            response = MappingUtil.mapListUsersRes(listUsers);

            // Set cache
            for (UsersDao user : listUsers) {
                String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, user.getEmail());
                FetchUsersRes res = MappingUtil.mapFetchUsersRes(user);
                redisTemplate.opsForValue().set(cacheKey, res, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));

                redisTemplate.opsForSet().add(USER_CACHE_KEY, cacheKey);
                redisTemplate.expire(USER_CACHE_KEY, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));
            }
            log.info("Units successfully cached.");
        } catch (Exception e) {
            log.error("Error when fetching all users, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    public ApiBaseResponse<FetchUsersRes> fetchUserByEmail(String email, Metadata metadata) {
        log.info("Fetching user by email: {}", email);
        FetchUsersRes response;
        String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, email);
        try {
            // Try to fetch from cache first
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);

            log.info("Cache hit for user email {}. Loading from Redis...", email);

            // Deserialize cached object if necessary
            response = (cachedValue instanceof FetchUsersRes)
                    ? (FetchUsersRes) cachedValue
                    : MappingUtil.convertToFetchUsersRes(cachedValue);

            if (response != null) {
                log.info("Returning user data from Redis cache (EMAIL: {}).", email);
                return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
            }

            // Mapping to request
            log.info("Cache empty or missing. Fetching users from database...");
            FetchUsersReq request = FetchUsersReq.builder()
                    .email(email)
                    .build();

            // Checking mandatory field(s)
            ValidationHelper.validateFetchUsersRequest(request);

            // Fetch data from email
            UsersDao data = usersRepo.fetchByEmail(request.getEmail());
            if (data == null) {
                log.warn("User not found in database for EMAIL: {}", email);
                return ResponseUtil.buildResponse(STATUS_CODE_NOT_FOUND, ERROR_DATA_NOT_FOUND, null);
            }

            // Mapping response
            response = MappingUtil.mapFetchUsersRes(data);

            // Send Kafka event
            eventProducer.sendUserFetched(data, metadata.getRequestId());
        } catch (Exception e) {
            log.error("Error when fetching user by email, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<FetchUsersRes> deleteFlagUser(UpdateUsersReq request, Metadata metadata) {
        log.info("Delete flag user by email: {}", request.getEmail());
        FetchUsersRes response = null;
        try {
            // Checking mandatory field(s)
            ValidationHelper.validateUpdateUsersRequest(request);

            // Update to database
            int isSuccess = usersRepo.deleteFlagByEmail(request.getEmail());
            log.info("Update user value: {}", isSuccess);

            // Mapping response
            if (isSuccess > 0) {
                UsersDao data = usersRepo.fetchByEmail(request.getEmail());
                response = MappingUtil.mapFetchUsersRes(data);

                // Send Kafka event
                eventProducer.sendUserUpdated(data, metadata.getRequestId());

                // Update current cache
                String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, request.getEmail());
                redisTemplate.opsForValue().set(cacheKey, response, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));

                log.info("User email: {} successfully cached.", data.getEmail());
                redisTemplate.opsForSet().add(USER_CACHE_KEY, cacheKey);
                redisTemplate.expire(USER_CACHE_KEY, Duration.ofSeconds(USER_CACHE_DETAIL_TTL));
            }
        } catch (Exception e) {
            log.error("Error when deleting flag user, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_CODE_SUCCESS, response);
    }

    @Transactional
    public ApiBaseResponse<DeleteUsersRes> deleteUserByEmail(String email, Metadata metadata) {
        log.info("Start deleting user by email: {}", email);
        DeleteUsersRes response;
        String cacheKey = Strings.concat(USER_CACHE_SUB_KEY, email);
        try {
            // Initial response mapping
            UsersDao data = usersRepo.fetchByEmail(email);
            response = MappingUtil.mapDeleteUsersRes(data);

            // Delete from database
            usersRepo.deleteByEmail(email);

            // Send Kafka event
            eventProducer.sendUserDeleted(data, metadata.getRequestId());

            // Delete cache
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("Error when deleting user, caused by: " + e.getMessage());
            throw e;
        }
        return ResponseUtil.buildResponse(STATUS_CODE_SUCCESS, STATUS_MESSAGE_SUCCESS, response);
    }
}
