package com.example.capstonedesign20252.sms.controller;

import com.example.capstonedesign20252.groupMember.domain.GroupMember;
import com.example.capstonedesign20252.groupMember.repository.GroupMemberRepository;
import com.example.capstonedesign20252.sms.dto.SendBulkSmsRequestDto;
import com.example.capstonedesign20252.sms.dto.SendSmsRequestDto;
import com.example.capstonedesign20252.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class SmsController {

  private final GroupMemberRepository groupMemberRepository;
  private final SmsService smsService;  // ✅ 주입

  // 개별 SMS 발송
  @PostMapping("/send")
  public ResponseEntity<Map<String, Object>> sendSms(@RequestBody SendSmsRequestDto request) {
    log.info("SMS 발송 요청 - memberId: {}, phone: {}, message: {}",
        request.memberId(), request.phone(), request.message());

    if (request.phone() == null || request.phone().isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of(
          "success", false,
          "message", "전화번호가 없습니다.",
          "recipient", ""
      ));
    }

    try {
      // ✅ 실제 CoolSMS 발송
      smsService.send(request.phone(), request.message());

      log.info("SMS 발송 성공 - phone: {}", request.phone());

      return ResponseEntity.ok(Map.of(
          "success", true,
          "message", "메시지가 발송되었습니다.",
          "recipient", request.phone()
      ));
    } catch (Exception e) {
      log.error("SMS 발송 실패 - phone: {}, error: {}", request.phone(), e.getMessage());
      return ResponseEntity.internalServerError().body(Map.of(
          "success", false,
          "message", "SMS 발송 중 오류가 발생했습니다: " + e.getMessage(),
          "recipient", request.phone()
      ));
    }
  }

  // 일괄 SMS 발송
  @PostMapping("/send-bulk")
  public ResponseEntity<Map<String, Object>> sendBulkSms(@RequestBody SendBulkSmsRequestDto request) {
    log.info("일괄 SMS 발송 요청 - {}명, message: {}", request.memberIds().size(), request.message());

    List<GroupMember> members = groupMemberRepository.findAllById(request.memberIds());

    int sentCount = 0;
    int failedCount = 0;

    for (GroupMember member : members) {
      try {
        if (member.getPhone() == null || member.getPhone().isEmpty()) {
          log.warn("전화번호 없음 - memberId: {}, name: {}", member.getId(), member.getName());
          failedCount++;
          continue;
        }

        // ✅ 실제 CoolSMS 발송
        smsService.send(member.getPhone(), request.message());

        log.info("SMS 발송 성공 - name: {}, phone: {}", member.getName(), member.getPhone());
        sentCount++;
      } catch (Exception e) {
        log.error("SMS 발송 실패 - name: {}, error: {}", member.getName(), e.getMessage());
        failedCount++;
      }
    }

    log.info("일괄 SMS 발송 완료 - 성공: {}명, 실패: {}명", sentCount, failedCount);

    return ResponseEntity.ok(Map.of(
        "success", true,
        "message", sentCount + "명에게 메시지가 발송되었습니다.",
        "sentCount", sentCount,
        "failedCount", failedCount
    ));
  }
}
