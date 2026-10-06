package com.firma.generator.model;

public class AssociationInfo {
	public String roleName;      
    public String targetType;    
    public String multiplicity;  
    public boolean isComposition;
    private String mappedBy;
    
	public String getMappedBy() {
		return mappedBy;
	}
	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public String getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}
	public boolean isComposition() {
		return isComposition;
	}
	public void setComposition(boolean isComposition) {
		this.isComposition = isComposition;
	}
    
    
}
