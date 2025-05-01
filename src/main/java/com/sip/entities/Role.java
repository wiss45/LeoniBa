package com.sip.entities;

import java.util.List;

import com.sip.enums.ERole;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Role {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id ;
	
	@NotNull(message="nom est obligatoire")
	@Enumerated(EnumType.STRING)
	private ERole name ;

	@ManyToMany(mappedBy = "roles")
    private List<User> users ;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ERole getName() {
		return name;
	}

	public void setName(ERole name) {
		this.name = name;
	}

	public Role(long id,@NotNull(message = "nom est obligatoire") ERole name) {
		this.id = id;
		this.name = name;
	}

	public Role() {
		
	}
	
	
	
	

}
