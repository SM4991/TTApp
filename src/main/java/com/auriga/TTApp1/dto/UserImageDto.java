package com.auriga.TTApp1.dto;

import com.auriga.TTApp1.util.FileUtil;

public class UserImageDto extends FileDto {
	/* Get files upload path */
	public String getFilesUploadPath() {
		return FileUtil.getUserFilesUploadPath();
	}
}
