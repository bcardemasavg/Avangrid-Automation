package com.nttdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestUtils {
	
	public static String extractDate(Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, new Locale("es", "ES"));
        return formatter.format(date);
    }
	
	public static Date formattDate(String dateToFormat, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, new Locale("es", "ES"));
        return formatter.parse(dateToFormat);
    }

	public static String generarNombreUnico(String sufijo) {
		String result = sufijo + " " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		return result;
	}

	public static String generateRUT() {
		long timestamp = new Date().getTime();
		int number = (int) (timestamp % 100000000); // Genera un número de hasta 8 dígitos basado en el timestamp
		char dv = calculateDV(number);
		return number + "-" + dv;
	}

	private static char calculateDV(int rut) {
		int sum = 0;
		int multiplier = 2;

		while (rut > 0) {
			sum += (rut % 10) * multiplier;
			rut /= 10;
			multiplier = (multiplier == 7) ? 2 : multiplier + 1;
		}

		int remainder = 11 - (sum % 11);
		if (remainder == 11) {
			return '0';
		} else if (remainder == 10) {
			return 'K';
		} else {
			return (char) (remainder + '0');
		}
	}

}
