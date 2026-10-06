package com.firma.generator;

import com.firma.generator.model.AssociationInfo;
import com.firma.generator.model.AttributeInfo;
import com.firma.generator.model.EntityInfo;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;

import java.util.ArrayList;
import java.util.List;

public class ModelExtractor {

    public List<EntityInfo> extract(org.eclipse.uml2.uml.Package domainPackage) {
        List<EntityInfo> entities = new ArrayList<>();
        for (PackageableElement element : domainPackage.getPackagedElements()) {
            if (element instanceof org.eclipse.uml2.uml.Class) {
                entities.add(extractEntity((org.eclipse.uml2.uml.Class) element));
            }
        }
        return entities;
    }

    public EntityInfo extractEntity(org.eclipse.uml2.uml.Class umlClass) {
        EntityInfo entity = new EntityInfo();
        entity.setName(umlClass.getName());

        // 1. Procitaj «Entity» stereotip i sve njegove tagove
        for (Stereotype st : umlClass.getAppliedStereotypes()) {
            if (st.getName().equals("Entity")) {
                Object tableName = umlClass.getValue(st, "tableName");
                entity.setTableName(tableName != null ? tableName.toString() : "");

                entity.setGenerateRepository(readBooleanTag(umlClass, st, "generateRepository", true));
                entity.setGenerateService(readBooleanTag(umlClass, st, "generateService", true));
                entity.setGenerateController(readBooleanTag(umlClass, st, "generateController", true));
                entity.setGenerateDTO(readBooleanTag(umlClass, st, "generateDTO", true));

                // Novi tagovi iz tvog profila:
                Object apiPath = umlClass.getValue(st, "apiPath");
                entity.setApiPath(apiPath != null ? apiPath.toString() : "");

                entity.setSoftDelete(readBooleanTag(umlClass, st, "softDelete", false));
                entity.setAuditable(readBooleanTag(umlClass, st, "auditable", false));
            }
        }

        // 2. Prodji kroz sve atribute klase - razdvoj prave atribute od asocijacija
        for (Property attr : umlClass.getOwnedAttributes()) {
            if (attr.getAssociation() != null) {
                entity.getAssociations().add(extractAssociation(attr));
            } else {
                entity.getAttributes().add(extractAttribute(attr));
            }
        }

        return entity;
    }

    private AttributeInfo extractAttribute(Property attr) {
        AttributeInfo ai = new AttributeInfo();
        ai.setName(attr.getName());
        ai.setType(getTypeName(attr));

        for (Stereotype st : attr.getAppliedStereotypes()) {
            switch (st.getName()) {
                case "Id":
                    ai.setId(true);
                    break;
                case "Required":
                    ai.setRequired(true);
                    break;
                case "Unique":
                    ai.setUnique(true);
                    break;
                case "Searchable":
                    ai.setSearchable(true);
                    break;
                case "ReadOnly":
                    ai.setReadOnly(true);
                    break;
                case "Persistent":
                    ai.setPersistent(true);
                    break;
            }
        }

        return ai;
    }

    private AssociationInfo extractAssociation(Property attr) {
        AssociationInfo assoc = new AssociationInfo();
        assoc.setRoleName(attr.getName());
        assoc.setTargetType(getTypeName(attr));
        assoc.setMultiplicity(attr.getUpper() == -1 ? "*" : String.valueOf(attr.getUpper()));
        assoc.setComposition(attr.getAggregation() == AggregationKind.COMPOSITE_LITERAL);

        // Pronadji ime polja na suprotnoj strani veze (potrebno za JPA mappedBy)
        Property otherEnd = attr.getOtherEnd();
        if (otherEnd != null) {
            assoc.setMappedBy(otherEnd.getName());
        }

        return assoc;
    }

    private String getTypeName(Property attr) {
        if (attr.getType() == null) return "String";

        String rawType;
        if (((InternalEObject) attr.getType()).eIsProxy()) {
            String uriStr = ((InternalEObject) attr.getType()).eProxyURI().toString();
            rawType = uriStr.substring(uriStr.lastIndexOf('#') + 1);
        } else {
            rawType = attr.getType().getName();
        }

        return mapType(rawType);
    }
    
    private String mapType(String rawType) {
        if (rawType == null || rawType.isEmpty()) return "String";

        switch (rawType.toLowerCase()) {
            case "date":
                return "LocalDate"; // Moze i "Date" ili "LocalDateTime" u zavisnosti od potreba
            case "string":
                return "String";
            case "int":
            case "integer":
                return "Integer";
            case "long":
                return "Long";
            case "double":
                return "Double";
            case "boolean":
                return "Boolean";
            case "float":
                return "Float";
            default:
                // Ako je tip druga klasa iz modela (npr. Korisnik, Proizvod) vrati izvorno ime
                return rawType;
        }
    }

    /**
     * Cita boolean tagged value sa stereotipa. Vraca defaultValue ako
     * tag nije popunjen u modelu.
     */
    private boolean readBooleanTag(org.eclipse.uml2.uml.Class umlClass, Stereotype st,
                                    String tagName, boolean defaultValue) {
        Object value = umlClass.getValue(st, tagName);
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }
}