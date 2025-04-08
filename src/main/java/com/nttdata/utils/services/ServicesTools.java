package com.nttdata.utils.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class ServicesTools {
	private static final long TIME_OUT = 120;
	protected static Logger log = LogManager.getLogger(ServicesTools.class);

	public static OkHttpClient generateClient() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}
			} };

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

			OkHttpClient client = new OkHttpClient().newBuilder()
					.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
					.hostnameVerifier((hostname, session) -> true).connectTimeout(Duration.ofSeconds(TIME_OUT))
					.callTimeout(Duration.ofSeconds(TIME_OUT)).readTimeout(Duration.ofSeconds(TIME_OUT))
					.writeTimeout(Duration.ofSeconds(TIME_OUT)).build();
			return client;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static String getFieldValueFromJSONObject(String field, String obj, String defaultValue) {
		String result = defaultValue;
		if (Objects.isNull(obj))
			return result;
		try {
			JSONObject jsonObject = new JSONObject(obj);
			result = jsonObject.getString(field);
		} catch (Exception e) {
		}

		return result;
	}

	public static List<String> getFieldValueFromJSONArray(String field, String array) {
		List<String> result = new ArrayList<String>();
		if (Objects.isNull(array))
			return result;
		try {
			JSONObject tmp;
			JSONArray jsonArray = new JSONArray(array);
			for (Object object : jsonArray) {
				tmp = (JSONObject) object;
				result.add(tmp.getString(field));
			}
		} catch (Exception e) {
		}

		return result;
	}

	public static List<String> getStringJSONArray(String field, JSONObject testFieldsJSObj) {
		List<String> result = new ArrayList<String>();
		if (Objects.isNull(testFieldsJSObj.optJSONArray(field)))
			return result;
		try {
			JSONArray jsonArray = testFieldsJSObj.getJSONArray(field);
			for (Object object : jsonArray) {
				result.add((String) object);
			}
		} catch (Exception e) {
		}

		return result;
	}

}
