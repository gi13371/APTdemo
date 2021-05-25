package com.trendlab.binder_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.trendlab.binder_annotation.BindView;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class BinderProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private HashMap<String,BinderClassCreator>  mCreatorMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //处理Element的工具类，用于获取程序的元素，例如包、类、方法。
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //扫描整个工程里被BindView注解过的元素,会根据activity名来生成相应的工具类BinderClassCreator
        //BinderClassCreator里包含了生成相应的activity的_ViewBinding类，里面有做了findViewById的事情
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for(Element element:elements){
            VariableElement variableElement = (VariableElement) element;//强转
            //返回此元素直接封装（非严格意义上）的元素。
            //类或接口被认为用于封装它直接声明的字段、方法、构造方法和成员类型
            //这里就是获取封装属性元素的类元素
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //获取简单类名
            String fullClassName = classElement.getQualifiedName().toString();
            //先在map缓存里取BinderClassCreator
            BinderClassCreator creator = mCreatorMap.get(fullClassName);
            if(creator == null){
                creator = new BinderClassCreator(mElementUtils.getPackageOf(classElement),classElement);
                //保存在map里
                mCreatorMap.put(fullClassName,creator);
            }
            //获取元素注解信息
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.value();
            creator.putElement(id,variableElement);
        }
        //通过javaPoet构建生成java文件
        for(String key:mCreatorMap.keySet()){
            BinderClassCreator classCreator = mCreatorMap.get(key);
            JavaFile javaFile = JavaFile.builder(classCreator.getmPackageName(), classCreator.generateJavaCode()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }
}