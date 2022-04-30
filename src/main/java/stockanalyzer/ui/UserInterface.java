package stockanalyzer.ui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import stockanalyzer.ctrl.Controller;
import yahooApi.YahooFinanceException;

public class UserInterface 
{

	private Controller ctrl = new Controller();

	public void getDataFromCtrl1(){
		try{
			ctrl.process("APPL");
		}catch(YahooFinanceException ex){
			System.out.println(ex.getMessage());
		}
	}

	public void getDataFromCtrl2(){
		try{
			ctrl.process("TWTR");
		}catch(YahooFinanceException ex){
			System.out.println(ex.getMessage());
		}
	}

	public void getDataFromCtrl3(){
		try{
			ctrl.process("TSLA");
		}catch(YahooFinanceException ex){
			System.out.println(ex.getMessage());
		}
	}
	public void getDataFromCtrl4(){
		try{
			ctrl.process("Error");
		}catch(YahooFinanceException ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public void getDataForCustomInput() {
		System.out.println("Enter your Stockticker");
		try{
			ctrl.process(readLine());
		}catch(YahooFinanceException ex){
			System.out.println(ex.getMessage());
		}
	}


	public void start() {
		Menu<Runnable> menu = new Menu<>("User Interfacx");
		menu.setTitel("Wählen Sie aus:");
		menu.insert("a", "Apple", this::getDataFromCtrl1);
		menu.insert("b", "Twitter", this::getDataFromCtrl2);
		menu.insert("c", "Tesla", this::getDataFromCtrl3);
		menu.insert("d", "Choice User Input:",this::getDataForCustomInput);
		menu.insert("z", "Error demo:",this::getDataFromCtrl4);
		menu.insert("q", "Quit", null);
		Runnable choice;
		while ((choice = menu.exec()) != null) {
			 choice.run();
		}
		ctrl.closeConnection();
		System.out.println("Program finished");
	}


	protected String readLine() 
	{
		String value = "\0";
		BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
		try {
			value = inReader.readLine();
		} catch (IOException e) {
		}
		return value.trim();
	}

	protected Double readDouble(int lowerlimit, int upperlimit) 
	{
		Double number = null;
		while(number == null) {
			String str = this.readLine();
			try {
				number = Double.parseDouble(str);
			}catch(NumberFormatException e) {
				number=null;
				System.out.println("Please enter a valid number:");
				continue;
			}
			if(number<lowerlimit) {
				System.out.println("Please enter a higher number:");
				number=null;
			}else if(number>upperlimit) {
				System.out.println("Please enter a lower number:");
				number=null;
			}
		}
		return number;
	}
}
