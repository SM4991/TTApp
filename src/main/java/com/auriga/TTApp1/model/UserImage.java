package com.auriga.TTApp1.model;

import org.springframework.web.multipart.MultipartFile;

public class UserImage {
	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}