package com.github.chiarelli.taskmanager.domain.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DataVencimentoVO {

  private final LocalDateTime dataVencimento;

}
