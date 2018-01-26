package com.drouter.compiler;

import com.drouter.base.annotation.Interceptor;
import com.drouter.compiler.util.TextUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * description:
 * author: Darren on 2018/1/22 12:29
 * email: 240336124@qq.com
 * version: 1.0
 */
@AutoService(Processor.class)
public class InterceptorProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private Filer mFiler;
    private final String KEY_MODULE_NAME = "moduleName";
    private TypeMirror iInterceptor = null;
    private String moduleName = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        iInterceptor = mElementUtils.getTypeElement(Consts.ACTIONINTERCEPTOR).asType();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 1. 有没配置 modelName 防止 class 类冲突
        String moduleName = "";

        Map<String, String> options = processingEnv.getOptions();
        if (isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (!TextUtils.isEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
        } else {
            String errorMessage = "These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n";
            throw new RuntimeException("DRouter::Compiler >>> No module name, for more information, look at gradle log.\n" + errorMessage);
        }

        // 生成类继承和实现接口
        ClassName routerAssistClassName = ClassName.get("com.drouter.api.action", "IRouterInterceptor");
        ClassName mapClassName = ClassName.get("java.util", "Map");
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder("DRouter$$Interceptor$$" + moduleName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(routerAssistClassName)
                .addField(mapClassName, "interceptors", Modifier.PRIVATE);

        // 构造函数
        MethodSpec.Builder constructorMethodBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        constructorMethodBuilder.addStatement("interceptors = new $T<>()", ClassName.get("java.util", "TreeMap"));

        // 2. 解析到所有的 Action 信息
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Interceptor.class);
        Map<Integer, String> interceptors = new HashMap<>(elements.size());

        for (Element element : elements) {

            // 获取注解上面的 priority
            Interceptor interceptorAnnotation = element.getAnnotation(Interceptor.class);
            int priority = interceptorAnnotation.priority();

            // 获取 Interceptor 的 ClassName
            Element enclosingElement = element.getEnclosingElement();
            String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            String interceptorClassName = packageName + "." + element.getSimpleName();

            // 判断 Interceptor 注解类是否实现了 ActionInterceptor
            if (!((TypeElement) element).getInterfaces().contains(iInterceptor)) {
                error(element, "%s verify failed, @Interceptor must be implements %s", element.getSimpleName().toString(), Consts.ACTIONINTERCEPTOR);
            }

            if (interceptors.containsKey(priority)) {
                // 输出错误，拦截器优先级 冲突重复了
                error(element, "More than one interceptors use same priority <%s> , The last interceptor was <%s>", String.valueOf(priority), interceptors.get(priority));
            }
            // 添加到集合
            interceptors.put(priority, interceptorClassName);

            constructorMethodBuilder.addStatement("this.interceptors.put(" + priority + ",new $T())", ClassName.bestGuess(interceptorClassName));
        }

        // 实现方法
        MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("getInterceptors")
                .addAnnotation(Override.class)
                .returns(List.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        unbindMethodBuilder.addStatement("return $T.getInterceptorClasses(interceptors)", ClassName.get("com.drouter.api.utils", "MapUtils"));

        classBuilder.addMethod(constructorMethodBuilder.build());
        classBuilder.addMethod(unbindMethodBuilder.build());

        // 生成类，看下效果
        try {
            JavaFile.builder(Consts.ROUTER_INTERCEPTOR_PACK_NAME, classBuilder.build())
                    .addFileComment("DRouter 自动生成")
                    .build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("翻车了！");
        }

        return false;
    }


    private boolean isNotEmpty(Map<String, String> options) {
        return options != null && !options.isEmpty();
    }

    private void error(Element element, String message, String... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        processingEnv.getMessager().printMessage(kind, message, element);
    }


    // 1. 指定处理的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 2. 给到需要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        // 需要解析的自定义注解 BindView  OnClick
        annotations.add(Interceptor.class);
        return annotations;
    }
}
