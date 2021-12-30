package com.p3.archon.coreprocess.executables.tools.utility.html;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.isBlank;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.common.Color;

public class Anchor extends BaseTag {

	public Anchor(final String text, final boolean escapeText, final int characterWidth, final Alignment align,
			final boolean emphasizeText, final String styleClass, final Color bgColor, final String link,
			final TextOutputFormat outputFormat) {
		super(text, escapeText, characterWidth, align, emphasizeText, styleClass, bgColor, outputFormat);
		if (!isBlank(link)) {
			addAttribute("href", link);
		}
	}

	@Override
	protected String getTag() {
		return "a";
	}

}
