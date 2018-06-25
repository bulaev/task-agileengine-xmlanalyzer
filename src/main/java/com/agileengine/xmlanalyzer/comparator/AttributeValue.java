package com.agileengine.xmlanalyzer.comparator;

public enum AttributeValue {

	ID(8), CLASS(2), ONCLICK(2), HREF(1), TITLE(1), REL(1);
	
	private int value;
	
	private AttributeValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
