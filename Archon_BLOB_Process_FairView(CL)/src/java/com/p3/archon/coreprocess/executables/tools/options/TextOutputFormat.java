package com.p3.archon.coreprocess.executables.tools.options;

public enum TextOutputFormat {

	text("Plain text format"), html("HyperText Markup Language (HTML) format"), csv(
			"Comma-separated values (CSV) format"), tsv("Tab-separated values (TSV) format"), json(
					"JavaScript Object Notation (JSON) format"), xml("eXtensive Markup Language (XML) format"), file(
							"Unstructured Content format"), arxml("Analysis Report Format");
	;

	public static TextOutputFormat valueOfFromString(final String format) {
		TextOutputFormat outputFormat;
		try {
			outputFormat = TextOutputFormat.valueOf(format);
		} catch (final IllegalArgumentException | NullPointerException e) {
			outputFormat = text;
		}
		return outputFormat;
	}

	public static boolean isTextOutputFormat(final String format) {
		try {
			TextOutputFormat.valueOf(format);
			return true;
		} catch (final IllegalArgumentException | NullPointerException e) {
			return false;
		}
	}

	private final String description;

	private TextOutputFormat(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
