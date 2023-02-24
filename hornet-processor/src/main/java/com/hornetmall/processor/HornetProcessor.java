package com.hornetmall.processor;

import com.hornetmall.processor.annotation.Generated;
import com.hornetmall.processor.config.Hornet;
import com.hornetmall.processor.meta.EntityMeta;
import com.hornetmall.processor.meta.FieldMeta;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



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
        Hornet.init(processingEnv);
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

        this.entityMetas = ElementFilter.typesIn(entities).stream().filter(typeElement -> Objects.isNull(typeElement.getAnnotation(Generated.class))).map(this::toEntityMeta).collect(Collectors.toList());
        String match = Hornet.getInstance().getMatch();
        entityMetas.forEach(entityMeta -> {

            if (Objects.nonNull(match)) {
                if (!entityMeta.getName().matches(match)) {
                    return;
                }
            }

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
