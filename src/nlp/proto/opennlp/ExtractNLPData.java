package nlp.proto.opennlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class ExtractNLPData {
	
	
	private static String trainDataText = "resources/text_02_person_train.txt";
	private static String traindModel_person = "resources/en-ner-person-train.bin";
	
	public static String[] text_1_persons = {};
	public static String[] text_1_orgs = {"FinTRAC", "Capital One Bank USA", "Capital One", "Capital One Bank", "Wallmart", "Target", "Quality Inn", "Best Buy"};
	public static String[] text_1_address = {"NA", "Canada"};
	public static String[] text_1_dates = {"1/12/2017", "3/15/2016", "5/14/2016", "5/12/2016", "5/14/2016"};
	public static String[] text_1_money = {"$8 , 098. 76", "$8 , 098. 76", "$ 30 . 03"};
	public static String[] text_1_keywords = {"fraud", "fraudulent"};
	public static String[] text_1_email = {"ThisIsFake@CapitalOne.com"};
	
	public static String[] text_2_persons = {"Jong CHEN", "Jong CHEN", "Jong CHEN", "John", "Jackie CHEN"};
	public static String[] text_2_orgs = {"FINTRAC", "HBCA", "HBCA", "HBCA"};
	public static String[] text_2_address = {"China", "China", "133 YOUNGE ST  Toronto ON M 0 G 7 V 8, Canada", "18 STRANDHURST COURT Barrie ON L7F4L1, Canada"};
	public static String[] text_2_dates = {"21Dec2016", "09Dec2018", "14Jul2009", "24Jan1992"};
	public static String[] text_2_money = {"CAD 30 , 200 . 00", "CAD 45 , 300. 00"};
	public static String[] text_2_accounts = {"#111-111111-123"};
	public static String[] text_2_keywords = {"money laundering"};
	
	public static String[] text_3_persons = {"Min TZING", "Sammy Sung"};
	public static String[] text_3_orgs = {"Flamenco Realty", "Amex Flamenco Realty"};
	public static String[] text_3_address = {"3330 Southbend Road, West Vancouver  C 2 V 9 K 7"};
	public static String[] text_3_dates = {"13 Dec 2012", "14 Dec 2012", "17 Dec 2012", "January 13th, 2013", "Feb 25 , 2013", "Feb 24 , 2013", "06 Feb 2013"};
	public static String[] text_3_money = {"$ 200 , 000", "TWO MILLION Dollars", "two million Canadian dollars", "Four Million Dollars"};
	public static String[] text_3_keywords = {};
	
	
	private static SimpleTokenizer tokenizer;
	
	public static void main(String args[]) {
		tokenizer = SimpleTokenizer.INSTANCE;
		try {
			Map<String, List<String>> accMap_1 = new HashMap<String, List<String>>();
			accMap_1.put("person", Arrays.asList(text_1_persons));
			accMap_1.put("org", Arrays.asList(text_1_orgs));
			accMap_1.put("date", Arrays.asList(text_1_dates));
			accMap_1.put("money", Arrays.asList(text_1_money));
			accMap_1.put("location", Arrays.asList(text_1_address));
			
			Map<String, List<String>> accMap_2 = new HashMap<String, List<String>>();
			accMap_2.put("person", Arrays.asList(text_2_persons));
			accMap_2.put("org", Arrays.asList(text_2_orgs));
			accMap_2.put("date", Arrays.asList(text_2_dates));
			accMap_2.put("money", Arrays.asList(text_2_money));
			accMap_2.put("location", Arrays.asList(text_2_address));
			
			Map<String, List<String>> accMap_3 = new HashMap<String, List<String>>();
			accMap_3.put("person", Arrays.asList(text_3_persons));
			accMap_3.put("org", Arrays.asList(text_3_orgs));
			accMap_3.put("date", Arrays.asList(text_3_dates));
			accMap_3.put("money", Arrays.asList(text_3_money));
			accMap_3.put("location", Arrays.asList(text_3_address));
			
			extractEntitiesForText("text_01.txt", accMap_1);
			extractEntitiesForText("text_02.txt", accMap_2);
			extractEntitiesForText("text_03.txt", accMap_3);
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void trainPersonData(TokenNameFinderModel model) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		InputStreamFactory isf = new InputStreamFactory() {

			@Override
			public InputStream createInputStream() throws IOException {
				return new FileInputStream(trainDataText);
			}
		};
		ObjectStream<String> lineStream = new PlainTextByLineStream(isf, charset);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
		try {
			model = NameFinderME.train("en", "person", sampleStream, TrainingParameters.defaultParams(), nameFinderFactory);
		} finally {
			sampleStream.close();
		}
		
	}
	


	private static void extractEntitiesForText(String file, Map<String, List<String>> accMap) throws IOException {
		System.out.println("Extracting entities for " + file);
		System.out.println("*******PERSON *******");
		extractEntities(file, "en-ner-person.bin", accMap.get("person"));
		//trainAndExtractNames();
		System.out.println("*******ORG *******");
		
		extractEntities(file, "en-ner-organization.bin", accMap.get("org"));
		//trainAndExtractOrgs();
		
		System.out.println("*******DATE *******");
		
		extractEntities(file, "en-ner-date.bin", accMap.get("date"));
		//trainAndExtractDates():
		System.out.println("*******LOCATION *******");
		
		extractEntities(file, "en-ner-location.bin", accMap.get("location"));
		//trainAndExtractDates():
		System.out.println("*******MONEY *******");
		extractEntities(file, "en-ner-money.bin", accMap.get("money"));
	}
	
	private static String[] tokenizeText(String filePath) {
		Stream<String> text = null;
		String[] tokens = null;
		try {
			text = Files.lines(Paths.get(filePath));
			String content = text.collect(Collectors.joining("/"));
			tokens = tokenizer.tokenize(content);
		} catch (IOException e) {
			e.printStackTrace();	
		}
		if (text != null) {
			text.close();
		}
		return tokens;
	}
	
	private static void extractData(TokenNameFinderModel model, String[] tokens, List<String> accList) {
		NameFinderME nameFinderME = new NameFinderME(model);
		List<Span> spans = Arrays.asList(nameFinderME.find(tokens));
		int expected = accList.size();
		System.out.println("Expecting " + expected + " items");
		int found = 0;
		int unexpected = 0;
		for(Span span: spans) {
			StringBuilder sb = new StringBuilder();
			System.out.println(span.toString() + "(" + span.getProb() + ")");
			for (int i = span.getStart(); i < span.getEnd(); i++) {
			    System.out.print(Arrays.asList(tokens).get(i).toString() + " ");
			    sb.append(Arrays.asList(tokens).get(i) + " ");
			}
			System.out.println("");
			if (accList.contains(sb.toString().trim())) {
				found++;
			} else {
				System.out.println(sb.toString() + "is not expected");
				unexpected++;
			}
		}
		if (expected > 0) {
			System.out.println("Accuracy: " + (100*found/expected) + "%");
		}
	}
	
    private static void extractEntities(String inputTextFile, String modelFile, List<String> accList) throws IOException {
    		String[] tokens = tokenizeText("resources/" + inputTextFile);
        InputStream nameData = ExtractNLPData.class.getClassLoader().getResourceAsStream(modelFile);	
		TokenNameFinderModel model = new TokenNameFinderModel(nameData);
		extractData(model, tokens, accList);	
	}
}