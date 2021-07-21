package com.auriga.TTApp1.constants;

public enum TournamentTypeEnum {
	MENSSINGLE("Men's Single"),
	WOMENSSINGLE("Women's Single");
	
	private String value;
	
	private TournamentTypeEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
