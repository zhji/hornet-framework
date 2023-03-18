package com.hornetmall.processor.meta;

import com.squareup.javapoet.AnnotationSpec;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

@Data
@Accessors(chain = true)
public class FieldMeta {
    private String name;
    private VariableElement variableElement;
    private TypeMirror type;
    private boolean readyOnly=false;
    private boolean nullable;

    private List<AnnotationSpec> validationAnnotations;




}
