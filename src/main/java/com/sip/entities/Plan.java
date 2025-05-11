package com.sip.entities;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sip.enums.StatutEquipement;

import jakarta.persistence.*;

@Entity
public class Plan {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private int orderNumber;
	    private double orderPrice;
	    private String pamNumber;
	    private int quantite;
  
	    @Temporal(TemporalType.DATE)
	    private Date targetDate;
	    @Temporal(TemporalType.DATE)
	    private Date deliveryDate;
	    @Temporal(TemporalType.DATE)
	    private Date rprdate;

	    @JsonIgnore
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "projet_id")
	    private Projet projet;

	    @JsonIgnore
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "equipement_id")
	    private Equipement equipement;

	    @Enumerated(EnumType.STRING)
	    private StatutEquipement statut;
	    
	    
	    public Plan() {}

	    public Plan(Projet projet, Equipement equipement, int orderNumber, double orderPrice,
	                String pamNumber, int quantite, Date targetDate, Date deliveryDate, Date rprdate) {
	        this.projet = projet;
	        this.equipement = equipement;
	        this.orderNumber = orderNumber;
	        this.orderPrice = orderPrice;
	        this.pamNumber = pamNumber;
	        this.quantite = quantite;
	        this.targetDate = targetDate;
	        this.deliveryDate = deliveryDate;
	        this.rprdate = rprdate;
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


	public Equipement getEquipement() {
		return equipement;
	}


	public void setEquipement(Equipement equipement) {
		this.equipement = equipement;
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

	public StatutEquipement getStatut() {
		return statut;
	}

	public void setStatut(StatutEquipement statut) {
		this.statut = statut;
	}
	
	
	

}
