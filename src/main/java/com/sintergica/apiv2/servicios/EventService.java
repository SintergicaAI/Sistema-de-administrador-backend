package com.sintergica.apiv2.servicios;

import com.sintergica.apiv2.configuration.MichelleConfig;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class EventService {
	private final MichelleConfig michelleConfig;
	private final OkHttpClient client = new OkHttpClient();

	public void subscribeToEvent(String event, String callbackEndpoint) throws IOException {
		String url = michelleConfig.getUrl() + event;
		Request request = new Request.Builder()
			.url(url)
			.addHeader("Authorization", "Basic " + michelleConfig.getToken())
			.post(RequestBody.create(
				MediaType.parse("text/plain"), callbackEndpoint
			))
			.build();
		client.newCall(request).execute().close();
	}
}
