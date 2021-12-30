package com.p3.archon.coreprocess.executables.tools.utility.common;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public final class Utility {

	public static boolean isBlank(final CharSequence text) {
		if (text == null || text.length() == 0) {
			return true;
		}
		for (int i = 0; i < text.length(); i++) {
			if (!Character.isWhitespace(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static String getTextFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string.length() > 0 ? ((string.charAt(0) >= '0' && string.charAt(0) <= '9') ? "_" : "") + string
				: string;
	}

	public static String getRowCountMessage(final Number number) {
		requireNonNull(number, "No number provided");
		final long longValue = number.longValue();
		if (longValue <= 0) {
			return "empty";
		} else {
			return String.format("%,d rows", longValue);
		}
	}

	public static String readFully(final InputStream stream) {
		if (stream == null) {
			return null;
		}
		final Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
		return readFully(reader);
	}

	public static String readFully(final Reader reader) {
		if (reader == null) {
			System.out.println("Cannot read null reader");
			return "";
		}
		try {
			final StringWriter writer = new StringWriter();
			copy(reader, writer);
			writer.close();
			return writer.toString();
		} catch (final IOException e) {
			e.printStackTrace();
			return "";
		}

	}

	public static String readResourceFully(final String resource) {
		return readFully(Utility.class.getResourceAsStream(resource));
	}

	public static void copy(final Reader reader, final Writer writer) {
		if (reader == null) {
			System.out.println("Cannot read null reader");
			return;
		}
		if (writer == null) {
			System.out.println("Cannot write null writer");
			return;
		}

		final char[] buffer = new char[0x10000];
		try {
			final Reader bufferedReader = new BufferedReader(reader, buffer.length);
			final BufferedWriter bufferedWriter = new BufferedWriter(writer, buffer.length);
			int read;
			do {
				read = bufferedReader.read(buffer, 0, buffer.length);
				if (read > 0)
					bufferedWriter.write(buffer, 0, read);
			} while (read >= 0);
			bufferedWriter.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
