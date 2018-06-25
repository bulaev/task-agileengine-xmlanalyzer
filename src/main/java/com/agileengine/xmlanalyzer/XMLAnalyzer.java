package com.agileengine.xmlanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agileengine.xmlanalyzer.comparator.ReferenceSimilarityChecker;
import com.agileengine.xmlanalyzer.comparator.ReferenceSimilarityComparator;

public class XMLAnalyzer {

	private static Logger LOGGER = LoggerFactory.getLogger(XMLAnalyzer.class);

	private static final String CHARSET_NAME = "utf8";
	private static final int ARGS_NUMBER = 3;
	
	public static void main(String[] args) {
		try {
			String originFilePath;
			String diffFilePath;
			String targetElementId;
			
			if(args.length != ARGS_NUMBER) {
				throw new IllegalArgumentException("Incorrect number of input arguments: " + args.length + ". Expected: " + ARGS_NUMBER);
			} else {
				originFilePath = args[0];
				diffFilePath = args[1];
				targetElementId = args[2];
			}
			
			Optional<Document> originDocumentOpt = parseFile(new File(originFilePath));
			Optional<Document> diffDocumentOpt = parseFile(new File(diffFilePath));
			
			if(originDocumentOpt.isPresent() && diffDocumentOpt.isPresent()) {
				Optional<Element> targetElementOpt = findElementById(originDocumentOpt.get(), targetElementId);
				
				if(targetElementOpt.isPresent()) {
					Element targetElement = targetElementOpt.get();
					ReferenceSimilarityChecker refSimilarityChecker = new ReferenceSimilarityChecker(targetElement);
					
					Elements diffTargetElements = findElementsByQuery(diffDocumentOpt.get(), targetElement.tagName());
					
					if(diffTargetElements.size() > 0) {
						diffTargetElements.sort(new ReferenceSimilarityComparator(refSimilarityChecker));
						
						printOutput(refSimilarityChecker, diffTargetElements.get(0));
					} else {
						LOGGER.info("Unable to locate target element in diff file.");
					}
				} else {
					LOGGER.info("Unable to locate target element in origin file.");
				}
			}
		} catch(IllegalArgumentException e) {
			LOGGER.error("", e);
		}
	}
	
	private static void printOutput(ReferenceSimilarityChecker refSimilarityChecker, Element targetElement) {
		StringBuilder absPath = new StringBuilder();
		
		LOGGER.info("Element path:");

		for (int i = targetElement.parents().size() - 1; i >= 0; i--) {
			Element element = targetElement.parents().get(i);
			absPath.append(element.tagName());
			absPath.append(" > ");
		}
		absPath.append(targetElement.tagName());
		
		LOGGER.info(absPath.toString());
		LOGGER.info("Attributes involved in decision making (Name and value):");
		
		refSimilarityChecker.getHistory().get(targetElement).forEach(attrValue -> {
			LOGGER.info(attrValue.toString() + " : " + attrValue.getValue());
		});
	}
	
	private static Elements findElementsByQuery(Document doc, String cssQuery) {
		return doc.select(cssQuery);
	}
	
	private static Optional<Element> findElementById(Document doc, String targetElementId) {
		return Optional.ofNullable(doc.getElementById(targetElementId));
	}
	
	private static Optional<Document> parseFile(File htmlFile) {
		try {
			Document doc = Jsoup.parse(
					htmlFile,
					CHARSET_NAME,
					htmlFile.getAbsolutePath());
			
			return Optional.of(doc);
			
			} catch (IOException e) {
				LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
				return Optional.empty();
			}
	}

}
