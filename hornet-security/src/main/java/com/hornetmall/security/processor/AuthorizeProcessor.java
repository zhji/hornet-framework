package com.hornetmall.security.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AuthorizeProcessor extends AbstractProcessor {

    private ProcessingEnvironment environment;
    private static String swaggerOperationClassName = "io.swagger.v3.oas.annotations.Operation";

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_17;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(PreAuthorize.class.getCanonicalName(), PostAuthorize.class.getCanonicalName());
    }

    public synchronized void init(ProcessingEnvironment env) {
        this.environment = env;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(PreAuthorize.class);
        Optional<Class<? extends Annotation>> swaggerOperationClass = getSwaggerOperationClass();


        List<Meta> authorizeMeta = ElementFilter.methodsIn(elements).stream().map(method -> {


            String operation = swaggerOperationClass.map(clazz -> {
                Annotation annotation = method.getAnnotation(clazz);
                if (Objects.nonNull(annotation)) {
                    Method operationId = ClassUtils.getMethod(clazz, "operationId");
                    try {
                        Object result = operationId.invoke(annotation);
                        if (Objects.nonNull(result) && StringUtils.hasText(result.toString())) {
                            return result.toString();
                        }
                    } catch (Exception e) {

                    }
                }
                return null;
            }).orElseGet(() -> method.getSimpleName().toString());

            PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);


            return new Meta().setOperation(operation)
                    .setCode(preAuthorize.value())
                    .setMethodName(method.getSimpleName().toString());
        }).collect(Collectors.toList());


        if (roundEnv.processingOver()) {
            try (OutputStream outputStream = createMetadataResource().openOutputStream()) {

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(outputStream, authorizeMeta);
            } catch (Exception e) {
                    log.error("write authoures failed");
            }

        }


        return false;
    }

    @Data
    @Accessors(chain = true)
    public static class Meta {
        private String methodName;
        private String className;
        private String operation;
        private String code;
    }

    private FileObject createMetadataResource() throws IOException {
        return this.environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/authorities-metadata.json", new Element[0]);
    }

    private static Optional<Class<? extends Annotation>> getSwaggerOperationClass() {

        try {
            return Optional.ofNullable((Class<? extends Annotation>) Class.forName(swaggerOperationClassName));
        } catch (ClassNotFoundException e) {
            log.info("no swagger dependency");
        }
        return Optional.empty();
    }
}
