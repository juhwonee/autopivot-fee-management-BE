package com.example.capstonedesign20252.excel.service;

import com.example.capstonedesign20252.excel.dto.MemberDataDto;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ExcelParserService {

  public List<MemberDataDto> parseExcelFile(MultipartFile file) throws IOException {
    List<MemberDataDto> members = new ArrayList<>();

    try (InputStream is = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(is)) {

      Sheet sheet = workbook.getSheetAt(0);
      log.info("엑셀 파일 파싱 시작: {}, 총 {}행", file.getOriginalFilename(), sheet.getLastRowNum());

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null) {
          log.warn("{}번째 행이 비어있습니다. 스킵합니다.", i + 1);
          continue;
        }

        try {
          String name = getCellValueAsString(row.getCell(0));   // A열: 이름
          String phone = getCellValueAsString(row.getCell(1));  // B열: 전화번호
          String email = getCellValueAsString(row.getCell(2));  // C열: 이메일

          if (name == null || name.trim().isEmpty()) {
            log.warn("{}번째 행: 이름이 비어있어 스킵합니다.", i + 1);
            continue;
          }

          members.add(new MemberDataDto(
              name.trim(),
              phone != null ? phone.trim() : null,
              email != null ? email.trim() : null
          ));

          log.debug("{}번째 행 파싱 완료: {}", i + 1, name);

        } catch (Exception e) {
          log.error("{}번 째 행 파싱 오류: {}", i + 1, e.getMessage());
        }
      }

      log.info("엑셀 파일 파싱 완료: 총 {}명의 멤버 정보 추출", members.size());
    }
    return members;
  }

  private String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return null;
    }

    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> {
        // 전화번호가 숫자로 입력된 경우 (예: 01012345678)
        if (DateUtil.isCellDateFormatted(cell)) {
          yield cell.getDateCellValue().toString();
        }
        // 소수점 제거하고 정수로 변환
        yield String.valueOf((long) cell.getNumericCellValue());
        // 소수점 제거하고 정수로 변환
      }
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      default -> null;
    };
  }
}
