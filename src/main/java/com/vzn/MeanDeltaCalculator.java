package com.vzn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MeanDeltaCalculator {
	//c+Nx to find c use pattern (?<=)(.*)(?=+)
	//c-Nx to find c use pattern (?<=)(.*)(?=-)
	//-c+Nx to find c use pattern (?<=-)(.*)(?=+)
	//-c-Nx to find c use pattern (?<=-)(.*)(?=-)
			
	static String[] patternsToFindC = new String[] {"(?<=)(.*)(?=\\+)","(?<=)(.*)(?=-)","(?<=-)(.*)(?=\\+)",
				"(?<=-)(.*)(?=-)"};

	public static void main(String[] args) throws Exception {
		//String x = findCInTheEquation("-c+nx");
	   // String y = findCInTheEquation("-c+nx","c");

	    System.out.println(meanDelta("2+3x","input.csv"));
	    System.out.println(meanDelta("-2+3x","input.csv"));
	    System.out.println(meanDelta("2-3x","input.csv"));
	    System.out.println(meanDelta("-2-3x","input.csv"));
	}
	
	private static double meanDelta(String equation, String fileName) throws Exception {
		
		List<List<String>> records =  readCsvFile(fileName);
		double sumOfSquares = 0;
		
		for(List<String> record: records) {
			sumOfSquares+= dealtaSquare(equation, Double.valueOf(record.get(0)), Double.valueOf(record.get(1)));
		}
		
		double meanDelta = sumOfSquares/ records.size();
		
		return meanDelta;
	}
	
	private static double dealtaSquare(String equation, double x, double y) throws Exception{
		double yEq = evaluateEquation(equation, x);
		double delta = yEq -y;
		
		return delta * delta;
	}

	//c op nx
	private static double evaluateEquation(String equation, double x) throws Exception{
		String cString = findCInTheEquation(equation);
		
		double c = Double.valueOf(cString);
		double n = Double.valueOf(findNInTheEquation(equation, cString));
		
		// c op nx is now c+nx since op is merged with n value so + - is same as - and 
		// ++ same as +
		return c + (n*x);
	}
	
	private static String findCInTheEquation(String equation) throws Exception{
		
		for (String pattern : patternsToFindC) {
			Matcher matcher = Pattern.compile(pattern).matcher(equation);
			if(matcher.find()) {
				return matcher.group(0);
			}
		}
		
		throw new Exception(" Equation doesn't match any pattern");
	}
	
	private static String findNInTheEquation(String equation, String c) throws Exception{
		// n is in between c and x
		
		String pattern = "(?<=c)(.*)(?=x)";
		
		// replacing place holder c with actual value of c
		pattern = pattern.replace("c", c);
		Matcher matcher = Pattern.compile(pattern).matcher(equation);
		if(matcher.find()) {
			return matcher.group(0);
		}
		
		throw new Exception(" Equation doesn't match the pattern");
	}
	
	// return list of row and each row is list of size 2 where index 0 has value of x , index 1 has value of y
	
	private static List<List<String>> readCsvFile( String fileName) throws FileNotFoundException, IOException{
		
		List<List<String>> records = new ArrayList<>();
		
		try(InputStreamReader isReader = new InputStreamReader(MeanDeltaCalculator.class.getClassLoader().getResourceAsStream(fileName));
				BufferedReader br = new BufferedReader(isReader)){
			
			String line = null;
			while((line = br.readLine()) != null) {
				String[] values = line.split(",");
				records.add(Arrays.asList(values));
			}
			
		}
		return records.subList(1, records.size());
	}
}
