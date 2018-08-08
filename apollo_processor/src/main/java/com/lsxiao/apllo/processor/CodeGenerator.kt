package com.lsxiao.apllo.processor

import com.lsxiao.apollo.core.Apollo
import com.lsxiao.apollo.core.contract.ApolloBinder
import com.lsxiao.apollo.core.contract.ApolloBinderGenerator
import com.lsxiao.apollo.core.entity.ApolloBinderImpl
import com.lsxiao.apollo.core.entity.Event
import com.lsxiao.apollo.core.entity.SchedulerProvider
import com.squareup.javapoet.*
import io.reactivex.BackpressureStrategy
import io.reactivex.subscribers.DisposableSubscriber
import java.io.IOException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror


/**
 * write with Apollo
 * author:lsxiao
 * date:2017-04-22 18:00
 * github:https://github.com/lsxiao
 * zhihu:https://zhihu.com/people/lsxiao
 */

class CodeGenerator private constructor(private val apolloDescriptors: ArrayList<ApolloDescriptor>, private val mFiler: Filer, private val modelName: String) {

    companion object {
        private val GENERATE_PACKAGE_NAME = "com.lsxiao.apollo.generate"
        private val SINGLE_INSTANCE_PARAM_NAME = "sInstance"
        private val SINGLE_INSTANCE_METHOD_NAME = "instance"
        private val SUBSCRIBER_BINDER_LOCAL_PARAM_NAME = "apolloBinder"
        private val GENERATE_METHOD_BIND_OBJECT_NAME = "bindObject"
        private val EVENT_PARAM_NAME = "event"

        private val SUBSCRIBER_LOCAL_NAME = "subscriber"
        private val TO_FLOWABLE_STICKY_METHOD_NAME = "toFlowableSticky"
        private val TO_FLOWABLE_METHOD_NAME = "toFlowable"


        fun create(apolloDescriptors: ArrayList<ApolloDescriptor>, filer: Filer, modelName: String): CodeGenerator = CodeGenerator(apolloDescriptors, filer, modelName)
    }
    //生成不同的实例供Apollo调用
    private var GENERATE_CLASS_NAME = "ApolloBinderGenerator_" + modelName + "_Impl"

    fun generate() = createJavaFile()

    private fun createJavaFile() = try {
        getBinderGeneratorJavaFile().writeTo(mFiler)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    private fun getBinderGeneratorJavaFile(): JavaFile = JavaFile
            .builder(GENERATE_PACKAGE_NAME, getGeneratorTypeSpec())
            .addStaticImport(SchedulerProvider.Tag.MAIN)
            .addStaticImport(SchedulerProvider.Tag.IO)
            .addStaticImport(SchedulerProvider.Tag.COMPUTATION)
            .addStaticImport(SchedulerProvider.Tag.NEW)
            .addStaticImport(SchedulerProvider.Tag.SINGLE)
            .addStaticImport(SchedulerProvider.Tag.TRAMPOLINE)
            .build()

    /**
     *   public final class ApolloBinderGeneratorImpl implements ApolloBinderGenerator {
     *      ...
     *  }
     */
    private fun getGeneratorTypeSpec(): TypeSpec = TypeSpec
            .classBuilder(GENERATE_CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addSuperinterface(ApolloBinderGenerator::class.java)
            .addField(getSingleInstanceFileSpec())
            .addMethod(getBroadcastEventFunctionMethodSpec())
            .addMethod(getSingleInstanceMethodSpec())
            .addMethod(getGenerateFunctionMethodSpec())
            .addMethod(getInvokeClassMethodSpec())
            .build()

    /**
     *   private static ApolloBinderGenerator sInstance;
     */
    private fun getSingleInstanceFileSpec(): FieldSpec = FieldSpec
            .builder(ApolloBinderGenerator::class.java, SINGLE_INSTANCE_PARAM_NAME, Modifier.PRIVATE, Modifier.STATIC)
            .build()

    /**
     *  public static synchronized ApolloBinderGenerator instance() {
     *    if (null == sInstance) {
     *        sInstance = new ApolloBinderGeneratorImpl();
     *    }
     *    return sInstance;
     *  }
     */
    private fun getSingleInstanceMethodSpec(): MethodSpec = MethodSpec.methodBuilder(SINGLE_INSTANCE_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
            .returns(ApolloBinderGenerator::class.java)
            .beginControlFlow("if (null == $SINGLE_INSTANCE_PARAM_NAME)")
            .addStatement("$SINGLE_INSTANCE_PARAM_NAME = new $GENERATE_CLASS_NAME()")
            .endControlFlow()
            .addStatement("return $SINGLE_INSTANCE_PARAM_NAME")
            .build()

    /**
     * public void broadcastEvent(final Event event) {
     *      if(com.lsxiao.apollo.core.Apollo.getContext()==null||!(com.lsxiao.apollo.core.Apollo.getContext() instanceof android.content.Context)) {
     *      return;
     *      }
     *      ...
     *      return;
     * }
     */
    private fun getBroadcastEventFunctionMethodSpec(): MethodSpec {
        val builder = MethodSpec.methodBuilder("broadcastEvent")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(Event::class.java, EVENT_PARAM_NAME, Modifier.FINAL)
                .beginControlFlow("if(${getContext()}==null||!(${getContext()} instanceof android.content.Context))")
                .addStatement("return")
                .endControlFlow()
                .addCode(getSendIntentCodeBlock())

        return builder.addStatement("return").build()
    }


    /**
     *
     * android.content.Intent intent = new android.content.Intent("apollo");
     * android.content.Context context =(android.content.Context)com.lsxiao.apollo.core.Apollo.getContext();
     * intent.putExtra("event", event);
     * context.sendBroadcast(intent);
     */
    private fun getSendIntentCodeBlock(): CodeBlock = CodeBlock.builder()
            .addStatement("android.content.Intent intent = new android.content.Intent(\"apollo\")")
            .addStatement("android.content.Context context =(android.content.Context)${getContext()}")
            .addStatement("intent.putExtra(\"event\", ${getSerializer()}.serialize($EVENT_PARAM_NAME))")
            .addStatement("context.sendBroadcast(intent)")
            .build()

    /**
     *  @Override
     *  public ApolloBinder generate(Object object) {
     *      final ApolloBinderImpl apolloBinder = new ApolloBinderImpl();
     *      ...
     *      return apolloBinder;
     *  }
     */
    private fun getGenerateFunctionMethodSpec(): MethodSpec {
        val builder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .returns(ApolloBinder::class.java)
                .addParameter(Any::class.java, GENERATE_METHOD_BIND_OBJECT_NAME, Modifier.FINAL)
                .addStatement("final \$T $SUBSCRIBER_BINDER_LOCAL_PARAM_NAME = new \$T($GENERATE_METHOD_BIND_OBJECT_NAME)", ApolloBinderImpl::class.java, ApolloBinderImpl::class.java)

        apolloDescriptors.forEach {
            getSingleBinderStatement(builder, it)
        }

        return builder.addStatement("return $SUBSCRIBER_BINDER_LOCAL_PARAM_NAME").build()
    }

    /**
     *  if (bindObject.getClass().isAssignableFrom(...class)) {
     *      apolloBinder.add(Apollo.get().toFlowable(new String[]{...}).subscribeOn(Apollo.get().getSchedulerProvider().get(...)).observeOn(...).subscribeWith(...))
     *  }
     */
    private fun getSingleBinderStatement(builder: MethodSpec.Builder, descriptor: ApolloDescriptor) {
        val clazzType = descriptor.methodElement.enclosingElement.asType().toString().replace(Regex("<.*>"), "")

        builder.beginControlFlow("if($clazzType.class.isAssignableFrom($GENERATE_METHOD_BIND_OBJECT_NAME.getClass()))")
                .addStatement("$SUBSCRIBER_BINDER_LOCAL_PARAM_NAME.add(" +
                        getApollo() +
                        getToFlowableCode(descriptor) +
                        getEventOnceReceiveCode(descriptor) +
                        getBackpressure(descriptor) +
                        getSubscribeOnMethodCode(descriptor) +
                        getObserveOnMethodCode(descriptor) +
                        getSubscribeWithCode(descriptor) +
                        ")",
                        Apollo::class.java
                )
                .endControlFlow()
    }

    private fun getInvokeClassMethodSpec(): MethodSpec {
        val builder = MethodSpec.methodBuilder("getInvokeClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .returns(String::class.java)
                .addStatement("String invokeClassesStr = \"\"")

        var strList:MutableList<String> = ArrayList()

        apolloDescriptors.forEach { it
            val clazzType = it.methodElement.enclosingElement.asType().toString().replace(Regex("<.*>"), "")
            if (!strList.contains(clazzType)) {
                strList.add(clazzType)
                builder.addStatement("invokeClassesStr = invokeClassesStr + \"$clazzType\" + " + "\",\"")
            }
        }

        return builder.addStatement("return invokeClassesStr").build()
    }


    /**
     *  .toFlowable(new String[...tags])
     */
    private fun getToFlowableCode(descriptor: ApolloDescriptor): CodeBlock {
        val toFlowable: String = if (descriptor.isSticky) {
            TO_FLOWABLE_STICKY_METHOD_NAME
        } else {
            TO_FLOWABLE_METHOD_NAME
        }
        return CodeBlock.of(".$toFlowable(${getTagsStringArrayCode(descriptor)})")
    }

    private fun getTagsStringArrayCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of("new String[]{${Util.split(descriptor.tags, ",")}}")

    /**
     *  .onBackpressureBuffer()
     *  .onBackpressureDrop()
     *  .
     */
    private fun getBackpressure(descriptor: ApolloDescriptor): CodeBlock {
        val onBackpressure: String = when (descriptor.backpressureStrategy) {
            BackpressureStrategy.BUFFER -> ".onBackpressureBuffer()"
            BackpressureStrategy.DROP -> ".onBackpressureDrop()"
            BackpressureStrategy.LATEST -> ".onBackpressureLatest()"
            else -> {
                ""
            }
        }
        return CodeBlock.of(onBackpressure)
    }

    /**
     *  Apollo.get().getSchedulerProvider().get(descriptor.subscribeOn)
     */
    private fun getSubscribeOnMethodCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(".subscribeOn(${getSchedulerCode(descriptor.subscribeOn)})")

    /**
     *  Apollo.get().getSchedulerProvider().get(descriptor.observeOn)
     */
    private fun getObserveOnMethodCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(".observeOn(${getSchedulerCode(descriptor.observeOn)})")

    /**
     *  Apollo.get().getSchedulerProvider().get(...)
     */
    private fun getSchedulerCode(tag: SchedulerProvider.Tag): CodeBlock = CodeBlock.of("${getApollo()}.getSchedulerProvider().get(${tag.name})")

    /**
     *  Apollo.get()
     */
    private fun getApollo(): CodeBlock = CodeBlock.of("\$T", Apollo::class.java)

    /**
     * Apollo.getContext()
     */
    private fun getContext(): CodeBlock = CodeBlock.of("\$T.getContext()", Apollo::class.java)


    /**
     * Apollo.getContext()
     */
    private fun getSerializer(): CodeBlock = CodeBlock.of("\$T.getSerializer()", Apollo::class.java)

    /**
     *  .subscribeWith(new DisposableSubscriber<Object>(){
     *      ...
     *  }),
     */
    private fun getSubscribeWithCode(descriptor: ApolloDescriptor): CodeBlock = CodeBlock.of(
            ".subscribeWith(new \$T<Object>(){" +
                    getOnCompleteMethodCode() +
                    getOnErrorMethodCode() +
                    getOnNextMethodCode(descriptor) +
                    "})",
            DisposableSubscriber::class.java
    )

    /**
     *  @java.lang.Override
     *  public void onComplete(Object o) {
     *      ...
     *  }
     */
    private fun getOnCompleteMethodCode(): MethodSpec = MethodSpec.methodBuilder("onComplete")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .build()

    /**
     *  @java.lang.Override
     *  public void onError(java.lang.Throwable t) {
     *      ...
     *  }
     */
    private fun getOnErrorMethodCode(): MethodSpec = MethodSpec.methodBuilder("onError")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(Throwable::class.java, "t")
            .build()

    /**
     *  @java.lang.Override
     *  public void onNext(Object o) {
     *      ...
     *  }
     */
    private fun getOnNextMethodCode(descriptor: ApolloDescriptor): MethodSpec = MethodSpec.methodBuilder("onNext")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(Object::class.java, "o")
            .addCode(getStickyAutoRemoveCode(descriptor))
            .addCode(getReceiveMethodInvokeCode(descriptor).toString())
            .build()

    /**
     * Apollo.get().removeStickyEvent(new String[]{"sticky"});
     */
    private fun getStickyAutoRemoveCode(descriptor: ApolloDescriptor): CodeBlock = if (descriptor.isSticky && descriptor.stickyAutoRemove) {
        CodeBlock.builder().addStatement("${getApollo()}.removeStickyEvent(${getTagsStringArrayCode(descriptor)})").build()
    } else {
        CodeBlock.of("")
    }


    /**
     * .take(...)
     */
    private fun getEventOnceReceiveCode(descriptor: ApolloDescriptor): CodeBlock = if (descriptor.take > 0) {
        CodeBlock.of(".take(${descriptor.take})")
    } else {
        CodeBlock.of("")
    }

    /**
     * subscriber.onNext(...)
     */
    private fun getReceiveMethodInvokeCode(descriptor: ApolloDescriptor): CodeBlock {
        val clazzType = descriptor.methodElement.enclosingElement.asType().toString().replace(Regex("<.*>"), "")
        val builder = CodeBlock
                .builder()
                .beginControlFlow("try")
                .addStatement("final $clazzType $SUBSCRIBER_LOCAL_NAME=($clazzType)$GENERATE_METHOD_BIND_OBJECT_NAME")

        if (descriptor.methodElement.parameters.map(VariableElement::asType).isEmpty()) {
            builder.addStatement("$SUBSCRIBER_LOCAL_NAME.${descriptor.methodElement.simpleName}()")
        } else {
            val typeMirror = descriptor.methodElement.parameters.map(VariableElement::asType).first()

            builder.beginControlFlow("if(o instanceof ${parseVariableType(typeMirror)})")
            builder.addStatement("$SUBSCRIBER_LOCAL_NAME.${descriptor.methodElement.simpleName}((\$T)o)", typeMirror)
            builder.endControlFlow()
        }
        builder.endControlFlow()
        builder.beginControlFlow("catch (Exception e)")
        builder.endControlFlow()
        return builder.build()
    }

    /**
     * 返回基本数据类型装箱后的类型

     * @param typeMirror VariableElement
     * *
     * @return String
     */
    private fun parseVariableType(typeMirror: TypeMirror): String {
        val typeKind = typeMirror.kind
        when (typeKind) {
            TypeKind.BOOLEAN -> {
                return "Boolean"
            }
            TypeKind.BYTE -> {
                return "Byte"
            }
            TypeKind.SHORT -> {
                return "Short"
            }
            TypeKind.INT -> {
                return "Integer"
            }
            TypeKind.LONG -> {
                return "Long"
            }
            TypeKind.CHAR -> {
                return "Character"
            }
            TypeKind.FLOAT -> {
                return "Float"
            }
            TypeKind.DOUBLE -> {
                return "Double"
            }
            else -> {
                if (typeMirror is DeclaredType) {
                    return handleGenericTypeVariable(typeMirror)
                }

                return typeMirror.toString()
            }
        }
    }

    /**
     * List<User> return java.util.List.class ,rather than java.util.List<User>.class
     * @return String
     *
     */
    private fun handleGenericTypeVariable(typeMirror: TypeMirror): String {
        val declaredType = typeMirror as DeclaredType
        return (declaredType.asElement() as TypeElement).qualifiedName.toString()
    }

}
