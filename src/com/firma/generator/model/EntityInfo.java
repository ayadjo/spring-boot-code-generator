package com.firma.generator.model;

import java.util.ArrayList;
import java.util.List;

public class EntityInfo {
    public String name;
    public String tableName;
    public List<AttributeInfo> attributes = new ArrayList<>();
    public List<AssociationInfo> associations = new ArrayList<>();
    public boolean generateRepository = true;
    public boolean generateController = true;
    public boolean generateService = true;
    public boolean generateDTO = true;
	public boolean softDelete;
    public boolean auditable;
    public String apiPath;
    
    public boolean isSoftDelete() {
		return softDelete;
	}
    
	public void setSoftDelete(boolean softDelete) {
		this.softDelete = softDelete;
	}
	public boolean isAuditable() {
		return auditable;
	}
	
	public void setAuditable(boolean auditable) {
		this.auditable = auditable;
	}
	public String getApiPath() {
		return apiPath;
	}
	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public List<AttributeInfo> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeInfo> attributes) {
		this.attributes = attributes;
	}
	public List<AssociationInfo> getAssociations() {
		return associations;
	}
	public void setAssociations(List<AssociationInfo> associations) {
		this.associations = associations;
	}
	public boolean isGenerateRepository() {
		return generateRepository;
	}
	public void setGenerateRepository(boolean generateRepository) {
		this.generateRepository = generateRepository;
	}
	public boolean isGenerateController() {
		return generateController;
	}
	public void setGenerateController(boolean generateController) {
		this.generateController = generateController;
	}
	public boolean isGenerateService() {
		return generateService;
	}
	public void setGenerateService(boolean generateService) {
		this.generateService = generateService;
	}
	public boolean isGenerateDTO() {
		return generateDTO;
	}
	public void setGenerateDTO(boolean generateDTO) {
		this.generateDTO = generateDTO;
	}
    
    
}