package com.agileengine.xmlanalyzer.comparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

public class ReferenceSimilarityChecker {

	private Element referenceElement;
	private Element otherElement;
	private Map<Element, List<AttributeValue>> history = new HashMap<>();
	
	public ReferenceSimilarityChecker(Element referenceElement) {
		this.referenceElement = referenceElement;
	}
	
	public int checkReferenceSimilarity(Element otherElement) {
		this.otherElement = otherElement;
		int similarityValue = 0;
		Attributes attrs = otherElement.attributes();
		
		history.put(otherElement, new ArrayList<AttributeValue>());
		
		for(Attribute attr : attrs) {
			if(isAttrValueAssigned(attr.getKey())) {
				similarityValue += checkAttrSimilarity(attr.getKey(), attr.getValue());
			}
		}
		
		return similarityValue;
	}
	
	public Map<Element, List<AttributeValue>> getHistory() {
		return history;
	}

	private int checkAttrSimilarity(String attrName, String otherAttrValue) {
		String referenceAttrValue = referenceElement.attr(attrName);
		
		if(referenceAttrValue.equals(otherAttrValue)) {
			AttributeValue attributeValue = AttributeValue.valueOf(attrName.toUpperCase());
			history.get(otherElement).add(attributeValue);
			
			return attributeValue.getValue();
		}
		
		return 0;
	}
	
	private static boolean isAttrValueAssigned(String attrName) {
		for (AttributeValue attrValue : AttributeValue.values()) {
			if (attrValue.name().equalsIgnoreCase(attrName)) {
				
				return true;
			}
		}

		return false;
	}
	
}
