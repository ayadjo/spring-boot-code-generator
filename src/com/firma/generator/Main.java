package com.firma.generator;

import com.firma.generator.model.AttributeInfo;
import com.firma.generator.model.EntityInfo;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    static Configuration cfg;

    public static void main(String[] args) throws Exception {
        // 1. Ucitaj model
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(
            "http://www.eclipse.org/uml2/3.0.0/UML", UMLPackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", UMLResource.Factory.INSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("uml", UMLResource.Factory.INSTANCE);

        Resource resource = resourceSet.getResource(
            URI.createFileURI("Ecommerce.uml"), true);
        Model model = (Model) resource.getContents().get(0);

        Package domainModel = null;
        for (PackageableElement element : model.getPackagedElements()) {
            if (element instanceof Package && element.getName().equals("Domain Model")) {
                domainModel = (Package) element;
            }
        }
        
        String apiPrefix = "/api"; // fallback vrednost ako tag nije popunjen
        for (Stereotype st : domainModel.getAppliedStereotypes()) {
            if (st.getName().equals("Application")) {
                Object prefixValue = domainModel.getValue(st, "apiPrefix");
                if (prefixValue != null && !prefixValue.toString().trim().isEmpty()) {
                    apiPrefix = prefixValue.toString();
                }
            }
        }
        System.out.println("apiPrefix iz modela: " + apiPrefix);

        // 2. Izvuci entitete
        ModelExtractor extractor = new ModelExtractor();
        List<EntityInfo> entities = extractor.extract(domainModel);

        // 2.5 Validiraj model
        ModelValidator validator = new ModelValidator();
        List<String> errors = validator.validate(entities);

        if (!errors.isEmpty()) {
            System.err.println("Model nije validan, generator prekida rad:");
            for (String error : errors) {
                System.err.println("  - " + error);
            }
            return;
        }

        // 3. Podesi FreeMarker
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDirectoryForTemplateLoading(new File("src/templates"));

        BeansWrapperBuilder wrapperBuilder = new BeansWrapperBuilder(Configuration.VERSION_2_3_32);
        wrapperBuilder.setExposeFields(true);
        cfg.setObjectWrapper(wrapperBuilder.build());

        String basePackage = "com.tvojafirma.ecommerce";

        // 4. Generisi sve artefakte za svaki entitet
        for (EntityInfo entity : entities) {
            String nameLower = entity.getName().toLowerCase();
            String idType = getIdType(entity);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("basePackage", basePackage);
            dataModel.put("entityName", entity.getName());
            dataModel.put("entityNameLower", nameLower);
            dataModel.put("tableName", entity.getTableName());
            dataModel.put("attributes", entity.getAttributes());
            dataModel.put("associations", entity.getAssociations());
            dataModel.put("idType", idType);
            dataModel.put("apiPrefix", apiPrefix);
            dataModel.put("apiPath", entity.getApiPath());
            dataModel.put("softDelete", entity.isSoftDelete());
            dataModel.put("auditable", entity.isAuditable());

            // --- Entity ---
            generateArtifact("entity_base.ftl", dataModel,
                "output/" + nameLower + "/base/" + entity.name + "Base.java");
            generateArtifact("entity_skeleton.ftl", dataModel,
                "output/" + nameLower + "/" + entity.name + ".java");

            // --- Repository ---
            if (entity.isGenerateRepository()) {
                generateArtifact("repository_base.ftl", dataModel,
                    "output/" + nameLower + "/base/" + entity.name + "RepositoryBase.java");
                generateArtifact("repository_skeleton.ftl", dataModel,
                    "output/" + nameLower + "/" + entity.name + "Repository.java");
            }

            // --- Service ---
            if (entity.isGenerateService()) {
                generateArtifact("service_base.ftl", dataModel,
                    "output/" + nameLower + "/base/" + entity.name + "ServiceBase.java");
                generateArtifact("service_skeleton.ftl", dataModel,
                    "output/" + nameLower + "/" + entity.name + "Service.java");
            }

            // --- Controller ---
            if (entity.isGenerateController()) {
                generateArtifact("controller_base.ftl", dataModel,
                    "output/" + nameLower + "/base/" + entity.name + "ControllerBase.java");
                generateArtifact("controller_skeleton.ftl", dataModel,
                    "output/" + nameLower + "/" + entity.name + "Controller.java");
            }

            // --- DTO ---
            if (entity.isGenerateDTO()) {
                generateArtifact("dto_base.ftl", dataModel,
                    "output/" + nameLower + "/base/" + entity.name + "DTOBase.java");
                generateArtifact("dto_skeleton.ftl", dataModel,
                    "output/" + nameLower + "/" + entity.name + "DTO.java");
            }
        }
    }

    /**
     * Pronalazi tip atributa koji ima Id stereotip. Vraca "Long" kao fallback
     * ako entitet nema Id (validator bi ovo vec trebalo da uhvati ranije).
     */
    private static String getIdType(EntityInfo entity) {
        for (AttributeInfo attr : entity.getAttributes()) {
            if (attr.isId()) return attr.getType();
        }
        return "Long";
    }

    /**
     * Generise jedan fajl iz datog template-a.
     * "Base" fajlovi (putanja sadrzi "/base/") se UVEK prepisuju.
     * Ostali (skeleton/rucno pisani) fajlovi se generisu SAMO ako jos ne postoje,
     * cime se stite rucne izmene developera pri ponovnom pokretanju generatora.
     */
    private static void generateArtifact(String templateName,
                                          Map<String, Object> dataModel,
                                          String outputPath) throws Exception {

        File outputFile = new File(outputPath);
        boolean isBaseFile = outputPath.contains("/base/");

        if (!isBaseFile && outputFile.exists()) {
            System.out.println("Preskočeno (već postoji): " + outputPath);
            return;
        }

        outputFile.getParentFile().mkdirs();

        Template template = cfg.getTemplate(templateName);
        try (Writer out = new FileWriter(outputFile)) {
            template.process(dataModel, out);
        }

        System.out.println((isBaseFile ? "Generisano: " : "Generisan skeleton: ") + outputPath);
    }
}