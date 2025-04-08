package com.nttdata.utils.reporter;

import com.nttdata.utils.Utilities;

public class Attachment {

	private Object data;
	private String mediaType;
	private String name;

	public Attachment(Object data, String mediaType, String name) {
		super();
		this.data = data;
		this.setMediaType(mediaType);
		this.name = name;
	}

	@Override
	public String toString() {
		return Utilities.GSOn_PRETTY.toJson(this);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

}
