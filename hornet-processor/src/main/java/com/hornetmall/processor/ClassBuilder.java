package com.hornetmall.processor;

import com.hornetmall.processor.meta.EntityMeta;
import com.hornetmall.processor.util.ClassUtils;
import com.squareup.javapoet.*;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.io.IOException;

public class ClassBuilder {

    private static final ClassName EXCEPTIONS_CLASS_NAME=ClassName.bestGuess("com.hornetmall.framework.exception.Exceptions");
    private static final ClassName QUERY_SPECIFICATION_CLASS_NAME=ClassName.bestGuess("com.hornetmall.framework.domain.query.QuerySpecification");


    private final EntityMeta entityMeta;
    private ProcessingEnvironment processingEnvironment;

    public ClassBuilder(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public TypeSpec buildRepository() {
        return TypeSpec.interfaceBuilder(ClassUtils.getRepositoryName(entityMeta))


                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(CrudRepository.class), ClassName.get(entityMeta.getType()), ClassName.get(entityMeta.getIdType())))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(JpaSpecificationExecutor.class), ClassName.get(entityMeta.getType())))
                .build();
    }


    public TypeSpec buildCreateCommand() {
        return TypeSpec.classBuilder(ClassUtils.getCreateCommandName(entityMeta))

                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build())
                .addFields(entityMeta.getCreateFields())
                .build();
    }


    public TypeSpec buildUpdateCommand() {
        return TypeSpec.classBuilder(ClassUtils.getUpdateCommandName(entityMeta))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build())
                .addFields(entityMeta.getCreateFields())
                .build();
    }

    public TypeSpec buildDTO() {
        return TypeSpec.classBuilder(ClassUtils.getDTOName(entityMeta))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build())
                .addFields(entityMeta.getDTOFields())
                .build();
    }

    public TypeSpec buildViewDTO() {
        return TypeSpec.classBuilder(ClassUtils.getViewDTOName(entityMeta))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build())
                .addFields(entityMeta.getViewDTOFields())
                .build();
    }



    public TypeSpec buildQuery() {
        return TypeSpec.classBuilder(ClassUtils.getQueryName(entityMeta))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .superclass(ParameterizedTypeName.get(QUERY_SPECIFICATION_CLASS_NAME,entityMeta.getEntityClassName()))
                .addAnnotation(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build())
                .addFields(entityMeta.getCreateFields())
                .addMethod(MethodSpec.methodBuilder("buildPredicates")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(ParameterSpec.builder(Root.class, "root").build())
                        .addParameter(ParameterSpec.builder(CriteriaQuery.class, "query").build())
                        .addParameter(ParameterSpec.builder(CriteriaBuilder.class, "criteriaBuilder").build())
                        .build())
                .build();
    }


    public TypeSpec buildMapper() {


        return TypeSpec.interfaceBuilder(ClassUtils.getMapperName(entityMeta)).addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Mapper.class).addMember("componentModel", "$S", "spring").build())

                .addMethod(MethodSpec.methodBuilder("toEntity")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ClassName.get(entityMeta.getType()))
                        .addParameter(ParameterSpec.builder(ClassUtils.getCreateCommandName(entityMeta), "command").build())
                        .build())

                .addMethod(MethodSpec.methodBuilder("toEntity")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(ClassUtils.getUpdateCommandName(entityMeta), "command").build())
                        .addParameter(ParameterSpec.builder(ClassName.get(entityMeta.getType()), "entity").addAnnotation(MappingTarget.class).build())
                        .build())


                .addMethod(MethodSpec.methodBuilder("patchToEntity")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(BeanMapping.class).addMember("nullValuePropertyMappingStrategy", "$T.$L", NullValuePropertyMappingStrategy.class, NullValuePropertyMappingStrategy.IGNORE).build())
                        .addParameter(ParameterSpec.builder(ClassUtils.getUpdateCommandName(entityMeta), "command").build())
                        .addParameter(ParameterSpec.builder(ClassName.get(entityMeta.getType()), "entity").addAnnotation(MappingTarget.class).build())
                        .build())


                .addMethod(MethodSpec.methodBuilder("toDTO")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ClassUtils.getDTOName(entityMeta))
                        .addParameter(ParameterSpec.builder(ClassName.get(entityMeta.getType()), "entity").build())
                        .build())

                .addMethod(MethodSpec.methodBuilder("toViewDTO")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ClassUtils.getViewDTOName(entityMeta))
                        .addParameter(ParameterSpec.builder(ClassName.get(entityMeta.getType()), "entity").build())
                        .build())


                .build();
    }



    public TypeSpec buildService() {


        return TypeSpec.classBuilder(ClassUtils.getServiceName(entityMeta)).addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Service.class).build())
                .addAnnotation(RequiredArgsConstructor.class)
                .addField(FieldSpec.builder(entityMeta.getRepositoryName() ,entityMeta.getRepositoryVariableName()).addModifiers(Modifier.PRIVATE,Modifier.FINAL).build())
                .addField(FieldSpec.builder(entityMeta.getMapperName() ,entityMeta.getMapperVariableName()).addModifiers(Modifier.PRIVATE,Modifier.FINAL).build())


                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers( Modifier.PUBLIC)
                        .addAnnotation(Transactional.class)
                        .addParameter(ParameterSpec.builder(entityMeta.getCreateCommandName(), "command").build())
                        .addCode(CodeBlock.builder()
                                .add("$T entity = $L.toEntity(command);",entityMeta.getEntityClassName(),entityMeta.getMapperVariableName())

                                .add("$L.save(entity);",entityMeta.getRepositoryVariableName())
                                .build())
                        .build())




                .addMethod(MethodSpec.methodBuilder("update")
                        .addModifiers( Modifier.PUBLIC)
                        .addAnnotation(Transactional.class)
                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").build())
                        .addParameter(ParameterSpec.builder(entityMeta.getUpdateCommandName(), "command").build())
                        .addCode(CodeBlock.builder()
                                .add("$L.findById(id).ifPresentOrElse(entity -> {\n" +
                                        "            $L.toEntity(command,entity);\n" +
                                        "            $L.save(entity);\n" +
                                        "        },()-> $T.noContent(id, $T.class));",entityMeta.getRepositoryVariableName(),entityMeta.getMapperVariableName(),entityMeta.getRepositoryVariableName(), EXCEPTIONS_CLASS_NAME,entityMeta.getEntityClassName())
                                .build())
                        .build())


                .addMethod(MethodSpec.methodBuilder("patch")
                        .addModifiers( Modifier.PUBLIC)
                        .addAnnotation(Transactional.class)
                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").build())
                        .addParameter(ParameterSpec.builder(entityMeta.getUpdateCommandName(), "command").build())
                        .addCode(CodeBlock.builder()
                                .add("$L.findById(id).ifPresentOrElse(entity -> {\n" +
                                        "            $L.patchToEntity(command,entity);\n" +
                                        "            $L.save(entity);\n" +
                                        "        },()-> $T.noContent(id, $T.class));",entityMeta.getRepositoryVariableName(),entityMeta.getMapperVariableName(),entityMeta.getRepositoryVariableName(), EXCEPTIONS_CLASS_NAME,entityMeta.getEntityClassName())
                                .build())
                        .build())





                .addMethod(MethodSpec.methodBuilder("delete")
                        .addModifiers( Modifier.PUBLIC)
                        .addAnnotation(Transactional.class)
                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").build())
                        .addCode(CodeBlock.builder()
                                .add(" if ($L.existsById(id)) {\n" +
                                        "            $L.deleteById(id);\n" +
                                        "        }",entityMeta.getRepositoryVariableName(),entityMeta.getRepositoryVariableName())
                                .build())
                        .build())





                .addMethod(MethodSpec.methodBuilder("page")
                        .addModifiers( Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Page.class),entityMeta.getDTOName()))
                        .addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("readOnly","$L",true).build())
                        .addParameter(ParameterSpec.builder(entityMeta.getQueryName(),"query").build())
                        .addCode(CodeBlock.builder()
                                .add(" return $L.findAll(query, query.pageable()).map($L::toDTO);",entityMeta.getRepositoryVariableName(),entityMeta.getMapperVariableName())
                                .build())
                        .build())
                .build();
    }


    public TypeSpec buildEntitiesDefine(){
        return TypeSpec.classBuilder(entityMeta.getEntitiesName())
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addField(FieldSpec.builder(String.class,"ENTITIES_NAME").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("$S",entityMeta.entityName()).build())
                .addField(FieldSpec.builder(String.class,"MODULE").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("$S",entityMeta.entityName()).build())
                .addField(FieldSpec.builder(String.class,"AUTHORITY_NAME").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("$S",entityMeta.entityName()).build())
                .addType(TypeSpec.classBuilder("Authorities").addModifiers(Modifier.PUBLIC,Modifier.STATIC)

                        .addField(FieldSpec.builder(String.class,"CREATE").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("MODULE+$S+AUTHORITY_NAME+$S",":",":create").build())
                        .addField(FieldSpec.builder(String.class,"UPDATE").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("MODULE+$S+AUTHORITY_NAME+$S",":",":update").build())
                        .addField(FieldSpec.builder(String.class,"PATCH").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("MODULE+$S+AUTHORITY_NAME+$S",":",":patch").build())
                        .addField(FieldSpec.builder(String.class,"DELETE").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("MODULE+$S+AUTHORITY_NAME+$S",":",":delete").build())
                        .addField(FieldSpec.builder(String.class,"QUERY").addModifiers(Modifier.PUBLIC,Modifier.STATIC,Modifier.FINAL).initializer("MODULE+$S+AUTHORITY_NAME+$S",":",":query").build())
                        .build())
                .build();
    }


    public TypeSpec buildController(){


        return TypeSpec.classBuilder(entityMeta.getControllerName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(RestController.class).build())
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("path","$S+$T.ENTITIES_NAME","/",entityMeta.getEntitiesName()).build())
                .addField(FieldSpec.builder(entityMeta.getServiceName() ,entityMeta.getServiceVariableName()).addModifiers(Modifier.PRIVATE,Modifier.FINAL).build())




                .addMethod(MethodSpec.methodBuilder("page")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(ResponseEntity.class),ParameterizedTypeName.get(ClassName.get(Page.class),entityMeta.getDTOName())))
                        .addAnnotation(GetMapping.class)
                        .addAnnotation(AnnotationSpec.builder(PreAuthorize.class).addMember("value","\"hasAuthority('\"+ $T.Authorities.QUERY +\"')\"",entityMeta.getEntitiesName()).build())

                        .addParameter(entityMeta.getQueryName(),"query")
                        .addCode(CodeBlock.builder()
                                .add("return ResponseEntity.ok($L.page(query));",entityMeta.getServiceVariableName())

                                .build())


                        .build())




                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ResponseEntity.class)

                        .addAnnotation(PostMapping.class)
                        .addAnnotation(AnnotationSpec.builder(PreAuthorize.class).addMember("value","\"hasAuthority('\"+ $T.Authorities.CREATE +\"')\"",entityMeta.getEntitiesName()).build())

                        .addParameter(entityMeta.getCreateCommandName(),"command")
                        .addCode(CodeBlock.builder()
                                .add("$L.create(command);",entityMeta.getServiceVariableName())
                                .add("return ResponseEntity.created(null).build();",entityMeta.getServiceVariableName())
                                .build())
                        .build())



                .addMethod(MethodSpec.methodBuilder("update")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ResponseEntity.class)

                        .addAnnotation(AnnotationSpec.builder(PutMapping.class).addMember("value","$S","/{id}").build())
                        .addAnnotation(AnnotationSpec.builder(PreAuthorize.class).addMember("value","\"hasAuthority('\"+ $T.Authorities.UPDATE +\"')\"",entityMeta.getEntitiesName()).build())

                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").addAnnotation(PathVariable.class).build())

                        .addParameter(entityMeta.getUpdateCommandName(),"command")
                        .addCode(CodeBlock.builder()
                                .add("$L.update(id,command);",entityMeta.getServiceVariableName())
                                .add("return ResponseEntity.ok().build();",entityMeta.getServiceVariableName())
                                .build())
                        .build())




                .addMethod(MethodSpec.methodBuilder("patch")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ResponseEntity.class)

                        .addAnnotation(AnnotationSpec.builder(PatchMapping.class).addMember("value","$S","/{id}").build())
                        .addAnnotation(AnnotationSpec.builder(PreAuthorize.class).addMember("value","\"hasAuthority('\"+ $T.Authorities.PATCH +\"')\"",entityMeta.getEntitiesName()).build())
                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").addAnnotation(PathVariable.class).build())
                        .addParameter(entityMeta.getUpdateCommandName(),"command")
                        .addCode(CodeBlock.builder()
                                .add("$L.patch(id,command);",entityMeta.getServiceVariableName())
                                .add("return ResponseEntity.ok().build();",entityMeta.getServiceVariableName())
                                .build())
                        .build())




                .addMethod(MethodSpec.methodBuilder("delete")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ResponseEntity.class)

                        .addAnnotation(AnnotationSpec.builder(DeleteMapping.class).addMember("value","$S","/{id}").build())
                        .addAnnotation(AnnotationSpec.builder(PreAuthorize.class).addMember("value","\"hasAuthority('\"+ $T.Authorities.DELETE +\"')\"",entityMeta.getEntitiesName()).build())
                        .addParameter(ParameterSpec.builder(entityMeta.getIdClassName(),"id").addAnnotation(PathVariable.class).build())
                        .addCode(CodeBlock.builder()
                                .add("$L.delete(id);",entityMeta.getServiceVariableName())
                                .add("return ResponseEntity.ok().build();",entityMeta.getServiceVariableName())
                                .build())
                        .build())

                .build();
    }



    public void toFile(Filer filer) throws IOException {
        JavaFile.builder(this.entityMeta.getRepositoryName().packageName(),buildRepository()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getCreateCommandName().packageName(),buildCreateCommand()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getUpdateCommandName().packageName(),buildUpdateCommand()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getDTOName().packageName(),buildDTO()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getViewDTOName().packageName(),buildViewDTO()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getQueryName().packageName(),buildQuery()).build().writeTo(filer);


        JavaFile.builder(this.entityMeta.getMapperName().packageName(),buildMapper()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getServiceName().packageName(),buildService()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getEntitiesName().packageName(),buildEntitiesDefine()).build().writeTo(filer);
        JavaFile.builder(this.entityMeta.getControllerName().packageName(),buildController()).build().writeTo(filer);

    }
}
