package com.nttdata.utils.driver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nttdata.utils.Auth;
import com.nttdata.utils.Constants;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BrowserstackReporter {
	protected static Logger log = LogManager.getLogger(BrowserstackReporter.class);

	private BrowserstackReporter() {
	}

	public static String markAs(String status, String sessionId, String type) throws URISyntaxException, IOException {
		String responseAsString = Constants.EMPTY;
		String username = System.getenv("BROWSERSTACK_USERNAME");
		String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
		String estado = "Failed";
		if (status.equals("PASSED")) {
			estado = "Passed";
		}

		URI uri = null;
		uri = new URI("https://@api.browserstack.com/app-automate/sessions/" + sessionId + ".json");
		if (type.equals("WEB")) {
			uri = new URI("https://@api.browserstack.com/automate/sessions/" + sessionId + ".json");
		}
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("status", estado));
		if (status.equals("FAILED")) {
			nameValuePairs.add(new BasicNameValuePair("reason", estado));
		}

		try {
			String bodyString = "{\"status\":\"" + estado + "\", \"reason\":\"" + estado + "\"}";
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(bodyString, mediaType);
			Request request = new Request.Builder().url(uri.toString()).method("PUT", body)
					.addHeader(Constants.AUTHORIZATION, Auth.getBasicAuth(username, accessKey)).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en markAs: " + e.getMessage(), e);
		}
		return responseAsString;
	}

}
