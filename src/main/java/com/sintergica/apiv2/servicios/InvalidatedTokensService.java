package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.InvalidatedTokens;
import com.sintergica.apiv2.repositorio.InvalidatedTokensRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class InvalidatedTokensService {
  private final InvalidatedTokensRepository invalidatedTokensRepository;
  private final EventService eventService;
  private final List<String> bannedTokens = new ArrayList<>();
  private final String LOGOUT_EVENT = "logout";
  private final String UPDATE_TOKENS_ENDPOINT = "/users/updateTokens";

  public InvalidatedTokens addInvalidatedToken(InvalidatedTokens invalidatedTokens) {
    bannedTokens.add(invalidatedTokens.getRefreshToken());
    eventService.triggerEvent(LOGOUT_EVENT);
    return invalidatedTokensRepository.save(invalidatedTokens);
  }

  public InvalidatedTokens getInvalidatedToken(String token) {
    return this.invalidatedTokensRepository.findByRefreshToken(token);
  }

  public boolean isTokenBanned(String token) {
    return this.bannedTokens.contains(token);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void loadBannedTokens() {
    bannedTokens.clear();
    List<String> invalidToken =
        invalidatedTokensRepository.findAll().stream()
            .map(InvalidatedTokens::getRefreshToken)
            .toList();
    bannedTokens.addAll(invalidToken);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void subscribeToLogout() {
    eventService.subscribeToEvent(LOGOUT_EVENT, UPDATE_TOKENS_ENDPOINT);
  }
}
