package com.auriga.TTApp1.constants;

public enum MatchStatusEnum {
	INACTIVE,
	PENDING,
	LIVE,
	COMPLETE;
	
	public static String getEnumText(MatchStatusEnum value) {
		String text = "";
		switch (value) {
		case INACTIVE:
			text = "Inactive";
			break;
		case PENDING:
			text = "Pending";
			break;
		case LIVE:
			text = "Live";
			break;
		case COMPLETE:
			text = "Complete";
			break;
		default:
			text = "";
			break;
		}
		return text;
	}
}
