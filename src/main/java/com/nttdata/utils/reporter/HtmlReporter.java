package com.nttdata.utils.reporter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.nttdata.utils.Utilities;
import com.nttdata.utils.runner.MainRun;

import io.cucumber.java.Status;

public class HtmlReporter {

	public static File generate(TestCase testCase) throws Exception {
		File report = new File(MainRun.REPORT_FOLDER + "Report_" + testCase.getTestKey() + ".html");
		List<TestCase> testCases = new ArrayList<TestCase>();
		report.delete();
		testCases.add(testCase);
		generate(testCases, report);
		return report;
	}

	public static File generate(List<TestCase> testCases, String featureName) throws Exception {
		File report = new File(MainRun.REPORT_FOLDER + "Global_Report_" + featureName + ".html");
		report.delete();
		return generate(testCases, report);
	}

	private static File generate(List<TestCase> testCases, File report) throws Exception {

		String jsonCasosDePrueba = "[";
		for (int i = 0; i < testCases.size(); i++) {
			if (i != 0) {
				jsonCasosDePrueba += ",";
			}
			jsonCasosDePrueba += generateJSOnReportTestCase(testCases.get(i));
		}

		jsonCasosDePrueba += "]";

		String htmlReport = HtmlTemplate.TEMPLATE.replace("@VARIABLE_CASOS_DE_PRUEBA", jsonCasosDePrueba);

		String casosPassed = ""
				+ testCases.stream().filter(x -> Status.PASSED.toString().equals(x.getStatus())).count();
		String casosFailed = ""
				+ testCases.stream().filter(x -> Status.FAILED.toString().equals(x.getStatus())).count();
		htmlReport = htmlReport.replace("@VARIABLE_RESULTADO", generarResumenResultado(testCases));
		htmlReport = htmlReport.replace("@VARIABLE_CASOS_DE_PASSED", casosPassed);
		htmlReport = htmlReport.replace("@VARIABLE_CASOS_DE_FAILED", casosFailed);
		htmlReport = htmlReport.replace("@VARIABLE_CASOS_DE_TOTAL", "" + testCases.size());

		Files.write(report.toPath(), htmlReport.getBytes(), StandardOpenOption.CREATE);
		return report;
	}

	private static String generarResumenResultado(List<TestCase> testCases) {
		long casosPassed = testCases.stream().filter(x -> Status.PASSED.toString().equals(x.getStatus())).count();
		long casosFailed = testCases.stream().filter(x -> Status.FAILED.toString().equals(x.getStatus())).count();
		String result = "<h2>Casos Ejecutados: " + testCases.size() + ", PASSED: " + casosPassed + " [ "
				+ ((int) casosPassed * 100 / testCases.size()) + " % ] -  FAILED: " + casosFailed + " [ "
				+ ((int) casosFailed * 100 / testCases.size()) + "% ]</h2>";

		String ambiente = System.getenv("AMBIENTE");
		String aplicativo = System.getenv("APLICATIVO");
		result += "<h3>Ambiente: " + ambiente;
		if (aplicativo != null) {
			result += " : " + aplicativo;
		}
		String testPlanKey = System.getenv().getOrDefault("ID_TEST_PLAN", null);
		if (testPlanKey != null) {
			result += " - Test Plan:  " + testPlanKey;
		}
		String testExecutionKey = System.getProperty("testExecutionKey", null);
		if (testExecutionKey != null) {
			result += " - Test Execution: " + testExecutionKey;
		}
		result += "</h3>";
		result += "<h3>Fecha Inicio: " + Utilities.formatDate(testCases.get(0).getStart(), "yyyy-MM-dd HH:mm:ss")
				+ " - Fecha Termino: "
				+ Utilities.formatDate(testCases.get(testCases.size() - 1).getFinish(), "yyyy-MM-dd HH:mm:ss") + "</br>"
				+ "Duracion: "
				+ Duration
						.between(testCases.get(0).getStart().toInstant(),
								testCases.get(testCases.size() - 1).getFinish().toInstant())
						.toString().replace("PT", "")
				+ "</h3>";
		return result;
	}

	private static String generateJSOnReportTestCase(TestCase testCase) {
		Step step;
		String jsonCasosDePrueba = "{";
		jsonCasosDePrueba += "    id: '" + testCase.getTestKey() + "',";
		jsonCasosDePrueba += "    nombre: '" + testCase.getName() + "',";
		jsonCasosDePrueba += "    resultado: '" + testCase.getStatus() + "',";
		jsonCasosDePrueba += "    resultadoColor: '" + testCase.getStatus().toLowerCase() + "',";
		jsonCasosDePrueba += "    start: '" + Utilities.formatDate(testCase.getStart(), "yyyy-MM-dd HH:mm:ss") + "',";
		jsonCasosDePrueba += "    finish: '" + Utilities.formatDate(testCase.getFinish(), "yyyy-MM-dd HH:mm:ss") + "',";
		jsonCasosDePrueba += "    duracion: '" + Duration
				.between(testCase.getStart().toInstant(), testCase.getFinish().toInstant()).toString().replace("PT", "")
				+ "',";
		jsonCasosDePrueba += "    testAttachs: [";
		for (int i = 0; i < testCase.getAttachments().size(); i++) {
			if (i != 0) {
				jsonCasosDePrueba += ",";
			}
			jsonCasosDePrueba += "{" + "text: '"
					+ new String(Base64.getDecoder().decode((String) testCase.getAttachments().get(i).getData())) + "'"
					+ ", name: '" + testCase.getAttachments().get(i).getName() + "'" + "}";
		}
		jsonCasosDePrueba += "],";
		jsonCasosDePrueba += "    error: \""
				+ (Objects.isNull(testCase.getError()) ? "" : generarHTMlConErrorOcultable(testCase)) + "\",";
		jsonCasosDePrueba += "    pasos: [";
		for (int i = 0; i < testCase.getSteps().size(); i++) {
			step = testCase.getSteps().get(i);
			if (i != 0) {
				jsonCasosDePrueba += ",";
			}
			jsonCasosDePrueba += "{ descripcion: '" + step.getKeyWord() + " " + step.getText() + "', ";
			jsonCasosDePrueba += "resultado: '" + step.getStatus().toLowerCase() + "', ";
			jsonCasosDePrueba += "evidencias:[";
			for (int j = 0; j < step.getAttachments().size(); j++) {
				if (j != 0) {
					jsonCasosDePrueba += ",";
				}
				jsonCasosDePrueba += "{";
				jsonCasosDePrueba += "'text' : '"
						+ ((step.getAttachments().get(j).getName() != null) ? step.getAttachments().get(j).getName()
								: "")
						+ "',";
				jsonCasosDePrueba += "data: 'data:" + step.getAttachments().get(j).getMediaType() + ";base64,"
						+ step.getAttachments().get(j).getData() + "'";
				jsonCasosDePrueba += "}";
			}
			jsonCasosDePrueba += "]";
			jsonCasosDePrueba += "}";

		}
		jsonCasosDePrueba += "    ]";
		jsonCasosDePrueba += "}";
		return jsonCasosDePrueba;
	}

	private static String generarHTMlConErrorOcultable(TestCase testCase) {
		String result = "<button class='boton-bonito' onclick='toggleSpan(\\\"" + testCase.getTestKey()
				+ "\\\")'>Mostrar Error</button>"
				+ "<p style='max-width: 1000px;'><code style='word-break: break-word; display: none;' id='error-"
				+ testCase.getTestKey() + "'>" + testCase.getError().replace("\n", "</br>").replace("\"", "\\\"")
				+ "</code></p>";
		return result;
	}

}
