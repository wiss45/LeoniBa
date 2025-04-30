package com.sip.responses;

import java.util.Date;
import java.util.List;

import com.sip.entities.Equipement;
import com.sip.entities.Projet;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;

public class PlanResponse {
	
    private Long id;
	private Projet projet;
	private List<Equipement> equipements;
    private int orderNumber ;
	private double orderPrice ;
	private String pamNumber ;
	private int quantite ;
	private Date targetDate ;
	private Date deliveryDate ;
	private Date rprdate ;
	
	
	public PlanResponse(Projet projet, List<Equipement> equipements, int orderNumber, double orderPrice, String pamNumber,
			int quantite, Date targetDate, Date deliveryDate, Date rprdate) {
		
		this.projet = projet;
		this.equipements = equipements;
		this.orderNumber = orderNumber;
		this.orderPrice = orderPrice;
		this.pamNumber = pamNumber;
		this.quantite = quantite;
		this.targetDate = targetDate;
		this.deliveryDate = deliveryDate;
		this.rprdate = rprdate;
	}


	public PlanResponse() {
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Projet getProjet() {
		return projet;
	}


	public void setProjet(Projet projet) {
		this.projet = projet;
	}


	public List<Equipement> getEquipements() {
		return equipements;
	}


	public void setEquipements(List<Equipement> equipements) {
		this.equipements = equipements;
	}


	public int getOrderNumber() {
		return orderNumber;
	}


	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}


	public double getOrderPrice() {
		return orderPrice;
	}


	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}


	public String getPamNumber() {
		return pamNumber;
	}


	public void setPamNumber(String pamNumber) {
		this.pamNumber = pamNumber;
	}


	public int getQuantite() {
		return quantite;
	}


	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}


	public Date getTargetDate() {
		return targetDate;
	}


	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}


	public Date getDeliveryDate() {
		return deliveryDate;
	}


	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}


	public Date getRprdate() {
		return rprdate;
	}


	public void setRprdate(Date rprdate) {
		this.rprdate = rprdate;
	}
	
	
}
