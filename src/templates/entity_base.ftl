package ${basePackage}.${entityNameLower}.base;

import jakarta.persistence.*;;
import java.util.List;
import java.util.ArrayList;

<#list associations as assoc>
import ${basePackage}.${assoc.targetType?lower_case}.${assoc.targetType};
</#list>

@MappedSuperclass
public abstract class ${entityName}Base {

<#list attributes as attr>
<#if attr.id>
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
</#if>
<#if attr.required>
    @Column(nullable = false<#if attr.unique>, unique = true</#if>)
<#elseif attr.unique>
    @Column(unique = true)
</#if>
    private ${attr.type} ${attr.name};
</#list>

<#list associations as assoc>
<#if assoc.multiplicity == "*">
<#if assoc.composition>
    @OneToMany(mappedBy = "${assoc.mappedBy}", cascade = CascadeType.ALL, orphanRemoval = true)
<#else>
    @OneToMany(mappedBy = "${assoc.mappedBy}")
</#if>
    private List<${assoc.targetType}> ${assoc.roleName} = new ArrayList<>();
<#else>
    @ManyToOne
    @JoinColumn(name = "${assoc.roleName}_id")
    private ${assoc.targetType} ${assoc.roleName};
</#if>
</#list>

<#list attributes as attr>
    public ${attr.type} get${attr.name?cap_first}() {
        return ${attr.name};
    }

    public void set${attr.name?cap_first}(${attr.type} value) {
        this.${attr.name} = value;
    }
</#list>

<#list associations as assoc>
<#if assoc.multiplicity == "*">
    public List<${assoc.targetType}> get${assoc.roleName?cap_first}() {
        return ${assoc.roleName};
    }

    public void set${assoc.roleName?cap_first}(List<${assoc.targetType}> value) {
        this.${assoc.roleName} = value;
    }
<#else>
    public ${assoc.targetType} get${assoc.roleName?cap_first}() {
        return ${assoc.roleName};
    }

    public void set${assoc.roleName?cap_first}(${assoc.targetType} value) {
        this.${assoc.roleName} = value;
    }
</#if>
</#list>
}