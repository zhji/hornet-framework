package com.hornetmall.processor.meta;

import com.google.common.base.CaseFormat;
import com.hornetmall.processor.annotation.ReadOnly;
import com.hornetmall.processor.util.Inflector;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class EntityMeta {
    private TypeMirror type;
    private TypeElement typeElement;
    private String packageName;
    private String name;
    private ModuleMeta module;
    private String idName;
    private TypeMirror idType;
    private VariableElement idTypeElement;

    private List<FieldMeta> fields;


    public String baseName() {
        return name.replace("Entity", "");
    }


    public String entityName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, baseName());
    }


    public String basePackage() {
        return packageName.substring(0, packageName.lastIndexOf("."));
    }


    public String className() {
        return type.toString();
    }


    public List<FieldSpec> getCreateFields() {

        return fields.stream()
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(Id.class)) && Objects.isNull(f.getVariableElement().getAnnotation(ReadOnly.class)))
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(ManyToMany.class)) && Objects.isNull(f.getVariableElement().getAnnotation(OneToMany.class)))
                .map(f -> FieldSpec.builder(ClassName.get(f.getType()), f.getName()).addModifiers(Modifier.PRIVATE).build()).collect(Collectors.toList());
    }

    public List<FieldSpec> getDTOFields() {

        return fields.stream()
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(Id.class)) && Objects.isNull(f.getVariableElement().getAnnotation(ReadOnly.class)))
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(ManyToMany.class)) && Objects.isNull(f.getVariableElement().getAnnotation(OneToMany.class)))
                .map(f -> FieldSpec.builder(ClassName.get(f.getType()), f.getName()).addModifiers(Modifier.PRIVATE).build()).collect(Collectors.toList());
    }


    public List<FieldSpec> getViewDTOFields() {

        return fields.stream()
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(Id.class)) && Objects.isNull(f.getVariableElement().getAnnotation(ReadOnly.class)))
                .filter(f -> Objects.isNull(f.getVariableElement().getAnnotation(ManyToMany.class)) && Objects.isNull(f.getVariableElement().getAnnotation(OneToMany.class)))
                .map(f -> FieldSpec.builder(ClassName.get(f.getType()), f.getName()).addModifiers(Modifier.PRIVATE).build()).collect(Collectors.toList());
    }

    public TypeName getIdClassName() {
        return ClassName.get(this.idTypeElement.asType());
    }

    public ClassName getEntityClassName() {
        return ClassName.get(this.typeElement);
    }


    public TypeName getEntityTypeName() {
        return ClassName.get(this.typeElement);
    }

    public ClassName getServiceName() {
        return ClassName.bestGuess(this.basePackage() + ".service." + this.baseName() + "Service");
    }

    public String getServiceVariableName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this.baseName() + "Service");
    }


    public ClassName getRepositoryName() {
        return ClassName.bestGuess(this.basePackage() + ".repository." + this.baseName() + "Repository");
    }


    public String getRepositoryVariableName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this.baseName() + "Repository");
    }

    public ClassName getControllerName() {
        return ClassName.bestGuess(this.basePackage() + ".controller." + this.baseName() + "Controller");
    }


    public ClassName getEntitiesName() {
        return ClassName.bestGuess(this.basePackage() + ".constant." + Inflector.getInstance().pluralize(this.baseName()) + "");
    }


    public ClassName getEntitiesAuthoritiesName() {
        return ClassName.bestGuess(this.basePackage() + ".constant." + Inflector.getInstance().pluralize(this.baseName()) + "$Authorities");
    }

    public ClassName getMapperName() {
        return ClassName.bestGuess(this.basePackage() + ".mapper." + this.baseName() + "Mapper");
    }


    public String getMapperVariableName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this.baseName() + "Mapper");
    }

    public ClassName getAuthoritiesName() {
        return ClassName.bestGuess(this.basePackage() + ".authority." + this.baseName() + "Authorities");
    }


    public ClassName getCreateCommandName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.command." + this.baseName() + "CreateCommand");
    }

    public ClassName getUpdateCommandName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.command." + this.baseName() + "UpdateCommand");
    }


    public ClassName getPatchCommandName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.command." + this.baseName() + "UpdateCommand");
    }


    public ClassName getDTOName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.dto." + this.baseName() + "DTO");
    }

    public ClassName getViewDTOName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.dto." + this.baseName() + "ViewDTO");
    }


    public ClassName getQueryName() {
        return ClassName.bestGuess(this.basePackage() + ".domain.query." + this.baseName() + "Query");
    }

}
