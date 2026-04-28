package com.pogbe.bankingsystem.services.interfaces;

import com.pogbe.bankingsystem.dto.requests.UserCreateRequest;
import com.pogbe.bankingsystem.dto.requests.UserLoginRequest;
import com.pogbe.bankingsystem.dto.responses.GenericSuccessResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserCreatedResponse;
import com.pogbe.bankingsystem.dto.responses.SuccessUserLoginResponse;
import com.pogbe.bankingsystem.dto.responses.VerificationDocumentDTO;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    SuccessUserCreatedResponse createUser(UserCreateRequest userCreateRequest);

    SuccessUserLoginResponse loginUser(UserLoginRequest userLoginRequest);

    GenericSuccessResponse updateProfilePicture(Authentication authentication, MultipartFile file);

    byte[] getProfilePicture(Authentication authentication);

    String getProfilePictureContentType(Authentication authentication);

    GenericSuccessResponse deleteProfilePicture(Authentication authentication);
}
