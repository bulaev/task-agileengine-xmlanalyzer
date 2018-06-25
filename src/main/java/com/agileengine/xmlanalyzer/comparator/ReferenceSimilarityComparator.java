package com.agileengine.xmlanalyzer.comparator;

import java.util.Comparator;

import org.jsoup.nodes.Element;

public class ReferenceSimilarityComparator implements Comparator<Element> {
	
	private ReferenceSimilarityChecker refSimilarityChecker;
	
	public ReferenceSimilarityComparator(ReferenceSimilarityChecker refSimilarityChecker) {
		this.refSimilarityChecker = refSimilarityChecker;
	}

	@Override
	public int compare(Element e1, Element e2) {
		return refSimilarityChecker.checkReferenceSimilarity(e2) - refSimilarityChecker.checkReferenceSimilarity(e1);
	}
	
}
