package com.hornetmall.processor;

import com.hornetmall.processor.annotation.Generated;
import com.hornetmall.processor.config.Hornet;
import com.hornetmall.processor.meta.EntityMeta;
import com.hornetmall.processor.meta.FieldMeta;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;



public class HornetProcessor extends AbstractProcessor {

    public HornetProcessor() {
        System.out.println("===============HornetProcessor================");
    }

    private ProcessingEnvironment processingEnv;

    private List<EntityMeta> entityMetas;

    private List<Class> validationAnnotaions=List.of(
            AssertFalse              .class,
            AssertTrue.class,
            DecimalMax.class,
            DecimalMin.class,
            Digits.class,
            Email.class,
            Future.class,
            FutureOrPresent.class,
            Max.class,
            Min.class,
            Negative.class,
            NegativeOrZero.class,
            NotBlank.class,
            NotEmpty.class,
            NotNull.class,
            Null.class,
            Past.class,
            PastOrPresent.class,
            Pattern.class,
            Positive.class,
            PositiveOrZero.class,
            Size.class
    );

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
        Column column = variableElement.getAnnotation(Column.class);
        List<AnnotationSpec> validations=new ArrayList<>();
        if (Objects.nonNull(column)&&!column.nullable()) {
            validations.add(AnnotationSpec.builder(NotNull.class).build());
        }






            for (AnnotationMirror mirror : variableElement.getAnnotationMirrors()) {
                String annotationTypeName = mirror.getAnnotationType().asElement().getSimpleName().toString();
                validationAnnotaions.stream().filter(a-> Objects.equals(a.getName(),annotationTypeName)).findFirst().ifPresent(annotationClass->{
                    AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(ClassName.bestGuess(annotationTypeName));

                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                        String fieldName = entry.getKey().getSimpleName().toString();
                        Object value = entry.getValue().getValue();
                        try {
                            Method method = annotationClass.getMethod(fieldName);
                            Object defaultValue = method.getDefaultValue();
                            if (value != null && !value.equals(defaultValue)) {
                                annotationBuilder.addMember(fieldName, "$S", value);
                            }
                        } catch (NoSuchMethodException ignored) {
                        }

                    }

                    validations.add(annotationBuilder.build());
                });


            }










        return new FieldMeta()
                .setValidationAnnotations(validations)
                .setNullable(Objects.isNull(column)?true:column.nullable())
                .setVariableElement(variableElement)
                .setName(variableElement.getSimpleName().toString())
                .setType(variableElement.asType());
    }

}
