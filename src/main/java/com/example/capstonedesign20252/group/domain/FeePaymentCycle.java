package com.example.capstonedesign20252.group.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FeePaymentCycle {
  WEEKLY("주간", "매주", 1, ChronoUnit.WEEKS),
  MONTHLY("월간", "매월", 1, ChronoUnit.MONTHS),
  QUARTERLY("분기", "3개월마다", 3, ChronoUnit.MONTHS),
  HALF_YEARLY("반기", "6개월마다", 6, ChronoUnit.MONTHS),
  YEARLY("연간", "매년", 1, ChronoUnit.YEARS);

  private final String Korean;
  private final String description;
  private final long amount;
  private final ChronoUnit unit;

  public LocalDate getNextPaymentDate(LocalDate baseDate) {
    return baseDate.plus(amount, unit);
  }

  public boolean isOverdue(LocalDate lastPaymentDate) {
    return LocalDate.now().isAfter(getNextPaymentDate(lastPaymentDate));
  }
}
