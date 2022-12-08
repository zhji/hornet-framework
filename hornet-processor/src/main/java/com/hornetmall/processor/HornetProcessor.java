package com.hornetmall.processor;

import com.google.auto.service.AutoService;
import com.hornetmall.processor.meta.EntityMeta;
import com.hornetmall.processor.meta.FieldMeta;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@AutoService(HornetProcessor.class)
public class HornetProcessor extends AbstractProcessor {

    public HornetProcessor() {
        System.out.println("===============HornetProcessor================");
    }

    private ProcessingEnvironment processingEnv;

    private List<EntityMeta> entityMetas;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_17;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("============================================================");
        System.out.println("============================================================");
        System.out.println("============================================================");
        System.out.println("============================================================");
        System.out.println("============================================================");

        Set<? extends Element> entities = roundEnv.getElementsAnnotatedWith(Entity.class);

        this.entityMetas = ElementFilter.typesIn(entities).stream().map(this::toEntityMeta).collect(Collectors.toList());

        entityMetas.forEach(entityMeta -> {


            try {

                ClassBuilder classBuilder=new ClassBuilder(entityMeta);
                classBuilder.toFile(processingEnv.getFiler());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });




        return false;
    }




    private EntityMeta toEntityMeta(TypeElement typeElement){

        Optional<VariableElement> id = ElementFilter.fieldsIn(processingEnv.getElementUtils().getAllMembers(typeElement)).stream().filter(item -> Objects.nonNull(item.getAnnotation(Id.class))).findFirst();
        if (!id.isPresent()) {
            throw new IllegalArgumentException("Id not found");
        }

        return new EntityMeta().setName(typeElement.getSimpleName().toString())
                .setTypeElement(typeElement)
                .setType(typeElement.asType())
                .setPackageName(processingEnv.getElementUtils().getPackageOf(typeElement).toString())
                .setIdName(id.get().getSimpleName().toString())
                .setIdType(id.get().asType())
                .setIdTypeElement(id.get())
                .setFields(ElementFilter.fieldsIn(processingEnv.getElementUtils().getAllMembers(typeElement)).stream().map(this::toFieldMeta).collect(Collectors.toList()));
    }

    private FieldMeta toFieldMeta(VariableElement variableElement){
        return new FieldMeta().setVariableElement(variableElement).setName(variableElement.getSimpleName().toString()).setType(variableElement.asType());
    }
}
