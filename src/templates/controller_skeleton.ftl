package ${basePackage}.${entityNameLower};

import ${basePackage}.${entityNameLower}.base.${entityName}ControllerBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${apiPrefix}/${entityNameLower}")
public class ${entityName}Controller extends ${entityName}ControllerBase {
    
}