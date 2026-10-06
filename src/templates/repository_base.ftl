package ${basePackage}.${entityNameLower}.base;

import org.springframework.data.jpa.repository.JpaRepository;
import ${basePackage}.${entityNameLower}.${entityName};

public interface ${entityName}RepositoryBase extends JpaRepository<${entityName}, ${idType}> {
}