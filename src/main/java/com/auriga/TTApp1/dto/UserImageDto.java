package com.auriga.TTApp1.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserImageDto {
	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
}
