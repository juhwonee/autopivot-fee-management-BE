package com.example.capstonedesign20252.auth.client;

import com.example.capstonedesign20252.auth.config.KakaoProperties;
import com.example.capstonedesign20252.auth.dto.KakaoTokenResponseDto;
import com.example.capstonedesign20252.auth.dto.KakaoUserInfoResponseDto;
import com.example.capstonedesign20252.auth.exception.KakaoErrorCode;
import com.example.capstonedesign20252.auth.exception.KakaoException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthClient {

  private final KakaoProperties kakaoProperties;
  private final RestClient restClient;

  /**
   * 인가 코드(code)를 사용하여 카카오에서 Access Token 요청
   *
   * @param code 카카오 서버로부터 받은 인가 코드
   * @return KaKaoTokenResponse 액세스 토큰 정보
   */
  public KakaoTokenResponseDto requestAccessToken(String code) {
    MultiValueMap<String, String> params = buildTokenRequestParams(code);

    try {
      KakaoTokenResponseDto tokenResponse = restClient.post()
                                                   .uri(kakaoProperties.getTokenUri())
                                                   .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                                   .body(params)
                                                   .retrieve()
                                                   .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                                     String responseBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                                                     log.warn("Kakao Token Request Failed - Status: {}", response.getStatusCode());
                                                     handleTokenErrorResponse(responseBody);
                                                   })
                                                   .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                                     log.error("HttpServerErrorException: status={}", response.getStatusCode());
                                                     throw new KakaoException(KakaoErrorCode.TOKEN_REQUEST_FAILED_SERVER);
                                                   })
                                                   .body(KakaoTokenResponseDto.class);

      log.info("Kakao Token API Response received successfully");

      // body 자체가 null
      if (tokenResponse == null) {
        log.error("Kakao Token API response body is null");
        throw new KakaoException(KakaoErrorCode.TOKEN_REQUEST_FAILED_NULL_BODY);
      }

      // access_token이 없는 경우
      if (tokenResponse.accessToken() == null) {
        log.error("Kakao Token API response does not contain access_token");
        throw new KakaoException(KakaoErrorCode.TOKEN_REQUEST_FAILED_NO_TOKEN);
      }

      return tokenResponse;

    } catch (ResourceAccessException ex) {
      log.error("Kakao Token API Connection Failed: {}", ex.getMessage());
      throw new KakaoException(KakaoErrorCode.CONNECTION_FAILED);
    } catch (KakaoException ex) {
      // 이미 처리된 KakaoException은 그대로 던지기
      throw ex;
    } catch (Exception ex) {
      log.error("Unknown error during Kakao token request", ex);
      throw new KakaoException(KakaoErrorCode.UNKNOWN_ERROR);
    }
  }

  /**
   * Access Token을 사용하여 카카오에서 kakaoId 조회
   *
   * @param accessToken 카카오에서 발급받은 Access Token
   * @return kakaoId
   */
  public String requestKakaoId(String accessToken) {
    try {
      KakaoUserInfoResponseDto userInfo = restClient.get()
                                                    .uri(kakaoProperties.getUserInfoUri())
                                                    .header("Authorization", "Bearer " + accessToken)
                                                    .retrieve()
                                                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                                   log.error("Kakao User Info Request Failed - Status: {}", response.getStatusCode());
                                                   throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
                                                 })
                                                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                                   log.error("Kakao Server Error - Status: {}", response.getStatusCode());
                                                   throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
                                                 })
                                                    .body(KakaoUserInfoResponseDto.class);

      if (userInfo == null || userInfo.kakaoId() == null) {
        log.error("Kakao User Info response is invalid");
        throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
      }

      return String.valueOf(userInfo.kakaoId());

    } catch (ResourceAccessException ex) {
      log.error("Kakao User Info API Connection Failed: {}", ex.getMessage());
      throw new KakaoException(KakaoErrorCode.CONNECTION_FAILED);
    } catch (KakaoException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("Unknown error during Kakao user info request", ex);
      throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
    }
  }

  /**
   * 카카오 유저 정보 한번에 가져옴
   *
   * @param accessToken 카카오에서 발급받은 Access Token
   * @return KakaoUserInfoResponse 카카오 사용자 정보
   */
  public KakaoUserInfoResponseDto requestKakaoUserInfo(String accessToken) {
    try {
      KakaoUserInfoResponseDto userInfo = restClient.get()
                                                 .uri(kakaoProperties.getUserInfoUri())
                                                 .header("Authorization", "Bearer " + accessToken)
                                                 .retrieve()
                                                 .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                                   log.error("Kakao User Info Request Failed - Status: {}", response.getStatusCode());
                                                   throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
                                                 })
                                                 .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                                   log.error("Kakao Server Error - Status: {}", response.getStatusCode());
                                                   throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
                                                 })
                                                 .body(KakaoUserInfoResponseDto.class);

      if (userInfo == null || userInfo.kakaoId() == null) {
        log.error("Kakao User Info response is invalid");
        throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
      }

      return userInfo;

    } catch (ResourceAccessException ex) {
      log.error("Kakao User Info API Connection Failed: {}", ex.getMessage());
      throw new KakaoException(KakaoErrorCode.CONNECTION_FAILED);
    } catch (KakaoException ex) {
      throw ex;
    } catch (Exception ex) {
      log.error("Unknown error during Kakao user info request", ex);
      throw new KakaoException(KakaoErrorCode.USER_INFO_REQUEST_FAILED);
    }
  }

  // Access Token 요청을 위한 파라미터 생성
  private MultiValueMap<String, String> buildTokenRequestParams(String code) {
    code = code.trim();
    try {
      code = URLDecoder.decode(code, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Failed to decode code", e);
    }

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "authorization_code");
    params.add("client_id", kakaoProperties.getClientId());
    params.add("redirect_uri", kakaoProperties.getRedirectUri());
    params.add("code", code);

    return params;
  }

  // 토큰 요청 에러 응답 처리
  private void handleTokenErrorResponse(String responseBody) {
    String bodyLower = responseBody.toLowerCase();

    if (bodyLower.contains("invalid_grant")) {
      if (bodyLower.contains("code expired")) {
        throw new KakaoException(KakaoErrorCode.AUTH_CODE_EXPIRED);
      } else if (bodyLower.contains("already used")) {
        throw new KakaoException(KakaoErrorCode.AUTH_CODE_ALREADY_USED);
      } else if (bodyLower.contains("redirect uri mismatch")) {
        throw new KakaoException(KakaoErrorCode.REDIRECT_URI_MISMATCH);
      } else {
        throw new KakaoException(KakaoErrorCode.AUTH_CODE_INVALID);
      }
    } else if (bodyLower.contains("invalid_client")) {
      throw new KakaoException(KakaoErrorCode.INVALID_CLIENT_ID);
    }

    throw new KakaoException(KakaoErrorCode.TOKEN_REQUEST_FAILED_CLIENT);
  }
}