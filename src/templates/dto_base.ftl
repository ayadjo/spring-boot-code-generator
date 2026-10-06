package ${basePackage}.${entityNameLower}.base;

public class ${entityName}DTOBase {

<#list attributes as attr>
    private ${attr.type} ${attr.name};
</#list>

<#list attributes as attr>
    public ${attr.type} get${attr.name?cap_first}() {
        return ${attr.name};
    }

    public void set${attr.name?cap_first}(${attr.type} value) {
        this.${attr.name} = value;
    }
</#list>
}