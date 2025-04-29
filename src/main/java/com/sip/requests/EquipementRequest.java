package com.sip.requests;

import com.sip.entities.Projet;


import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class EquipementRequest {
	 

	    private String name;
	    private double firstUnitPrice;
	    private double secondUnitPrice;
	    private double thirdUnitPrice;
	    private int leadTime;
	    private int transportationTime;
	    private int installationTime;
	    private String supplier;
	    private double price;
	    private String capexType;

	    
	    @ManyToOne
	    @JoinColumn(name = "projet_id")
	    private Projet projet;

	    
	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public double getFirstUnitPrice() {
	        return firstUnitPrice;
	    }

	    public void setFirstUnitPrice(double firstUnitPrice) {
	        this.firstUnitPrice = firstUnitPrice;
	    }

	    public double getSecondUnitPrice() {
	        return secondUnitPrice;
	    }

	    public void setSecondUnitPrice(double secondUnitPrice) {
	        this.secondUnitPrice = secondUnitPrice;
	    }

	    public double getThirdUnitPrice() {
	        return thirdUnitPrice;
	    }

	    public void setThirdUnitPrice(double thirdUnitPrice) {
	        this.thirdUnitPrice = thirdUnitPrice;
	    }

	    public int getLeadTime() {
	        return leadTime;
	    }

	    public void setLeadTime(int leadTime) {
	        this.leadTime = leadTime;
	    }

	    public int getTransportationTime() {
	        return transportationTime;
	    }

	    public void setTransportationTime(int transportationTime) {
	        this.transportationTime = transportationTime;
	    }

	    public int getInstallationTime() {
	        return installationTime;
	    }

	    public void setInstallationTime(int installationTime) {
	        this.installationTime = installationTime;
	    }

	    public String getSupplier() {
	        return supplier;
	    }

	    public void setSupplier(String supplier) {
	        this.supplier = supplier;
	    }

	    public double getPrice() {
	        return price;
	    }

	    public void setPrice(double price) {
	        this.price = price;
	    }

	    public String getCapexType() {
	        return capexType;
	    }

	    public void setCapexType(String capexType) {
	        this.capexType = capexType;
	    }

	    public Projet getProjet() {
	        return projet;
	    }

	    public void setProjet(Projet projet) {
	        this.projet = projet;
	    }
	    
		public EquipementRequest(String name, double firstUnitPrice, double secondUnitPrice, double thirdUnitPrice, int leadTime,
				int transportationTime, int installationTime, String supplier, double price, String capexType,
				Projet projet) {
			
			this.name = name;
			this.firstUnitPrice = firstUnitPrice;
			this.secondUnitPrice = secondUnitPrice;
			this.thirdUnitPrice = thirdUnitPrice;
			this.leadTime = leadTime;
			this.transportationTime = transportationTime;
			this.installationTime = installationTime;
			this.supplier = supplier;
			this.price = price;
			this.capexType = capexType;
			this.projet = projet;
		}

		public EquipementRequest() {
		}
	}