package com.hornetmall.processor.util;

import com.hornetmall.processor.meta.EntityMeta;
import com.squareup.javapoet.ClassName;

public class ClassUtils {

    public static ClassName getServiceName(EntityMeta entityMeta){
      return    ClassName.bestGuess(entityMeta.basePackage()+".service."+entityMeta.baseName()+"Service");
    }

    public static ClassName getRepositoryName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".repository."+entityMeta.baseName()+"Repository");
    }

    public static ClassName getControllerName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".service."+entityMeta.baseName()+"Controller");
    }


    public static ClassName getMapperName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".mapper."+entityMeta.baseName()+"Mapper");
    }

    public static ClassName getAuthoritiesName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".authority."+entityMeta.baseName()+"Authorities");
    }


    public static ClassName getCreateCommandName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.command."+entityMeta.baseName()+"CreateCommand");
    }

    public static ClassName getUpdateCommandName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.command."+entityMeta.baseName()+"UpdateCommand");
    }


    public static ClassName getPatchCommandName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.command."+entityMeta.baseName()+"UpdateCommand");
    }


    public static ClassName getDTOName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.dto."+entityMeta.baseName()+"DTO");
    }

    public static ClassName getViewDTOName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.dto."+entityMeta.baseName()+"ViewDTO");
    }


    public static ClassName getQueryName(EntityMeta entityMeta){
        return    ClassName.bestGuess(entityMeta.basePackage()+".domain.query."+entityMeta.baseName()+"Query");
    }
}
