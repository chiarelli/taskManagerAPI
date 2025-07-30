package com.github.chiarelli.taskmanager.presentation.dtos;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"page", "size", "length", "total_query_count", "total_pages", "content"})
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PageCollectionJsonResponse<T> {

  private long length;

  private long page;

  private long size;

  @JsonProperty("total_query_count")
  private long queryCount;
  
  @JsonProperty("total_pages")
  private long totalPages;
  
  @JsonProperty("content")
  private List<T> data;

  // Construtor com Page<T>
  public PageCollectionJsonResponse(Page<T> page) {
    this.length = page.getTotalElements();
    this.page = page.getNumber() + 1;
    this.size = page.getSize();
    this.queryCount = page.getTotalElements();
    this.totalPages = page.getTotalPages();
    setData(page.getContent());
  }

  public List<T> getData() {
    return List.copyOf(data == null ? List.of() : data);
  }

  public void setData(List<T> data) {
    if(Objects.nonNull(data)) {
      this.data = data;
    }
  }

}