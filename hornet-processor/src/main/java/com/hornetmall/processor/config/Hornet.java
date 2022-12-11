package com.hornetmall.processor.config;

import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;


@Data
public class Hornet {

    private static  Hornet INSTANCE;

    public static void init(ProcessingEnvironment processingEnvironment) {
        Yaml yaml=new Yaml();
        try {
            FileObject fileObject = processingEnvironment.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "hornet.yaml");
            INSTANCE=yaml.loadAs(fileObject.openInputStream(),Hornet.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String module;


    public Hornet() {

    }

    public static Hornet getInstance(){
        return INSTANCE;
    }

}
