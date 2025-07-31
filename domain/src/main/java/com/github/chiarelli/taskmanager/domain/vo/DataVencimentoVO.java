package com.github.chiarelli.taskmanager.domain.vo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import com.github.chiarelli.taskmanager.domain.validation.DateRange;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class DataVencimentoVO {

  @DateRange(min = "now-1h", message = "A data de vencimento deve ser maior que a data atual")
  @NotNull(message = "A data de vencimento deve ser informada")
  private final Date dataVencimento;

  public static DataVencimentoVO now() {
    return new DataVencimentoVO(new Date());
  }

  public static DataVencimentoVO of(OffsetDateTime dataVencimento) {
    Date date = Date.from(dataVencimento.toInstant());
    return new DataVencimentoVO(date);
  }

  public static OffsetDateTime to(DataVencimentoVO dataVencimento) {
    return dataVencimento.getDataVencimento()
      .toInstant()
      .atZone(ZoneOffset.UTC)
      .toOffsetDateTime();
  }

}
