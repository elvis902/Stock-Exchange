package com.geektrust;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Geektrust {
	
	static private class Order{
		String id, stock, type;
		int time, qty;
		float price;
		Order(String id, int time, String stock, String type, float price, int qty){
			this.id = id;
			this.stock = stock;
			this.type = type;
			this.time = time;
			this.qty = qty;
			this.price = price;
		}
	}
	
	static private class MatchedOrder{
		String buy_id, sell_id;
		int qty;
		float sell_price;
		MatchedOrder(String buy_id, String sell_id, int qty, float sell_price){
			this.buy_id = buy_id;
			this.sell_id = sell_id;
			this.qty = qty;
			this.sell_price = sell_price;
		}
	}
	
	static class BuyOrderComparator implements Comparator<Geektrust.Order>{

		@Override
		public int compare(Order o1, Order o2) {
			if(o1.price == o2.price) {
				return o1.time > o2.time ? 1 : -1;
			}
			return o1.price < o2.price ? 1 : -1;
		}
		
	}
	
	static class SellOrderComparator implements Comparator<Geektrust.Order>{

		@Override
		public int compare(Order o1, Order o2) {
			if(o1.price == o2.price) {
				return o1.time > o2.time ? 1 : -1;
			}
			return o1.price > o2.price ? 1 : -1;
		}
		
	}
	
	

	public static void main(String[] args) {
		try {
			 if (args.length < 1) {
	                System.out.println("No input file");
	                return;
	            }
			 
			 ArrayList<Geektrust.Order> orders = new ArrayList<>();
			 File file = new File(args[0]);
	         BufferedReader br = new BufferedReader(new FileReader(file));
			 String line = br.readLine();

		    while (line != null) {
		    	String[] inputLineStrArr = line.split(" ");
		        line = br.readLine();
		       
		        String id = inputLineStrArr[0];
		        String[] timeStringArr = inputLineStrArr[1].split(":");
		        int time = Integer.parseInt(timeStringArr[0])*60 + Integer.parseInt(timeStringArr[1]);
		        String stock = inputLineStrArr[2];
		        String type = inputLineStrArr[3];
		        float price = Float.parseFloat(inputLineStrArr[4]);
		        int qty = Integer.parseInt(inputLineStrArr[5]);
		        
		        Geektrust.Order newOrder = new Geektrust.Order(id, time, stock, type, price, qty);
		        orders.add(newOrder);
		    }
		    br.close();

		    PriorityQueue<Geektrust.Order> pqBuy = new 
		             PriorityQueue<Geektrust.Order>(new Geektrust.BuyOrderComparator());
		    
		    PriorityQueue<Geektrust.Order> pqSell = new 
		             PriorityQueue<Geektrust.Order>(new Geektrust.SellOrderComparator());
	    
		    ArrayList<Geektrust.MatchedOrder> matchedOrders = new ArrayList<>();
		   
		    
		    for(int i = 0; i < orders.size(); i++) {
		    	String type = orders.get(i).type;
		    	
		    	if(type.equals("buy")) {
		    		pqBuy.add(orders.get(i));
		    	}else if(type.equals("sell")) {
		    		pqSell.add(orders.get(i));
		    	}
		    	matchOrdersFun(pqBuy, pqSell, matchedOrders);
		    }
		    
		    for(int j = 0; j < matchedOrders.size(); j++) {
	    		System.out.print(matchedOrders.get(j).buy_id + " ");
	    		System.out.print(matchedOrders.get(j).sell_price + " ");
	    		System.out.print(matchedOrders.get(j).qty + " ");
	    		System.out.print(matchedOrders.get(j).sell_id + "\n");
	    	}
			
		}catch(Exception e){
			System.out.println("Exception = " + e);
		}
	}


	private static void matchOrdersFun(PriorityQueue<Order> pqBuy, PriorityQueue<Order> pqSell,
			ArrayList<MatchedOrder> matchedOrders) {
		
		if(pqBuy.size() == 0 || pqSell.size() == 0) {
			return;
		}else {
			
			while(!pqBuy.isEmpty() && !pqSell.isEmpty() && pqBuy.peek().price >= pqSell.peek().price) {
				
				String buyId = pqBuy.peek().id;
				int buyTime = pqBuy.peek().time;
				String buyStock = pqBuy.peek().stock;
				String buyType = pqBuy.peek().type;
				float buyPrice = pqBuy.peek().price;
				int buyQty = pqBuy.peek().qty;
				
				String sellId = pqSell.peek().id;
				int sellTime = pqSell.peek().time;
				String sellStock = pqSell.peek().stock;
				String sellType = pqSell.peek().type;
				float sellPrice = pqSell.peek().price;
				int sellQty = pqSell.peek().qty;
								
				pqBuy.poll();
				pqSell.poll();
				
				if(buyQty > sellQty) {
					int updatedBuyQty = buyQty - sellQty;
					Geektrust.Order updatedBuyOrder = new Geektrust.Order(buyId, buyTime, buyStock, buyType, buyPrice, updatedBuyQty);
					pqBuy.add(updatedBuyOrder);
				}else if(buyQty < sellQty) {
					int updatedSellQty = sellQty - buyQty;
					Geektrust.Order updatedSellOrder = new Geektrust.Order(sellId, sellTime, sellStock, sellType, sellPrice, updatedSellQty);
					pqSell.add(updatedSellOrder);
					
				}
			
				Geektrust.MatchedOrder newMatchedOrder = new Geektrust.MatchedOrder(buyId, sellId, buyQty >= sellQty ? sellQty : buyQty, sellPrice);
				matchedOrders.add(newMatchedOrder);
			}
		
		}
		return;
	}

}
