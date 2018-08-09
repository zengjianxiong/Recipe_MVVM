package com.lsxiao.apllo.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.lsxiao.apllo.processor.step.BackpressureStep;
import com.lsxiao.apllo.processor.step.ObserveStep;
import com.lsxiao.apllo.processor.step.ReceiveStep;
import com.lsxiao.apllo.processor.step.StickyStep;
import com.lsxiao.apllo.processor.step.SubscribeStep;
import com.lsxiao.apllo.processor.step.TakeStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import static com.lsxiao.apllo.processor.ApolloProcessor.KEY_APOLLO_CLASS_NAME;

@SupportedOptions(KEY_APOLLO_CLASS_NAME)
@AutoService(Processor.class)
public class ApolloProcessor extends BasicAnnotationProcessor {
    public static Map<Element, ApolloDescriptor> sDescriptorMap = new HashMap<>();
    private boolean mGenerated = false;

    public static final String KEY_APOLLO_CLASS_NAME = "apolloClassName";

    private String mClassName;

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ReceiveStep(),
                new TakeStep(),
                new StickyStep(),
                new BackpressureStep(),
                new SubscribeStep(),
                new ObserveStep()
        );
    }

    @Override
    protected void postRound(RoundEnvironment roundEnv) {
        super.postRound(roundEnv);
        if (mGenerated || sDescriptorMap.isEmpty()) {
            return;
        }
        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (options == null || options.isEmpty()) {
            throw new RuntimeException("Apollo::Compiler >>> No generate class name, for more information, look at gradle log.");
        }
        mClassName = options.get(KEY_APOLLO_CLASS_NAME);
        if (mClassName == null || mClassName.length() == 0) {
            throw new RuntimeException("Apollo::Compiler >>> No generate class name, for more information, look at gradle log.");
        }
        CodeGenerator.Companion.create(new ArrayList<>(sDescriptorMap.values()), processingEnv.getFiler(), mClassName).generate();
        mGenerated = true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
