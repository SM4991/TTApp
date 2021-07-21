package com.auriga.TTApp1.constants;

public enum TournamentStatusEnum {
	PENDING("Pending"),
	ONGOING("On going"),
	COMPLETE("Complete");
	
	private String value;
	
	private TournamentStatusEnum(String value) {
        this.value = value;
    }

    public String getDisplayValue() {
        return value;
    }
}
