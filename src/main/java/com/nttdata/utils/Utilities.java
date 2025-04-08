package com.nttdata.utils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utilities {
	private static final String DATE_FORMAT_REPORTE = "yyyy-MM-dd_HH-mm-ss";
	private static final String DATE_FORMAT_HORA = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_JIRA = "yyyy-MM-dd'T'HH:mm:ssZ";
	protected static Logger log = LogManager.getLogger(Utilities.class);
	public static Gson GSON = new Gson();
	public static Gson GSOn_PRETTY = new GsonBuilder().setPrettyPrinting().create();

	public static boolean isValidDateTimeFormatter(String pattern, String dateTime) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			LocalDateTime.parse(dateTime, formatter);
			return true;
		} catch (DateTimeParseException var3) {
			return false;
		}
	}

	/**
	 * Formatea una fecha a String con el formato por defecto yyyy-MM-dd_HH-mm-ss
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HORA);
		return sdf.format(date);
	}

	/**
	 * Formato tipo fecha: ejemplo: yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static String getFechaHora() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_HORA);
		return sdf.format(new Timestamp(System.currentTimeMillis()));
	}

	public static String getFechaReporte() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_REPORTE);
		return sdf.format(new Timestamp(System.currentTimeMillis()));
	}

	public static String getFechaReporte(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Timestamp(System.currentTimeMillis()));
	}

	public static void setTimeZone() {
		TimeZone timeZone = TimeZone.getTimeZone("GMT-4:00");
		TimeZone.setDefault(timeZone);
	}

	public static void writeFile(File file, String content) {
		try (FileOutputStream fos = new FileOutputStream(file);
				DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));) {
			outStream.write(content.getBytes());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static String readFile(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			for (String line : Files.readAllLines(file.toPath())) {
				sb.append(line.trim());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return sb.toString();
	}

	public static String truncateString(String str, int max) {
		if (str.length() <= max) {
			return str;
		}
		return str.substring(0, max);
	}

	public static String encodeBase64(String originalInput) {
		return Base64.getEncoder().encodeToString(originalInput.getBytes());
	}

	public static String decodeBase64(String encodedString) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		return new String(decodedBytes);
	}
	
	public static String encodeBase64(byte[] input) {
		return Base64.getEncoder().encodeToString(input);
	}
	
	public static String decodeBase64(byte[] encodeInput) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodeInput);
		return new String(decodedBytes);
	}
}
