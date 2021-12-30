package com.p3.archon.coreprocess.executables.tools.utility;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONException;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

public class JsonFormattingHelper extends PlainTextFormattingHelper {

	public JsonFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
		super(out, outputFormat);
	}

	public void write(final JSONObject jsonObject) throws Exception {
		try {
			jsonObject.write(out, 2);
		} catch (final JSONException e) {
			throw new Exception("Could not write database", e);
		}
	}

}
