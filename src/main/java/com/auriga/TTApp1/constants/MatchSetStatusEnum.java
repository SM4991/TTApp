package com.auriga.TTApp1.constants;

public enum MatchSetStatusEnum {
	PENDING("Pending"),
	ONGOING("On going"),
	COMPLETE("Complete");
	
	private String value;
	
	private MatchSetStatusEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
