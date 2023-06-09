package com.auriga.TTApp1.constants;

public enum RoundTypeEnum {
	PRE("Pre"),
	QUATERFINAL("Quater Final"),
	SEMIFINAL("Semi Final"),
	FINAL("Final");
	
	private String value;
	
	private RoundTypeEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
