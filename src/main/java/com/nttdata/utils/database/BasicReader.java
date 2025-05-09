package com.nttdata.utils.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.nttdata.utils.JsonDataReader;
import com.nttdata.utils.Utilities;

public class BasicReader extends JsonDataReader {
	public final String basePath;
	public String environmentsPath;
	public final String dbPath;
	protected JSONArray environments;
	protected List<DatabaseConnection> dbs;

	public BasicReader(String ambiente) {
		this.basePath = this.configFileReader.getTestDataResourcesPath() + ambiente;
		this.environmentsPath = this.configFileReader.getEnvironmentFileName();
		this.dbPath = this.basePath + this.configFileReader.getDatabaseConnectionFileName();
	}

	public void loadMobileEnviroments() {
		this.environmentsPath = this.configFileReader.getEnvironmentMobileFileName();
	}

	public void loadEnviroments() {
		this.environmentsPath = this.configFileReader.getEnvironmentFileName();
	}

	public JSONArray getEnvironments() {
		if (this.environments == null) {
			this.environments = new JSONArray(Utilities.readFile(new File((this.environmentsPath))));
		}
		return this.environments;
	}

	public List<DatabaseConnection> getDatabaseConections() {
		if (this.dbs == null) {
			this.dbs = Arrays.asList(
					(new Gson()).fromJson(this.getBufferReaderFromPath(this.dbPath), DatabaseConnection[].class));
		}
		return this.dbs;
	}

	public JSONObject getEnvironmentByName(String environmentName) {
		JSONObject env = null;
		for (Object objEnv : this.getEnvironments()) {
			env = (JSONObject) objEnv;
			if (env.getString("name").equals(environmentName)) {
				return env;
			}
		}

		return null;
	}

	public DatabaseConnection getDatabaseByName(String db) {
		return this.getDatabaseConections().stream().filter(x -> x.name.equalsIgnoreCase(db)).findFirst().orElse(null);
	}

	public JSONArray readJsonFile(String fileName) {
		JSONArray fileContentArray = new JSONArray(Utilities.readFile(getFileFromResources(fileName)));
		return fileContentArray;
	}

	public JSONArray readExcelFile(String fileName, String sheetName) throws IOException {
		String filePath = getFileFromResources(fileName).getAbsolutePath();
        ExcelReader excelReader = new ExcelReader(filePath);
		JSONArray fileContentArray = excelReader.readSheetAsJson(sheetName);
		return fileContentArray;
	}

	public File getFileFromResources(String fileName) {
		return new File(this.basePath + "/" + fileName);
	}

	public File getConfigurationFileName() {
		return new File(this.configFileReader.getConfigurationFileName());
	}

}
