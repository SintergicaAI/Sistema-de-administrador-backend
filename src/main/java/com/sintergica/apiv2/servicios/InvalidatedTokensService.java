package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.*;
import com.sintergica.apiv2.repositorio.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;

@Data
@Service
@RequiredArgsConstructor
public class InvalidatedTokensService {

  private final InvalidatedTokensRepository invalidatedTokensRepository;

  public InvalidatedTokens addInvalidatedToken(InvalidatedTokens invalidatedTokens) {
    return invalidatedTokensRepository.save(invalidatedTokens);
  }

  public InvalidatedTokens getInvalidatedToken(String token) {
    return this.invalidatedTokensRepository.findByRefreshToken(token);
  }
}
