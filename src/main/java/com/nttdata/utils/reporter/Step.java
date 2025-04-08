package com.nttdata.utils.reporter;

import java.util.ArrayList;
import java.util.List;

import com.nttdata.utils.Utilities;

public class Step {
	private int fileLine;
	private int number;
	private String status;
	private String text;
	private String keyWord;
	private String error;
	private String dataTable;
	private List<Attachment> attachments = new ArrayList<Attachment>();

	@Override
	public String toString() {
		return Utilities.GSON.toJson(this);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getFileLine() {
		return fileLine;
	}

	public void setFileLine(int fileLine) {
		this.fileLine = fileLine;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDataTable() {
		return dataTable;
	}

	public void setDataTable(String dataTable) {
		this.dataTable = dataTable;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

}
