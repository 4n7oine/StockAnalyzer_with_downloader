package stockanalyzer.ctrl;
import stockanalyzer.ctrl.downloader.*;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import yahooApi.YahooFinance;
import yahooApi.YahooFinanceException;
import yahooApi.beans.QuoteResponse;
import yahooApi.beans.Result;
import yahooApi.beans.YahooResponse;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.util.logging.LogManager;

//*********************************************
//
//https://github.com/4n7oine/StockAnalyzer-4n7oine
//
//*********************************************

public class Controller {

	public void process(String ticker) throws YahooFinanceException{
			//Loggingausgabe jedes einzelnen Schritts durch YAHOOFINANCE API vermeiden
			Configurator.setRootLevel(Level.OFF);
			//CookieBug masssive errorloggingausgabe verstecken
			//Java API hat Cookie Bug durch Cookierichtlinie in Europa
			LogManager.getLogManager().reset();
			System.out.println("Start process");
			//TODO implement Error handling

			//TODO implement methods for
			//1) Daten laden
			//2) Daten Analyse
			switch (ticker) {
				case "APPL":
					System.out.println("Apple");
					//Alte Methode vom Lektor
					try {
						System.out.println(getResultData("AAPL").getAsk());
						//Methode mit YahooAPI
						Stock stock = getData("AAPL");
						stock.print();
						System.out.println("+++general AVERAGE without param++++");
						System.out.println(getAverageOfStock(stock));

						System.out.println("+++general AVERAGE weekly++++");
						System.out.println(getAverageOfStock52WeeksWeeklyFromNow(stock));

						System.out.println("+++general AVERAGE daily++++");
						System.out.println(getAverageOfStock52WeeksDailyFromNow(stock));
						//stock.getHistory();
					} catch (YahooFinanceException e) {
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}

					break;
				case "TWTR":
					try {
						System.out.println("Twitter");
						Stock stock = getData("TWTR");
						stock.print();
					} catch (YahooFinanceException e) {
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}
					break;
				case "TSLA":
					try {
						System.out.println("Tesla");
						Stock stock = getData("TSLA");
						stock.print();
						//throw new YahooFinanceException("TEST ERROR in reguar run");
					} catch (YahooFinanceException e) {
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}
					break;

				case "SequentialDownloader":
					try {
						System.out.println("++++ SequentialDownloader Start +++++");
						List <String> tickers = Arrays.asList("TSLA","TWTR", "APPL","BMW.DE", "1COV.DE", "BAS.DE", "FRE.DE", "HEI.DE", "MTX.DE", "VOW3.DE", "DB1.DE", "HNR1.DE", "ADS.DE", "AIR.DE");
						SequentialDownloader sequentialDownloader = new SequentialDownloader();
						long start = System.currentTimeMillis();
						String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
						System.out.println(timeStamp);
						sequentialDownloader.process(tickers);
						long end = System.currentTimeMillis();
						System.out.println("Miliseconds:" + (end - start));
						timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
						System.out.println(timeStamp);
						System.out.println("++++ SequentialDownloader Fertig ++++");
						//throw new YahooFinanceException("TEST ERROR in reguar run");
					}catch (Exception e){
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}
					break;

				case "ParallelDownloader":
					try {
						System.out.println("++++ ParallelDownloader Start +++++");
						List <String> tickers = Arrays.asList("TSLA","TWTR", "APPL","BMW.DE", "1COV.DE", "BAS.DE", "FRE.DE", "HEI.DE", "MTX.DE", "VOW3.DE", "DB1.DE", "HNR1.DE", "ADS.DE", "AIR.DE");
						ParallelDownloader paralleldownloader = new ParallelDownloader();
						long start = System.currentTimeMillis();
						String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
						System.out.println(timeStamp);
						paralleldownloader.process(tickers);
						long end = System.currentTimeMillis();
						System.out.println("Miliseconds:" + (end - start));
						timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
						System.out.println(timeStamp);
						System.out.println("++++ ParallelDownloader Fertig ++++");
					} catch (Exception e) {
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}
					break;

				case "Error":
					throw new YahooFinanceException("DEMO ERROR MESSAGE FROM CONTROLLER");

				default:
					try {
						System.out.println("Personal Stock");
						Stock stock = getData(ticker);
						stock.print();
					} catch (YahooFinanceException e) {
						//System.out.println(e.getMessage());
						throw new YahooFinanceException(e.getMessage());
					}
					break;
			}
	}
	

	public Result getResultData(String searchString) throws YahooFinanceException{

		yahooApi.YahooFinance yahooFinance = new YahooFinance();
			List<String> tickers = Arrays.asList(searchString);
			YahooResponse response = yahooFinance.getCurrentData(tickers);
			QuoteResponse quotes = response.getQuoteResponse();
			return quotes.getResult().get(0);

	}

	public Stock getData(String searchString) throws YahooFinanceException {
		try {
			Stock stock = yahoofinance.YahooFinance.get(searchString);
			if (stock == null) {
				throw new YahooFinanceException(" ++++  ++++  Stock not found ++++  ++++  ");
			}
			return stock;
		}catch(IOException e){
			System.out.println(e.toString());
			throw new YahooFinanceException(" ++++  ++++  Couldn't get Data ++++  ++++  ");
		}
	}


	public void closeConnection() {
		
	}

	public double getAverageOfStock(Stock stock) throws YahooFinanceException{
		double average = 0.0;

			try{
				average = stock.getHistory().stream()
						.mapToDouble(quote -> quote.getClose().doubleValue())
						.average()
						.orElse(0.0);
			}catch(IOException e){
				System.out.println(e.toString());
				throw new YahooFinanceException("ERROR while caluting the average");
			}
			return average;
	}

	public double getAverageOfStock52WeeksWeeklyFromNow(Stock stock) throws YahooFinanceException{
		double average = 0.0;
		try{
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.YEAR, -1);

			average = stock.getHistory(from, to, Interval.WEEKLY).stream()
					.mapToDouble(quote -> quote.getClose().doubleValue())
					.average()
					.orElse(0.0);
		}catch(IOException e){
			System.out.println(e.toString());
			throw new YahooFinanceException("ERROR while caluting the average");
		}
		return average;
	}

	public double getAverageOfStock52WeeksDailyFromNow(Stock stock) throws YahooFinanceException{
		double average = 0.0;
		try{
			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();
			from.add(Calendar.YEAR, -1);

			average = stock.getHistory(from, to, Interval.DAILY).stream()
					.mapToDouble(quote -> quote.getClose().doubleValue())
					.average()
					.orElse(0.0);
		}catch(IOException e){
			System.out.println(e.toString());
			throw new YahooFinanceException("ERROR while caluting the average");
		}
		return average;
	}

	public long getDatasetSize(Stock stock) throws YahooFinanceException{
		long size = 0;
		try{
			size = stock.getHistory().stream()
					.count();
		}catch(IOException e){
			System.out.println(e.toString());
			throw new YahooFinanceException(e.getMessage());
		}

		return size;
	}

	public Result getHighestPrice(List<Result> results) throws YahooFinanceException{
		try {
			double highestPrice = results.stream()
					.mapToDouble(h -> h.getAsk().doubleValue()).max().orElse(-1);

			return results.stream().filter(result -> result.getAsk().doubleValue() == highestPrice).findAny()
					.orElse(null);
		}catch (Exception e){
			throw new YahooFinanceException(e.getMessage());
		}
	}
}
