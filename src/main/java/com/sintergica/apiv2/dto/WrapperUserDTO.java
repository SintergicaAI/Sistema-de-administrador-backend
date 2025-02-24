package com.sintergica.apiv2.dto;

import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class WrapperUserDTO {

  private List<UserDTO> userDTOPage;
  private int currentPage;
  private int totalPages;
  private long totalElements;

  public WrapperUserDTO(Page<UserDTO> userDTOPage) {
    this.userDTOPage = userDTOPage.getContent();
    this.currentPage = userDTOPage.getNumber();
    this.totalPages = userDTOPage.getTotalPages();
    this.totalElements = userDTOPage.getTotalElements();
  }
}
