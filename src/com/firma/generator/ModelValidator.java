package com.firma.generator;

import com.firma.generator.model.AssociationInfo;
import com.firma.generator.model.AttributeInfo;
import com.firma.generator.model.EntityInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelValidator {

    public List<String> validate(List<EntityInfo> entities) {
        List<String> errors = new ArrayList<>();

        // Skup imena svih entiteta - koristi se za proveru da asocijacije
        // pokazuju na entitete koji stvarno postoje u modelu
        Set<String> entityNames = new HashSet<>();
        for (EntityInfo entity : entities) {
            entityNames.add(entity.name);
        }

        for (EntityInfo entity : entities) {
            validateEntity(entity, entityNames, errors);
        }

        return errors;
    }

    private void validateEntity(EntityInfo entity, Set<String> entityNames, List<String> errors) {

        // Pravilo 1 (odgovara OCL-u MustHaveId):
        // svaka klasa mora imati bar jedan atribut sa Id stereotipom
        boolean hasId = false;
        int idCount = 0;
        for (AttributeInfo attr : entity.attributes) {
            if (attr.isId) {
                hasId = true;
                idCount++;
            }
        }
        if (!hasId) {
            errors.add(entity.name + ": nema atribut sa Id stereotipom");
        }
        if (idCount > 1) {
            errors.add(entity.name + ": ima više od jednog Id atributa (" + idCount + ")");
        }

        // Pravilo 2 (odgovara OCL-u NonEmptyTableName):
        // tableName mora biti popunjen
        if (entity.tableName == null || entity.tableName.trim().isEmpty()) {
            errors.add(entity.name + ": nema popunjen tableName");
        }

        // Pravilo 3: ime entiteta ne sme biti prazno (osnovna zdrava provera)
        if (entity.name == null || entity.name.trim().isEmpty()) {
            errors.add("Pronađen entitet bez imena (proveri model u MagicDraw-u)");
        }

        // Pravilo 4: atributi ne smeju imati nepoznat/nerazrešen tip
        for (AttributeInfo attr : entity.attributes) {
            if (attr.type == null || attr.type.equals("?")) {
                errors.add(entity.name + "." + attr.name + ": tip atributa nije prepoznat");
            }
        }

        // Pravilo 5: asocijacije moraju pokazivati na entitet koji postoji u modelu
        for (AssociationInfo assoc : entity.associations) {
            if (!entityNames.contains(assoc.targetType)) {
                errors.add(entity.name + "." + assoc.roleName
                    + ": asocijacija pokazuje na nepostojeći entitet '" + assoc.targetType + "'");
            }
        }

        // Pravilo 6: upozorenje (ne greška) ako entitet nema nijedan pravi atribut
        // osim Id-a - moguće da je nešto pogrešno mapirano kao asocijacija
        int nonIdAttributeCount = 0;
        for (AttributeInfo attr : entity.attributes) {
            if (!attr.isId) nonIdAttributeCount++;
        }
        if (nonIdAttributeCount == 0) {
            System.out.println("  [upozorenje] " + entity.name
                + " nema atributa osim Id-a - proveri da li je to namerno");
        }
    }
}