package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.entidades.InvalidatedTokens;
import com.sintergica.apiv2.repositorio.InvalidatedTokensRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    List<String> invalidToken = invalidatedTokensRepository.findAll().stream()
        .map(InvalidatedTokens::getRefreshToken)
        .toList();
    bannedTokens.addAll(invalidToken);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void subscribeToLogout() {
	  try {
		  eventService.subscribeToEvent(LOGOUT_EVENT, UPDATE_TOKENS_ENDPOINT);
	  } catch (IOException e) {
		  throw new RuntimeException(e);
	  }
  }
}
