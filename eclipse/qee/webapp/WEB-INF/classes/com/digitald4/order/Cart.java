package com.digitald4.order;
import java.util.Vector;
import java.text.*;
public class Cart{
	private int orderID=0;
	private Vector items = new Vector();
	private Address shipAddress = new Address();
	private Address billAddress = new Address();
	private ShipMethod shipMethod = new ShipMethod();
	private boolean sameAddress=true;
	private CreditCard card = new CreditCard();
	private boolean rememberMe=false;
	private int cartIndex=0;
	private double taxRate=0;
	public Cart(){
	}
	public void setOrderID(int orderID){
		this.orderID=orderID;
	}
	public int getOrderID(){
		return orderID;
	}
	public ShipMethod getShipMethod(){
		return shipMethod;
	}
	public Item getItem(int index){
		if(index >= 0 && index < items.size())
			return (Item)items.get(index);
		return null;
	}
	public Item getItemByIndex(int cartIndex){
		for(int i=0; i<items.size(); i++){
			if(getItem(i).getCartIndex() == cartIndex)
				return getItem(i);
		}
		return null;
	}
	public Item getItem(String itemNo){
		return getItem(new Item(itemNo));
	}
	public Item getItem(Item item){
		for(int i=0; i<items.size(); i++){
			if(items.get(i).equals(item))
				return (Item)items.get(i);
		}
		return null;
	}
	public void add(Item item){
		for(int i=0; i<items.size(); i++){
			if(items.get(i).equals(item)){
				Item it = (Item)items.get(i);
				it.setQty(it.getQty()+item.getQty());
				return;
			}
		}
		item.setCartIndex(cartIndex++);
		items.add(item);
	}
	public boolean remove(int cartIndex){
		for(int i=0; i<items.size(); i++){
			if(getItem(i).getCartIndex() == cartIndex){
				items.remove(i);
				return true;
			}
		}
		return false;
	}
	public void empty(){
		orderID=0;
		items.clear();
	}
	public boolean isEmpty(){
		return items.isEmpty();
	}
	public int getItemCount(){
		return items.size();
	}
	public int getTotalItemCount(){
		int total=0;
		for(int x=0; x<getItemCount(); x++)
			total+=getItem(x).getQty();
		return total;
	}
	public Address getShipAddress(){
		return shipAddress;
	}
	public Address getBillAddress(){
		return billAddress;
	}
	public void setSameAddress(boolean sameAddress){
		this.sameAddress = sameAddress;
	}
	public boolean isSameAddress(){
		return sameAddress;
	}
	public CreditCard getCreditCard(){
		return card;
	}	
	public void setTaxRate(double taxRate){
		this.taxRate=taxRate;
	}
	public double getTaxRate(){
		return taxRate;
	}
	public double getSubTotal(){
		double subTotal=0;
		for(int x=0; x<getItemCount(); x++)
			subTotal+=getItem(x).getPrice()*getItem(x).getQty();
		return subTotal;
	}
	public double getTotalTax(){
		double tax=0;
		for(int x=0; x<getItemCount(); x++){
			if(getItem(x).isTaxable())
				tax+=getItem(x).getPrice()*getItem(x).getQty()*getTaxRate()/100;
		}
		return tax;
	}
	public double getGrandTotal(){
		double subTotal=0;
		for(int x=0; x<getItemCount(); x++){
			double cost = getItem(x).getPrice()*getItem(x).getQty();
			if(getItem(x).isTaxable())
				cost = cost*(1+getTaxRate()/100);
			subTotal+=cost;
		}
		return subTotal+getShippingCost();
	}
	public int getShipCount(){
		int shipCount=0;
		for(int x=0; x<getItemCount(); x++){
			Item item = getItem(x);
			if(item.requiresShipping())
				shipCount+=item.getQty();
		}
		return shipCount;
	}
	public double getShippingCost(){
		return shipMethod.getQuote(this);
	}
	public boolean shouldRemember(){
		return rememberMe;
	}
	public void setRememberMe(boolean rememberMe){
		this.rememberMe = rememberMe;
	}	
}