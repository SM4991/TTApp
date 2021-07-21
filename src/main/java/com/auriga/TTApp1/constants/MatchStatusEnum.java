package com.auriga.TTApp1.constants;

public enum MatchStatusEnum {
	INACTIVE("Inactive"),
	PENDING("Pending"),
	ONGOING("Live"),
	COMPLETE("Complete");
	
	private String value;
	
	private MatchStatusEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
