package com.firma.generator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import java.util.List;

public class ModelReaderTest {
    public static void main(String[] args) {
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

        // Registruj pathmap za primitivne tipove (long, int, double, String...)
        resourceSet.getURIConverter().getURIMap().put(
            URI.createURI("pathmap://UML_LIBRARIES/"),
            URI.createURI("jar:file:JARPATH!/library/"));

        Resource resource = resourceSet.getResource(
            URI.createFileURI("Ecommerce.uml"), true);

        Model model = (Model) resource.getContents().get(0);
        System.out.println("Model ucitan: " + model.getName());

        for (PackageableElement element : model.getPackagedElements()) {
            if (element instanceof Package && element.getName().equals("Domain Model")) {
                printPackageContents((Package) element);
            }
        }
    }

    private static void printPackageContents(Package pkg) {
        System.out.println("\n=== Paket: " + pkg.getName() + " ===");
        for (PackageableElement element : pkg.getPackagedElements()) {
            if (element instanceof org.eclipse.uml2.uml.Class) {
                org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) element;
                System.out.println("\nKlasa: " + umlClass.getName());

                List<Stereotype> stereotypes = umlClass.getAppliedStereotypes();
                for (Stereotype st : stereotypes) {
                    System.out.println("  Stereotip: " + st.getName());
                    Object tableName = umlClass.getValue(st, "tableName");
                    if (tableName != null) {
                        System.out.println("    tableName = " + tableName);
                    }
                }

                for (Property attr : umlClass.getOwnedAttributes()) {
                    String typeName = attr.getType() != null ? attr.getType().getName() : "?";
                    System.out.println("  Atribut: " + attr.getName() + " : " + typeName);

                    List<Stereotype> attrStereotypes = attr.getAppliedStereotypes();
                    for (Stereotype st : attrStereotypes) {
                        System.out.println("    Stereotip atributa: " + st.getName());
                    }
                }
            } else if (element instanceof Package) {
                printPackageContents((Package) element);
            }
        }
    }
}