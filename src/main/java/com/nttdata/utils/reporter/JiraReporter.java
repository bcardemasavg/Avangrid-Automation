package com.nttdata.utils.reporter;

import java.io.File;
import java.net.URI;
import java.text.Normalizer;
import java.time.Duration;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nttdata.utils.Auth;
import com.nttdata.utils.Constants;
import com.nttdata.utils.Utilities;

import io.cucumber.java.Status;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JiraReporter {
	protected static Logger log = LogManager.getLogger(JiraReporter.class);
	static final String JIRA_URL = "http://127.0.0.1:8089";
	static String username;
	static String password;
	static String jiraBarerToken;
	static String executedBy;

	private static String getAuthenticationMethod() {
		if (!jiraBarerToken.isEmpty()) {
			return "Bearer " + jiraBarerToken;
		} else {
			return Auth.getBasicAuth(username, password);
		}
	}

	public static String reportToXRayResult(File cucumberFileJsonReport, File infoFile) throws Exception {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");
		executedBy = System.getenv("JIRA_EXECUTOR");
		executedBy = executedBy != null ? executedBy : username;

		try {
			URI uri = new URI(JIRA_URL + "/rest/raven/1.0/import/execution/cucumber/multipart");

			OkHttpClient client = generateClient();
			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("result", cucumberFileJsonReport.getAbsolutePath(),
							RequestBody.create(cucumberFileJsonReport, MediaType.parse("application/octet-stream")))
					.addFormDataPart("info", infoFile.getAbsolutePath(),
							RequestBody.create(infoFile, MediaType.parse("application/octet-stream")))
					.build();
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Call newCall = client.newCall(request);
			newCall.timeout().deadlineNanoTime(60000);
			Response response = newCall.execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en reportToXRayResult: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	private static OkHttpClient generateClient() {
		OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(Duration.ofSeconds(60))
				.callTimeout(Duration.ofSeconds(60)).readTimeout(Duration.ofSeconds(60))
				.writeTimeout(Duration.ofSeconds(60)).build();
		return client;
	}

	public static String attachmentsToIssue(File fileToAttach, String issueIdOrKey) throws Exception {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/issue/" + issueIdOrKey + "/attachments");

			OkHttpClient client = generateClient();
			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("file", Utilities.getFechaReporte("yyyyMMddHHmmss") + "_" + fileToAttach.getName(),
							RequestBody.create(fileToAttach, MediaType.parse("application/octet-stream")))
					.build();
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod())
					.addHeader("X-Atlassian-Token", "no-check").addHeader("Accept", "application/json").build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();

		} catch (Exception e) {
			log.error("Error en attachmentsToIssue: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static JSONObject getIssueType(String issueTypeName) throws Exception {
		JSONArray jsonArray = new JSONArray(getIssueType());
		JSONObject jsonObject;
		for (Object object : jsonArray) {
			jsonObject = (JSONObject) object;
			if (jsonObject.getString("name").equals(issueTypeName)) {
				return jsonObject;
			}
		}
		return null;
	}

	public static String getIssueType() throws Exception {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/issuetype");

			OkHttpClient client = generateClient();
			Request request = new Request.Builder().url(uri.toString()).method("GET", null)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en getIssueType: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static String getIssuDetailType(String issueIDOrKey) throws Exception {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/issue/" + issueIDOrKey);

			OkHttpClient client = generateClient();
			Request request = new Request.Builder().url(uri.toString()).method("GET", null)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en getIssuDetailType: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static String asociarTestConTestPlan(String testExecutionKey, String idTesCase, String testPlanKey) {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");
		executedBy = System.getenv("JIRA_EXECUTOR");
		executedBy = executedBy != null ? executedBy : username;

		try {
			URI uri = new URI(JIRA_URL + "/rest/raven/1.0/testexec/" + testExecutionKey
					+ "/addAllTestsToTestPlan?testPlan=" + testPlanKey);
			String bodyString = "[" + idTesCase + "]";
			OkHttpClient client = generateClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(bodyString, mediaType);
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en asociarTestConTestPlan: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static String getUser(String usernameToFind) throws Exception {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/user?username=" + usernameToFind);

			OkHttpClient client = generateClient();
			Request request = new Request.Builder().url(uri.toString()).method("GET", null)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en getUser: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static String postTestCaseExecution(TestCase testCase, String testPlanKey, String testExecutionKey) {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");
		executedBy = System.getenv("JIRA_EXECUTOR");
		executedBy = executedBy != null ? executedBy : username;

		try {
			URI uri = new URI(JIRA_URL + "/rest/raven/2.0/api/import/execution");
			String bodyString = buildBodyForTestExecution(testCase, testPlanKey, testExecutionKey);
			OkHttpClient client = generateClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(bodyString, mediaType);
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
			if (new JSONObject(responseAsString).optJSONObject("testExecIssue", null) != null) {
				String testExecIssue = new JSONObject(responseAsString).getJSONObject("testExecIssue").getString("key");
				asignarUsuario(testExecIssue, executedBy);
			}
		} catch (Exception e) {
			log.error("Error en asociarTestConTestPlan: " + e.getMessage(), e);
		}
		return responseAsString;

	}

	private static void asignarUsuario(String issueKey, String user) {
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/raven/2.0/api/import/execution");
			String bodyString = "{\"fields\":{\"assignee\":{\"name\":\"" + user + "\"}}}";
			OkHttpClient client = generateClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(bodyString, mediaType);
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			client.newCall(request).execute();
		} catch (Exception e) {
			log.error("Error en getUser: " + e.getMessage(), e);
		}
	}

	private static String buildBodyForTestExecution(TestCase testCase, String testPlanKey, String testExecutionKey)
			throws Exception {
		JSONObject JTestExecution = new JSONObject();
		JTestExecution.put("testExecutionKey", testExecutionKey);
		JTestExecution.put("tests", generateTest(testCase));
		JTestExecution.put("info", generateInfo(testCase, testPlanKey));

		return JTestExecution.toString();
	}

	private static JSONObject generateInfo(TestCase testCase, String testPlanKey) throws Exception {
		JSONObject result = new JSONObject();
		result.put("startDate", Utilities.formatDate(testCase.getStart(), Utilities.DATE_FORMAT_JIRA));
		result.put("finishDate", Utilities.formatDate(testCase.getFinish(), Utilities.DATE_FORMAT_JIRA));
		result.put("testPlanKey", testPlanKey);

		return result;
	}

	private static JSONArray generateTest(TestCase testCase) {
		JSONArray result = new JSONArray();
		JSONObject test = new JSONObject();
		test.put("testKey", testCase.getTestKey());
		test.put("start", Utilities.formatDate(testCase.getStart(), Utilities.DATE_FORMAT_JIRA));
		test.put("finish", Utilities.formatDate(testCase.getFinish(), Utilities.DATE_FORMAT_JIRA));
		test.put("evidences", generarEvidencias(testCase));
		test.put("comment", generateComment(testCase));
		test.put("status", parseStatusToJira(testCase.getStatus()));

		result.put(test);
		return result;
	}

	private static String generateComment(TestCase testCase) {
		String result = "Prueba ejecutada correctamente.";
		if (testCase.getError() != null) {
			result = "Ejecucion de prueba con error:\n" + testCase.getError();
		}
		return result;
	}

	private static JSONArray generarEvidencias(TestCase testCase) {
		JSONArray result = new JSONArray();
		JSONObject jsObject;
		for (Attachment attachment : testCase.getAttachments()) {
			jsObject = new JSONObject();
			jsObject.put("data", attachment.getData());
			jsObject.put("filename", attachment.getName());
			jsObject.put("contentType", attachment.getMediaType());
			result.put(jsObject);
		}

		for (Step step : testCase.getSteps()) {
			for (Attachment attachment : step.getAttachments()) {
				jsObject = new JSONObject();
				jsObject.put("data", attachment.getData());
				jsObject.put("filename", "STEP_" + step.getNumber() + "_" + step.getStatus() + "_"
						+ removeSpecialCharacters(step.getText()));
				jsObject.put("contentType", attachment.getMediaType());
				result.put(jsObject);
			}
		}
		return result;
	}

	private static String parseStatusToJira(String status) {
		if (Status.PASSED.toString().equals(status)) {
			return "PASS";
		} else if (Status.FAILED.toString().equals(status)) {
			return "FAIL";
		}
		return "TODO";
	}

	public static String removeSpecialCharacters(String input) {
		String normalizedString = input.replace(" ", "_");
		normalizedString = Normalizer.normalize(normalizedString, Normalizer.Form.NFD);
		// Utiliza una expresión regular para quitar caracteres no alfanuméricos
		return normalizedString.replaceAll("[^a-zA-Z0-9]", "");
	}

	public static String createTestExecution(HashMap<String, String> capabilitesInfo, String featureName) {
		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");
		executedBy = System.getenv("JIRA_EXECUTOR");
		executedBy = executedBy != null ? executedBy : username;

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/issue");
			String bodyString = StageUploadResultJira.generateFields(capabilitesInfo, featureName).toString();
			OkHttpClient client = generateClient();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody body = RequestBody.create(bodyString, mediaType);
			Request request = new Request.Builder().url(uri.toString()).method("POST", body)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
		} catch (Exception e) {
			log.error("Error en asociarTestConTestPlan: " + e.getMessage(), e);
		}
		return responseAsString;
	}

	public static String getEnviromentId(String ambiente) {
		String ambienteID = "11872"; // Homologacion por defecto

		String responseAsString = Constants.EMPTY;
		username = System.getenv("JIRA_USERNAME");
		password = System.getenv("JIRA_PASSWORD");
		jiraBarerToken = System.getenv().getOrDefault("JIRA_TOKEN", "");

		try {
			URI uri = new URI(JIRA_URL + "/rest/api/2/customFields/11107/options");

			OkHttpClient client = generateClient();
			Request request = new Request.Builder().url(uri.toString()).method("GET", null)
					.addHeader(Constants.AUTHORIZATION, getAuthenticationMethod()).build();
			Response response = client.newCall(request).execute();
			responseAsString = response.body().string();
			JSONArray optionsAmbiente = new JSONObject(responseAsString).getJSONArray("options");
			JSONObject optionJSON;
			String value;
			String ambienteFormated;
			for (Object option : optionsAmbiente) {
				optionJSON = (JSONObject) option;
				value = optionJSON.optString("value");
				value = StringUtils.stripAccents(value);
				value = value.replace(" ", "").trim();

				ambienteFormated = StringUtils.stripAccents(ambiente);
				ambienteFormated = ambienteFormated.replace(" ", "").trim();

				if (ambiente.equalsIgnoreCase(value)) {
					ambienteID = optionJSON.optString("id");
					break;
				}
			}
		} catch (Exception e) {
			log.error("Error en getUser: " + e.getMessage(), e);
		}

		return ambienteID;
	}

}
