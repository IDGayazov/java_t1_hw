package com.example.starter.aop;

import java.time.LocalDateTime;

public record CachedData(
        LocalDateTime time,
        Object data
){}
