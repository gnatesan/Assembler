import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class GNAssembler {

	ArrayList<String> instructions;
	ArrayList<String> binary;
	HashMap<String, Integer> values;

	public GNAssembler() {
		values = new HashMap<String, Integer>();
		values.put("SP", 0);
		values.put("LCL", 1);
		values.put("ARG", 2);
		values.put("THIS", 3);
		values.put("THAT", 4);
		values.put("R0", 0);
		values.put("R1", 1);
		values.put("R2", 2);
		values.put("R3", 3);
		values.put("R4", 4);
		values.put("R5", 5);
		values.put("R6", 6);
		values.put("R7", 7);
		values.put("R8", 8);
		values.put("R9", 9);
		values.put("R10", 10);
		values.put("R11", 11);
		values.put("R12", 12);
		values.put("R13", 13);
		values.put("R14", 14);
		values.put("R15", 15);
		values.put("SCREEN", 16384);
		values.put("KBD", 24576);	
	}

	public void readFile(String fileName){
		int instructionCount = 0;
		instructions = new ArrayList<String>();
		Scanner in;
		File name = new File(fileName);
		try {
			in = new Scanner(name);
			in.nextLine();
			while(in.hasNextLine()){
				String currentInstruction = in.nextLine();
				CharSequence slash = "//";
				if (currentInstruction.contains(slash)) {
					currentInstruction = currentInstruction.substring(0, currentInstruction.indexOf('/'));
				}
				if (currentInstruction.trim().length() != 0 && currentInstruction.charAt(0) != '(') {
					instructions.add(currentInstruction);
					instructionCount++;
				}
				else if (currentInstruction.trim().length() != 0 && currentInstruction.charAt(0) == '(') {
				values.put(currentInstruction.substring(1, currentInstruction.length()-1), instructionCount);
				}
			}
		}
		 catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	public void writeFile(String fileName) {
		FileWriter wr;
		try {
			wr =  new FileWriter(fileName);
			for (String b : binary){
				wr.write(b);
				wr.write(System.lineSeparator());
			}
			wr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void toBinary(){
		binary = new ArrayList<String>();
		int variableCounter = 16;
		for (String position : instructions){
			position = position.trim();
			if (position.charAt(0) == '@'){ 
				if (values.containsKey(position.substring(1, position.length()))){
					binary.add(convertToBinary("@" + Integer.toString(values.get(position.substring(1, position.length())))));
				}
				else  { 
					try {
						Integer.parseInt(position.substring(1, position.length()));
						binary.add(convertToBinary(position));
				}
				catch (NumberFormatException e){
					String test = "@" + Integer.toString(variableCounter);
					binary.add(convertToBinary(test));
					values.put(position.substring(1, position.length()), variableCounter);
					variableCounter++;
				}
			   }
			}
			else{
				binary.add(CInstruction(position));
			}
			
			
		}	
	}

	public String CInstruction(String position){
		String binary = "111";
		String dest = "";
		String jump = "";
		String comp = "";
		String store = "";
		String op1 = "";
		String op2 = "";
		String jumpString = "";
		
		int i = 0;

		while (position.charAt(i) != '=' && position.charAt(i) != ';' && i < position.length()-1){
			store += position.charAt(i);
			i++;
		}
		
		op1 += position.charAt(i);
		i++;

		if( op1.equals("=")){
		while (i < position.length()){
			op2 += position.charAt(i);
			i++;
		}
		}
		else if (op1.equals(";")){
			op2 = store;
			while (i < position.length()) {
				jumpString += position.charAt(i);
				i++;
			}
		}
		

		switch(op2){
		case "0": comp = "0101010";
		break;
		case "1": comp = "0111111";
		break;
		case "-1": comp = "0111010";
		break;
		case "D": comp = "0001100";
		break;
		case "A": comp = "0110000";
		break;
		case "!D": comp = "0001101";
		break;
		case "!A": comp = "0110001";
		break;
		case "-D": comp = "0001111";
		break;
		case "-A": comp = "0110011";
		break;
		case "D+1": comp = "0011111";
		break;
		case "A+1": comp = "0110111";
		break;
		case "D-1": comp = "0001110";
		break;
		case "A-1": comp = "0110010";
		break;
		case "D+A": comp = "0000010";
		break;
		case "D-A": comp = "0010011";
		break;
		case "A-D": comp = "0000111";
		break;
		case "D&A": comp = "0000000";
		break;
		case "D|A": comp = "0010101";
		break;
		case "M": comp = "1110000";
		break;
		case "!M": comp = "1110001";
		break;
		case "M+1": comp = "1110111";
		break;
		case "D+M": comp = "1000010";
		break;
		case "D-M": comp = "1010011";
		break;
		case "M-D": comp = "1000111";
		break;
		case "D&M": comp = "1000000";
		break;
		case "D|M": comp = "1010101";
		break;
		case "M-1": comp = "1110010";
		break;
		}

		if(op1.equals(";")){
			dest = "000";
		}
		else if(op1.equals("=")){

			switch(store){
			case "null": dest = "000";
			break;
			case "M": dest = "001";
			break;
			case "D": dest = "010";
			break;
			case "MD": dest = "011";
			break;
			case "A": dest = "100";
			break;
			case "AM": dest = "101";
			break;
			case "AD": dest = "110";
			break;
			case "AMD": dest = "111";
			break;
			}      
		}

		if(op1.equals("=")){
			jump = "000";
		}
		else{
			switch(jumpString){
			case "null": jump = "000";
			break;
			case "JGT": jump = "001";
			break;
			case "JEQ": jump = "010";
			break;
			case "JGE": jump = "011";
			break;
			case "JLT": jump = "100";
			break;
			case "JNE": jump = "101";
			break;
			case "JLE": jump = "110";
			break;
			case "JMP": jump = "111";
			break;
			}     	
		}
		binary += comp;
		binary += dest;
		binary += jump;
		return binary;
	}
	
	public String convertToBinary(String x) {
		String answer = Integer.toBinaryString(Integer.parseInt(x.substring(1, x.length())));
		while(answer.length() != 16) {
			answer = "0" + answer;
		}
		return answer;
	}
	
	public String parseOutputFile(String input) {
		int index = input.indexOf('.');
		return (input.substring(0, index) + ".hack");
	}
	public static void main (String[] args) {
		GNAssembler run = new GNAssembler();
		run.readFile(args[0]);
		run.toBinary();
		run.writeFile(run.parseOutputFile(args[0]));
		
		
		
	}
}
