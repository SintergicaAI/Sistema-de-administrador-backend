package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.MichelleConfig;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventService {
  private final MichelleConfig michelleConfig;
  private final OkHttpClient client = new OkHttpClient();

  public void subscribeToEvent(String event, String callbackEndpoint) {
    new Thread(
            () -> {
              String url = michelleConfig.getUrl() + event;
              RequestBody requestBody =
                  RequestBody.create(MediaType.parse("text/plain"), callbackEndpoint);
              Request request =
                  new Request.Builder()
                      .url(url)
                      .addHeader("Authorization", "Basic " + michelleConfig.getToken())
                      .post(requestBody)
                      .build();
              try {
                client.newCall(request).execute().close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .start();
  }

  public void triggerEvent(String event) {
    new Thread(
            () -> {
              String url = michelleConfig.getUrl() + event;
              RequestBody requestBody = RequestBody.create(null, new byte[0]);
              Request request =
                  new Request.Builder()
                      .url(url)
                      .addHeader("Authorization", "Basic " + michelleConfig.getToken())
                      .put(requestBody)
                      .build();
              try {
                client.newCall(request).execute().close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .start();
  }
}
