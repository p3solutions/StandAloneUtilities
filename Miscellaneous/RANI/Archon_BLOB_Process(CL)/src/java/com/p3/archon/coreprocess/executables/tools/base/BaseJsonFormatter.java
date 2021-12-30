package com.p3.archon.coreprocess.executables.tools.base;

import java.io.PrintWriter;
import java.util.logging.Logger;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.JsonFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONException;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

public abstract class BaseJsonFormatter extends BaseFormatter {

	protected static final Logger LOGGER = Logger.getLogger(BaseJsonFormatter.class.getName());

	protected final JSONObject jsonRoot;
	protected final PrintWriter out;

	protected BaseJsonFormatter(PrintWriter out, TextOutputFormat outputFormat) throws Exception {
		super(out, outputFormat, false);
		this.out = out;
		jsonRoot = new JSONObject();
	}

	public void end() throws Exception {
		try {
			((JsonFormattingHelper) formattingHelper).write(jsonRoot);
			out.flush();
			out.close();
		} catch (final JSONException e) {
			throw new Exception("Could not write database", e);
		}
	}

}
