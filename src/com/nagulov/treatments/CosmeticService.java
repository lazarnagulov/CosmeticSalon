package com.nagulov.treatments;

import java.util.ArrayList;
import java.util.List;

import com.nagulov.controllers.ServiceController;
import com.nagulov.users.User;

public class CosmeticService {
	
	private String name;
	private List<CosmeticTreatment> treatments = new ArrayList<CosmeticTreatment>();
	
	public CosmeticService() {
		
	}
	
	public CosmeticService(String name){
		this.name = name;
	}
	
	public void addTreatment(CosmeticTreatment treatment) {
		ServiceController.getInstance().getCosmeticTreatments().put(treatment, this);
		treatments.add(treatment);
	}
	
	public void removeTreatment(CosmeticTreatment treatment) {
		ServiceController.getInstance().getCosmeticTreatments().remove(treatment);
		treatments.remove(treatment);
	}
	
	public void removeTreatment(String treatment) {
		ServiceController.getInstance().getCosmeticTreatments().remove(getTreatment(treatment));
		treatments.remove(getTreatment(treatment));
	}
	
	public List<CosmeticTreatment> getTreatments() {
		return treatments;
	}
	
	public void setTreatments(List<CosmeticTreatment> treatments) {
		this.treatments = treatments; 
	}
	
	public CosmeticTreatment getTreatment(String treatment) {
		for(CosmeticTreatment ct : treatments) {
			if(ct.getName().equals(treatment)) {
				return ct;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this) {
			return true;
		}
		if(!(other instanceof User)) {
			return false;
		}
		if(this.getName().equals(((CosmeticService) other).getName())) {
			return true;
		}
		return false;
	}	
}
