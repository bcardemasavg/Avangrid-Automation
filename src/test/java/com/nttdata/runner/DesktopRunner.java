package com.nttdata.runner;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nttdata.utils.Constants;
import com.nttdata.utils.database.BasicReader;
import com.nttdata.utils.database.ExcelReader;
import com.nttdata.utils.runner.MainRun;

public class DesktopRunner extends MainRun {

	static {
		try {
			String ambiente = System.getenv().getOrDefault("AMBIENTE", null);
            String filePath = new BasicReader(ambiente).getFileFromResources("Execution Control.xlsx").getAbsolutePath();
            ExcelReader excelReader = new ExcelReader(filePath);
            // Leer datos de una hoja por nombre
            JSONArray datos = excelReader.readSheetAsJson("TestCasesRunner");
			excelReader.close();
			
			JSONObject dato;
           for (Object object : datos) {
				dato = (JSONObject) object;
				if(dato.optString("Execute (Y/N)", "N").equalsIgnoreCase("Y")) {
					tags.add(dato.optString("TC ID"));
				}
		   }


            // Cerrar el lector
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static void config() {
		System.setProperty("platform", Constants.EXECUTION_DESKTOP);
	}
}
