package com.sintergica.apiv2.dto;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class WrapperUserDTO<T> {

  private List<T> data;
  private int currentPage;
  private int totalPages;
  private long totalElements;

  public WrapperUserDTO(Page<T> data) {
    this.data = data.getContent();
    this.currentPage = data.getNumber();
    this.totalPages = data.getTotalPages();
    this.totalElements = data.getTotalElements();
  }
}
