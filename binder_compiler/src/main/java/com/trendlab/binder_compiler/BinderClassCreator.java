package com.trendlab.binder_compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.HashMap;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author: lookey
 * @date: 2021/5/25
 * 用来生成_BinderView类的工具类
 */
public class BinderClassCreator {
    public static final String ParamName = "view";
    private TypeElement mTypeElement;//类元素
    private String mPackageName;
    private String mBinderClassName;
    private HashMap<Integer, VariableElement> mVariableElement = new HashMap<>();
    public BinderClassCreator(PackageElement mPackageElement, TypeElement mTypeElement) {
        this.mTypeElement = mTypeElement;
        this.mPackageName = mPackageElement.getQualifiedName().toString();
        this.mBinderClassName = mTypeElement.getSimpleName().toString()+"_ViewBinding";
    }
    public void putElement(int id,VariableElement variableElement){
        mVariableElement.put(id,variableElement);
    }

    public String getmPackageName() {
        return mPackageName;
    }
    //生成java类，及相应的方法
    public TypeSpec generateJavaCode(){
        return TypeSpec.classBuilder(mBinderClassName)
                .addModifiers(Modifier.PUBLIC) //public修饰
                .addMethod(generateMethod()) //添加方法
                .build();
    }
    private MethodSpec generateMethod(){
        //获取类名
        ClassName className = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        return MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(className,ParamName)
                .addCode(generateMethodCode())
                .build();
    }
    private String generateMethodCode() {
        StringBuilder code = new StringBuilder();
        for (int id : mVariableElement.keySet()) {
            VariableElement variableElement = mVariableElement.get(id);
            //使用注解的属性的名称
            String name = variableElement.getSimpleName().toString();
            //使用注解的属性的类型
            String type = variableElement.asType().toString();
            //view.name = (type)view.findViewById(id)
            String findViewCode = ParamName + "." + name + "=(" + type + ")" + ParamName +
                    ".findViewById(" + id + ");\n";
            code.append(findViewCode);

        }
        return code.toString();
    }
}
