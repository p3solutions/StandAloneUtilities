package com.p3.archon.coreprocess.executables.tools.utility;

import java.io.InputStream;
import java.sql.Blob;

public class BinaryData {
	private final boolean hasData;
	private final String data;
	private final Blob blob;
	private final InputStream stream;

	public BinaryData() {
		blob = null;
		data = null;
		hasData = false;
		stream = null;
	}

	public BinaryData(final String data, Blob blob, InputStream stream) {
		this.blob = blob;
		this.stream = stream;
		this.data = data;
		hasData = true;
	}

	public Blob getBlob() {
		return blob;
	}

	public InputStream getStream() {
		return stream;
	}

	public boolean hasData() {
		return hasData;
	}

	@Override
	public String toString() {
		if (hasData) {
			return data;
		} else {
			return "<binary>";
		}
	}
}
