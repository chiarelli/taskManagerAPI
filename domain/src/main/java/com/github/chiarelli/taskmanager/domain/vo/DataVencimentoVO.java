package com.github.chiarelli.taskmanager.domain.vo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.github.chiarelli.taskmanager.domain.validation.DateRange;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DataVencimentoVO {

  @DateRange(min = "now", message = "A data de vencimento deve ser maior que a data atual")
  @NotNull(message = "A data de vencimento deve ser informada")
  private final LocalDateTime dataVencimento;

  public static DataVencimentoVO of(LocalDateTime dataVencimento) {
    return new DataVencimentoVO(dataVencimento);
  }

  public static DataVencimentoVO of(Date dataVencimento) {
    var localDateTime = dataVencimento.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
    return new DataVencimentoVO(localDateTime);
  }

  public static Date to(LocalDateTime dataVencimento) {
    return Date.from(dataVencimento.atZone(ZoneId.systemDefault()).toInstant());
  }

  public static LocalDateTime to(Date dataVencimento) {
    return dataVencimento.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDateTime();
  }

}
