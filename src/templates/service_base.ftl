package ${basePackage}.${entityNameLower}.base;

import ${basePackage}.${entityNameLower}.${entityName};
import ${basePackage}.${entityNameLower}.${entityName}Repository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public abstract class ${entityName}ServiceBase {

    @Autowired
    protected ${entityName}Repository repository;

    public List<${entityName}> findAll() {
        return repository.findAll();
    }

    public Optional<${entityName}> findById(${idType} id) {
        return repository.findById(id);
    }

    public ${entityName} save(${entityName} entity) {
        return repository.save(entity);
    }

    public void deleteById(${idType} id) {
        repository.deleteById(id);
    }
}