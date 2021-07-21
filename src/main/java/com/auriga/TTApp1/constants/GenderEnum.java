package com.auriga.TTApp1.constants;

public enum GenderEnum {
	FEMALE("Female"),
	MALE("Male"),
	OTHER("Other");
	
	private String value;
	
	private GenderEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
