package com.tesisUrbe.backend.reportsManagerPdf.registry;

import com.tesisUrbe.backend.reportsManagerPdf.builders.ReportBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReportRegistry {

    private final Map<Class<?>, ReportBuilder<?>> registry = new HashMap<>();

    public <T> void register(Class<T> clazz, ReportBuilder<T> builder) {
        registry.put(clazz, builder);
    }

    @SuppressWarnings("unchecked")
    public <T> ReportBuilder<T> getBuilder(Class<T> clazz) {
        ReportBuilder<?> builder = registry.get(clazz);
        if (builder == null) {
            throw new IllegalArgumentException("No report builder registered for " + clazz.getSimpleName());
        }
        return (ReportBuilder<T>) builder;
    }
}
