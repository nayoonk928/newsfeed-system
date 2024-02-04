package com.nayoon.user_service.user.service;

import com.nayoon.user_service.auth.security.CustomUserDetails;
import com.nayoon.user_service.common.exception.CustomException;
import com.nayoon.user_service.common.exception.ErrorCode;
import com.nayoon.user_service.common.redis.service.RedisService;
import com.nayoon.user_service.common.s3.service.S3Service;
import com.nayoon.user_service.common.utils.EncryptionUtils;
import com.nayoon.user_service.user.dto.request.PasswordUpdateRequest;
import com.nayoon.user_service.user.dto.request.ProfileUpdateRequest;
import com.nayoon.user_service.user.dto.request.SignUpRequest;
import com.nayoon.user_service.user.dto.response.UserResponse;
import com.nayoon.user_service.user.dto.response.UserSignUpResponse;
import com.nayoon.user_service.user.entity.User;
import com.nayoon.user_service.user.repository.UserRepository;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private static final String AUTH_PREFIX = "AuthCode";

  private final UserRepository userRepository;
  private final RedisService redisService;
  private final S3Service s3Service;

  private final String IMAGE_PATH = "image/";

  /**
   * 회원가입 메서드
   */
  @Transactional
  public UserSignUpResponse signup(SignUpRequest request, MultipartFile imageFile) throws IOException {
    if (imageFile.isEmpty()) {
      throw new CustomException(ErrorCode.PROFILE_IMAGE_REQUIRED);
    }

    // 비밀번호 암호화
    String encryptPassword = EncryptionUtils.encode(request.password());

    // 이메일 중복 확인
    checkDuplicatedEmail(request.email());

    // 이메일 인증 코드 확인
    if (!verifyCode(request.email(), request.code())) {
      throw new CustomException(ErrorCode.EMAIL_AUTH_CODE_INCORRECT);
    }

    User user = User.builder()
        .email(request.email())
        .name(request.name())
        .password(encryptPassword)
        .greeting(request.greeting())
        .userRole(request.userRole())
        .build();

    userRepository.save(user);

    // S3에 업로드
    String s3ImageUrl = s3Service.saveFile(imageFile, user, IMAGE_PATH);

    // s3에 올라간 이미지 URL 저장
    user.updateProfileImage(s3ImageUrl);

    return new UserSignUpResponse(user.getId(), user.getEmail());
  }

  // 이메일 중복 체크 메서드
  private void checkDuplicatedEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      log.debug("UserService.checkDuplicatedEmail exception occur email: {}", email);
      throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
    }
  }

  // 이메일 인증 코드 유효성 확인 메서드
  private boolean verifyCode(String email, String code) {
    String authCode = redisService.getValue(AUTH_PREFIX + email);

    if (authCode == null) {
      throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
    }

    if (authCode.equals(code)) {
      redisService.deleteKey(AUTH_PREFIX + email);
      return true;
    }

    return false;
  }

  /**
   * 사용자 정보 업데이트 (이름, 프로필 이미지, 인사말)
   */
  @Transactional
  public UserResponse updateProfile(CustomUserDetails userDetails, ProfileUpdateRequest request,
      MultipartFile imageFile) throws IOException {
    User user = userRepository.findById(userDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String imageUrl = getImageUrl(user, user.getProfileImage(), imageFile);

    user.update(request.name(), request.greeting(), imageUrl);

    return UserResponse.builder()
        .email(user.getEmail())
        .name(user.getName())
        .profileImage(user.getProfileImage())
        .greeting(user.getGreeting())
        .build();
  }

  // 프로필 이미지 url 반환 (입력된 파일이 있다면 새 url 반환, 아니라면 기존 url 반환)
  private String getImageUrl(User user, String prevImage, MultipartFile imageFile)
      throws IOException {
    if (imageFile != null && !imageFile.isEmpty()) {
      // S3에 있는 기존 이미지 삭제 (세번째 "/" 이후의 문자열 전달)
      s3Service.deleteFile(String.join("/", Arrays.copyOfRange(prevImage.split("/"),
          3, prevImage.split("/").length)));

      return s3Service.saveFile(imageFile, user, IMAGE_PATH);
    }
    return prevImage;
  }

  /**
   * 비밀번호 업데이트
   */
  @Transactional
  public void updatePassword(CustomUserDetails userDetails, PasswordUpdateRequest request) {
    User user = userRepository.findById(userDetails.getId())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    String encryptedPassword = EncryptionUtils.encode(request.newPassword());

    // 현재 비밀번호 올바르게 입력했는지 확인
    if (!EncryptionUtils.matchPassword(request.prevPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.NOT_MATCHED_CURR_PASSWORD);
    }

    // 현재 비밀번호와 새로운 비밀번호가 동일한지 확인
    if (EncryptionUtils.matchPassword(request.prevPassword(), encryptedPassword)) {
      throw new CustomException(ErrorCode.PASSWORD_NOT_CHANGED);
    }

    user.updatePassword(encryptedPassword);
  }

}
