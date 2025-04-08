package com.nttdata.utils.reporter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.google.gson.Gson;
import com.nttdata.utils.Constants;
import com.nttdata.utils.Utilities;
import com.nttdata.utils.runner.MainRun;

public class StageUploadResultJira {
	protected Logger log = LogManager.getLogger(this.getClass());
	private static final String FIELD_TEST_PLAN = "customfield_10930";
	private static final String FIELD_AMBIENTE = "customfield_11107";
	private static final String FIELD_PORTAL = "customfield_14706";
	private static final String FIELD_SISTEMA_OPERATIVO = "customfield_14707";
	private static final String FIELD_VERSION_SISTEMA_OPERATIVO = "customfield_14708";
	private static final String FIELD_NAVEGADOR = "customfield_14709";
	private static final HashMap<String, String> FIELD_NAVEGADOR_IDS = new HashMap<String, String>() {
		{
			put("ninguno", "-1");
			put("chrome", "18020");
			put("firefox", "18021");
			put("edge", "18022");
			put("safari", "18023");
			put("opera", "18024");
		}
	};
	private static final String FIELD_VERSION_NAVEGADOR = "customfield_14710";
	private static final String FIELD_APLICACION_MOBILE = "customfield_14711";
	private static final String FIELD_VERSION_APLICACION_MOBILE = "customfield_14712";
	private static final String FIELD_TIPO_EJECUCION = "customfield_15301";
	private static final String FIELD_TIPO_EJECUCION_AUTOMATIZADO = "21120";

	static String testPlanKey;
	private String projectKey;

	@Test
	public void subirEvidencias() throws Exception {
		testPlanKey = System.getenv().getOrDefault("ID_TEST_PLAN", null);
		if (Objects.isNull(testPlanKey)) {
			throw new Exception(
					"Se debe definir variable de entorno ID_TEST_PLAN, para actualizar en XRAY. Ejemplo SANAUT-33");
		}
		setProjectKey(testPlanKey.split("-")[0]);

		FilenameFilter filterCucumberJson = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("Runner.json");
			}
		};

		File reportPath = new File(MainRun.REPORT_FOLDER);
		log.info(reportPath.getAbsolutePath());
		List<File> filesCucumberFileJsonReport = Arrays.asList(reportPath.listFiles(filterCucumberJson));
		JSONArray featuresRead;
		JSONObject featureRead;
		JSONArray elements;
		JSONObject element;
		String fileContent;
		String testCaseKey;
		String testExecutionKey;
		HashMap<String, String> capabilitesInfo;
		String platform;
		log.info("Archivos terminados en Runner.json detectados: " + filesCucumberFileJsonReport.size());
		filesCucumberFileJsonReport.forEach(x -> log.info(x.getName()));

		for (File cucumberFileJsonReport : filesCucumberFileJsonReport) {
			fileContent = Utilities.readFile(cucumberFileJsonReport);
			if (fileContent.isEmpty()) {
				log.error("File JSON vacio " + cucumberFileJsonReport.getName());
				continue;
			}
			featuresRead = new JSONArray(fileContent);

			for (int i = 0; i < featuresRead.length(); i++) {
				featureRead = featuresRead.getJSONObject(i);
				elements = featureRead.getJSONArray(Constants.ELEMENTS);
				capabilitesInfo = getCapabilitesInfo(elements.getJSONObject(0));
				platform = capabilitesInfo.getOrDefault("platform", Constants.EMPTY);
				log.info("Feature: " + featureRead.get("name"));

				for (Object elementObject : elements) {
					element = (JSONObject) elementObject;
					// ver tags en element, y setea el testCaseKey limpio
					testCaseKey = getTestCaseKeyFromTags(element, platform);
					log.info(Constants.TEST_CASE_KEY + " ejecutado: " + testCaseKey);
				}

				Date dateTest = new Date();
				String executionName = cucumberFileJsonReport.getName().replace(".json", "") + " feature: "
						+ featureRead.get("name");
				String testExecutionCreated = JiraReporter.reportToXRayResult(
						generateFeatureWithTestCase(reportPath, dateTest, featuresRead),
						generateInfoFile(executionName, reportPath, dateTest, capabilitesInfo));
				log.info("TestExecution Creation: " + testExecutionCreated);
				testExecutionKey = getFieldFromJSObject(getFieldFromJSObject(testExecutionCreated, "testExecIssue"),
						"key");
				File cucumberFileHtmlReport = new File(
						cucumberFileJsonReport.getAbsolutePath().replace(".json", ".html"));
				if (cucumberFileHtmlReport.exists()) {
					log.info(cucumberFileHtmlReport.getAbsolutePath());
					String attachmentsToIssue = JiraReporter.attachmentsToIssue(cucumberFileHtmlReport,
							testExecutionKey);
					log.info("attachmentsToIssue: " + attachmentsToIssue);
				}
			}
		}

	}

	private HashMap<String, String> getCapabilitesInfo(JSONObject element) {
		HashMap<String, String> capabilitesInfo = new HashMap<String, String>();
		JSONObject embedding;
		for (Object object : element.getJSONArray("before").getJSONObject(0).getJSONArray("embeddings")) {
			embedding = (JSONObject) object;
			if (embedding.getString("name").contentEquals("capabilities")) {
				capabilitesInfo.putAll(new Gson().fromJson(Utilities.decodeBase64(embedding.getString("data")),
						capabilitesInfo.getClass()));
				break;
			}
		}
		return capabilitesInfo;
	}

	private String getFieldFromJSObject(String testExecutionCreated, String fieldName) {
		JSONObject jsonObject = new JSONObject(testExecutionCreated);
		if (!Objects.isNull(jsonObject.optJSONObject(fieldName))) {
			return jsonObject.getJSONObject(fieldName).toString();
		}
		return jsonObject.getString(fieldName);
	}

	private String getTestCaseKeyFromTags(JSONObject element, String platform) {
		JSONObject jsonObject;
		String name;
		String testCaseKey;
		String testCaseKeyPrefix = "TestCaseKey";
		if (platform != null && !platform.isEmpty() && !"null".equals(platform)) {
			testCaseKeyPrefix = platform + testCaseKeyPrefix;
		}

		try {
			for (Object object : element.getJSONArray("tags")) {
				jsonObject = (JSONObject) object;
				name = jsonObject.getString("name");
				if (name.contains(Constants.TEST_CASE_KEY)) {
					if (name.startsWith("@" + Constants.TEST_CASE_KEY)) {
						testCaseKey = name.split("=")[1];
						jsonObject.put("name", "@" + testCaseKey);
						return testCaseKey;
					} else if (name.startsWith("@" + platform + Constants.TEST_CASE_KEY)) {
						testCaseKey = name.split("=")[1];
						jsonObject.put("name", "@" + testCaseKey);
						return testCaseKey;
					}
				}
			}
		} catch (Exception e) {
			log.error("Error en getTestCaseKeyFromTags: " + e.getMessage());
		}
		return null;
	}

	private File generateInfoFile(String runnerName, File reportPath, Date dateTest,
			HashMap<String, String> capabilitesInfo) throws Exception {
		File fileOut = new File(reportPath.getAbsolutePath() + "/" + dateTest.getTime() + "_tmpInfo.json");
		JSONObject info = generateInfo(runnerName, capabilitesInfo);

		Utilities.writeFile(fileOut, info.toString());
		return fileOut;
	}

	public JSONObject generateInfo(String runnerName, HashMap<String, String> capabilitesInfo) throws Exception {
		String testExecutionName = System.getenv().getOrDefault("EXECUTION_NAME_PREFIX", "Execution for") + " ";

		JSONObject info = new JSONObject();
		JSONObject fields = new JSONObject();
		JSONObject issuetype = new JSONObject();
		JSONObject assignee = new JSONObject();

		fields.put("summary", testExecutionName + runnerName);
		JSONObject project = new JSONObject();
		project.put("key", projectKey);
		fields.put("project", project);
		issuetype.put("id", "10802");
		fields.put("issuetype", issuetype);
		// Validar assignee
		String assigneeUser = System.getenv().getOrDefault("JIRA_EXECUTOR", System.getenv("JIRA_USERNAME"));
		String userInfo = JiraReporter.getUser(assigneeUser);
		JSONObject userInfoObject = new JSONObject(userInfo);
		if (userInfoObject.optString("name", null) != null) {
			log.info("Usuario asignado: " + assigneeUser);
			assignee.put("name", assigneeUser);
			fields.put("assignee", assignee);
		} else {
			log.info("Usuario no valido para asignar '" + assigneeUser + "', no se asignara uno.");
		}

		// Custom Fields
		// TEST PLAN
		JSONArray customfieldTestPlan = new JSONArray();
		customfieldTestPlan.put(testPlanKey);
		fields.put(FIELD_TEST_PLAN, customfieldTestPlan);

		// Ambiente
		String ambienteEnviroment = System.getenv("AMBIENTE");
		JSONObject ambiente = new JSONObject();
		ambiente.put("id", JiraReporter.getEnviromentId(ambienteEnviroment));
		fields.put(FIELD_AMBIENTE, ambiente);

		if (capabilitesInfo != null) {
			String labExecution = capabilitesInfo.get("labExecution");
			String platform = capabilitesInfo.getOrDefault("platform", Constants.EMPTY);
			fields.put(FIELD_PORTAL, capabilitesInfo.getOrDefault("portal", Constants.EMPTY));
			if (platform.equals(Constants.EXECUTION_MOBILE)) {
				fields.put(FIELD_APLICACION_MOBILE, capabilitesInfo.getOrDefault("app", Constants.EMPTY));
				fields.put(FIELD_VERSION_APLICACION_MOBILE,
						capabilitesInfo.getOrDefault("app_version", Constants.EMPTY));
			} else {
				String browser = capabilitesInfo.getOrDefault("browser", null);
				if (browser != null && !browser.isEmpty()
						&& FIELD_NAVEGADOR_IDS.getOrDefault(browser.toLowerCase(), null) != null) {
					JSONObject navegador = new JSONObject();
					navegador.put("id", FIELD_NAVEGADOR_IDS.get(browser.toLowerCase()));
					fields.put(FIELD_NAVEGADOR, navegador);
					fields.put(FIELD_VERSION_NAVEGADOR,
							capabilitesInfo.getOrDefault("browser_version", Constants.EMPTY));
				}
			}

			if (capabilitesInfo.getOrDefault("os", Constants.EMPTY) != null
					&& !capabilitesInfo.getOrDefault("os", Constants.EMPTY).isEmpty()) {
				fields.put(FIELD_SISTEMA_OPERATIVO, capabilitesInfo.getOrDefault("os", Constants.SIN_INFORMAR));
			} else {
				fields.put(FIELD_SISTEMA_OPERATIVO, Constants.SIN_INFORMAR);
			}

			if (capabilitesInfo.getOrDefault("os_version", Constants.EMPTY) != null
					&& !capabilitesInfo.getOrDefault("os_version", Constants.EMPTY).isEmpty()
					&& !capabilitesInfo.getOrDefault("os_version", Constants.EMPTY).equalsIgnoreCase("null")) {
				fields.put(FIELD_VERSION_SISTEMA_OPERATIVO,
						capabilitesInfo.getOrDefault("os_version", Constants.SIN_INFORMAR));
			} else {
				fields.put(FIELD_VERSION_SISTEMA_OPERATIVO, Constants.SIN_INFORMAR);
			}
		}
		JSONObject tipoEjecucion = new JSONObject();
		tipoEjecucion.put("id", FIELD_TIPO_EJECUCION_AUTOMATIZADO);
		fields.put(FIELD_TIPO_EJECUCION, tipoEjecucion);
		// Put Fields
		info.put("fields", fields);
		return info;
	}

	private File generateFeatureWithTestCase(File reportPath, Date dateTest, JSONArray feature) {
		File fileOut = new File(reportPath.getAbsolutePath() + "/" + dateTest.getTime() + "_tmpFeature.json");
		Utilities.writeFile(fileOut, feature.toString());
		return fileOut;
	}

	static JSONObject generateFields(HashMap<String, String> capabilitesInfo, String featureName) throws Exception {
		String runnerName = System.getProperty("labExecution", "") + System.getProperty("browser", "");
		String executionName = runnerName + " feature: " + featureName;
		StageUploadResultJira stageUploadResultJira = new StageUploadResultJira();
		testPlanKey = System.getenv().getOrDefault("ID_TEST_PLAN", null);
		stageUploadResultJira.setProjectKey(testPlanKey.split("-")[0]);
		JSONObject info = stageUploadResultJira.generateInfo(executionName, capabilitesInfo);
		return info;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

}
