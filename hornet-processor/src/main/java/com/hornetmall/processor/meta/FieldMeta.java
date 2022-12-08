package com.hornetmall.processor.meta;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Data
@Accessors(chain = true)
public class FieldMeta {
    private String name;
    private VariableElement variableElement;
    private TypeMirror type;
    private boolean readyOnly=false;


}
