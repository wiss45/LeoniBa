package com.sip.entities;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Plan {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	
	@JsonIgnore
    @OneToOne
	@JoinColumn(name = "projet_id") 
	private Projet projet;
    
    
    @JsonIgnore
	@ManyToMany
	@JoinTable(
	name = "plan_equipements",
	joinColumns = @JoinColumn(name = "plan_id"),
	inverseJoinColumns = @JoinColumn(name = "equipement_id")
	)
	private List<Equipement> equipements;
	
	
	private int orderNumber ;
	private double orderPrice ;
	private String pamNumber ;
	private int quantite ;
	private Date targetDate ;
	private Date deliveryDate ;
	private Date rprdate ;
	
	
	public Plan(Projet projet, List<Equipement> equipements, int orderNumber, double orderPrice, String pamNumber,
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


	public Plan() {
		
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
