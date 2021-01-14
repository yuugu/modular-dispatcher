package com.yuugu.modular.dispatcher.processor;

import com.yuugu.modular.dispatcher.annotations.SingleModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"com.yuugu.modular.dispatcher.annotations.SingleModule"})
public class ModulesProcessor extends AbstractProcessor {

    private static final String TAG = "ModulesProcessor";
    private static final String PROVIDER_CLASS_PATH_PREFIX = "ModuleClassProvider_";
    private static final String PACKAGE_NAME = "com.yuugu.modular.dispatcher.auto.generated";
    private static final String BUILD_GENERATE_DIR_NAME = "build" + File.separator + "generated" + File.separator + "source";
    private static final String MODULE_NAME_DIR = "modular-dispatcher-modules";

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) {
            return false;
        }
        info("=========================> ModulesProcessor instance : " + this);

        Set<Element> allDestinationElements = (Set<Element>) roundEnv.getElementsAnnotatedWith(SingleModule.class);

        info("allDestinationElements : " + allDestinationElements);

        try {

            for (Element element : allDestinationElements) {

                info("--------------------------------> start process element:  " + element);

                final TypeElement typeElement = (TypeElement) element;
                SingleModule module = typeElement.getAnnotation(SingleModule.class);
                if (module == null) {
                    return false;
                }

                final String name = module.value();
                final String clazzPath = ((TypeElement) element).getQualifiedName().toString();

                if (isEmpty(name)) {
                    throw new IllegalArgumentException("Module.name must not be null for " + clazzPath);
                }

                if (!isModuleNameCorrect(name)) {
                    throw new IllegalArgumentException("Module.name error for " + clazzPath + ", must only contain number, lower-case letters, en dash and start with letters.");
                }

                final String providerName = PROVIDER_CLASS_PATH_PREFIX + upperCamelCase(name);
                final String mappingFullClassName = PACKAGE_NAME + "." + providerName;

                info("name : " + name);
                info("clazzPath : " + clazzPath);
                info("providerName : " + providerName);
                info("mappingFullClassName : " + mappingFullClassName);

                String str = "" +
                        "package " + PACKAGE_NAME + ";\n" +
                        "\n" +
                        "import com.yuugu.modular.dispatcher.annotations.NotProguard;\n" +
                        "import com.yuugu.modular.dispatcher.core.ModuleClassProvider;\n" +
                        "import com.yuugu.modular.dispatcher.core.Module;\n" +
                        "\n" +
                        "@NotProguard\n" +
                        "public class " + providerName + " implements ModuleClassProvider {\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public Class<? extends Module> moduleClass() {\n" +
                        "        return " + clazzPath + ".class;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public String moduleName() {\n" +
                        "        return \"" + name + "\";\n" +
                        "    }\n" +
                        "}\n";

                info("final class  " + providerName + " : \n\n" + str + "\n\n");

                String fullJavaPath = createJavaSourceFileWithContent(mappingFullClassName, str);

                info("fullJavaPath = " + fullJavaPath);
                info("BUILD_GENERATE_DIR_NAME = " + BUILD_GENERATE_DIR_NAME);

            }

        } catch (Exception e) {
            error(
                    "Unexpected error thrown while generating ModuleClassProvider file." +
                            " Please report stack trace to ethanfeng ! : \n" + getDetailStack(e));

            e.printStackTrace();
        }

        return false;
    }

    public static String getDetailStack(Throwable throwable) {
        if (throwable == null) return "";
        try {
            Writer writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            String errorStack = writer.toString();
            return errorStack;
        } catch (Throwable ex) {
            return "error while get detail stack: " + throwable;
        }
    }

    private String createJavaSourceFileWithContent(String mappingFullClassName, String str) throws IOException {
        JavaFileObject source = processingEnv.getFiler().createSourceFile(mappingFullClassName);
        Writer writer = source.openWriter();
        writer.write(str);
        writer.flush();
        writer.close();
        return source.getName();
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

    private static boolean isModuleNameCorrect(String moduleName) {
        final String reg = "[a-z]+(-|_*[a-z0-9]+)*";
        if (isEmpty(moduleName)) {
            return false;
        }
        return moduleName.matches(reg);
    }

    private String upperCamelCase(String name) {
        if (name.contains("-")) {
            String[] arr = name.split("-");
            StringBuilder ret = new StringBuilder();
            for (String s : arr) {
                if (!isEmpty(s)) ret.append(capitalize(s));
            }
            return ret.toString();
        }
        return capitalize(name);
    }

    private String capitalize(String str) {
        return !isEmpty(str) ? str.substring(0, 1).toUpperCase() + str.substring(1) : str;
    }

    private void info(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, TAG + " >>> " + msg);
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, TAG + " >>> " + msg);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(SingleModule.class.getCanonicalName());
        return types;
    }
}
