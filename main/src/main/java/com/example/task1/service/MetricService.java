package com.example.task1.service;

import com.example.task1.entity.enums.Metrics;
import io.micrometer.core.instrument.Tag;

import java.util.List;

public interface MetricService {
    void increment(Metrics metric);
    void increment(Metrics metric, List<Tag> additionalTags);
}
