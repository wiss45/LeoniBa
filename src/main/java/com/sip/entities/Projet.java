package com.sip.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sip.enums.DrawingStatus;


import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Projet {
	    
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
	    private String customer;
	    private String deravative;
	    private double maxQuantite;

	    @Temporal(TemporalType.DATE)
	    private Date a_samples;
	    @Temporal(TemporalType.DATE)
	    private Date b_samples;
	    @Temporal(TemporalType.DATE)
	    private Date c_samples;
	    @Temporal(TemporalType.DATE)
	    private Date d_samples;
	    @Temporal(TemporalType.DATE)
	    private Date sop;
	    @Temporal(TemporalType.DATE)
	    private Date sop_1;

	    private String responsable;

	    @Enumerated(EnumType.STRING)
	    private DrawingStatus status;

	    private double sommePrevisionnel;
	    private double sommeReel;        
	    
	    @JsonIgnore
	    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.EAGER)
	    private List<Equipement> equipements;
        
	    @JsonIgnore
	    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
	    private List<Plan> plans;
	    
	    public Projet() {}

	    public Projet(String name, String customer, String deravative, double maxQuantite, Date a_samples, Date b_samples,
                Date c_samples, Date d_samples, Date sop, Date sop_1, String responsable,
                DrawingStatus status, double sommePrevisionnel, double sommeReel) {
      this.name = name;
      this.customer = customer;
      this.deravative = deravative;
      this.maxQuantite = maxQuantite;
      this.a_samples = a_samples;
      this.b_samples = b_samples;
      this.c_samples = c_samples;
      this.d_samples = d_samples;
      this.sop = sop;
      this.sop_1 = sop_1;
      this.responsable = responsable;
      this.status = status;
      this.sommePrevisionnel = sommePrevisionnel;
      this.sommeReel = sommeReel;
  }
	    
	    
	    
	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public String getCustomer() { return customer; }
	    public void setCustomer(String customer) { this.customer = customer; }

	    public String getDeravative() { return deravative; }
	    public void setDeravative(String deravative) { this.deravative = deravative; }

	    public double getMaxQuantite() { return maxQuantite; }
	    public void setMaxQuantite(double maxQuantite) { this.maxQuantite = maxQuantite; }

	    public Date getA_samples() { return a_samples; }
	    public void setA_samples(Date a_samples) { this.a_samples = a_samples; }

	    public Date getB_samples() { return b_samples; }
	    public void setB_samples(Date b_samples) { this.b_samples = b_samples; }

	    public Date getC_samples() { return c_samples; }
	    public void setC_samples(Date c_samples) { this.c_samples = c_samples; }

	    public Date getD_samples() { return d_samples; }
	    public void setD_samples(Date d_samples) { this.d_samples = d_samples; }

	    public Date getSop() { return sop; }
	    public void setSop(Date sop) { this.sop = sop; }

	    public Date getSop_1() { return sop_1; }
	    public void setSop_1(Date sop_1) { this.sop_1 = sop_1; }

	    public String getResponsable() { return responsable; }
	    public void setResponsable(String responsable) { this.responsable = responsable; }

	    public DrawingStatus getStatus() { return status; }
	    public void setStatus(DrawingStatus status) { this.status = status; }

	    public double getSommePrevisionnel() { return sommePrevisionnel; }
	    public void setSommePrevisionnel(double sommePrevisionnel) { this.sommePrevisionnel = sommePrevisionnel; }

	    public double getSommeReel() { return sommeReel; }
	    public void setSommeReel(double sommeReel) { this.sommeReel = sommeReel; }

	    public List<Equipement> getEquipements() {
	        return equipements;
	    }

	    public void setEquipements(List<Equipement> equipements) {
	        this.equipements = equipements;
	    }

		public List<Plan> getPlans() {
			return plans;
		}

		public void setPlans(List<Plan> plans) {
			this.plans = plans;
		}

		

	
	    
	    
}
