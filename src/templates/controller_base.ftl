package ${basePackage}.${entityNameLower}.base;

import ${basePackage}.${entityNameLower}.${entityName};
import ${basePackage}.${entityNameLower}.${entityName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class ${entityName}ControllerBase {

    @Autowired
    protected ${entityName}Service service;

    @GetMapping
    public List<${entityName}> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<${entityName}> getById(@PathVariable ${idType} id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ${entityName} create(@RequestBody ${entityName} entity) {
        return service.save(entity);
    }

    @PutMapping("/{id}")
    public ${entityName} update(@PathVariable ${idType} id, @RequestBody ${entityName} entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ${idType} id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}