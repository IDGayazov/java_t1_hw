package com.example.task1.service.impl;

import com.example.task1.entity.enums.Metrics;
import com.example.task1.service.MetricService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class MetricServiceImpl implements MetricService {
    private final MeterRegistry meterRegistry;
    private final Tag applicationTag = Tag.of("application", "t1_java");

    public MetricServiceImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void increment(Metrics metric) {
        increment(metric, Collections.emptyList());
    }

    @Override
    public void increment(Metrics metric, List<Tag> additionalTags) {
        List<Tag> allTags = new ArrayList<>();
        allTags.add(applicationTag);
        allTags.addAll(additionalTags);

        meterRegistry.counter(metric.getValue(), allTags).increment();
    }
}
