package com.example.demo.mapper;

import com.example.demo.entity.Performance;
import com.example.demo.web.dto.WalletPerformanceResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for Performance.
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PerformanceMapper {

    @Mapping(target = "total", source = "totalUsd")
    @Mapping(target = "bestAsset", source = "bestAsset")
    @Mapping(target = "worstAsset", source = "worstAsset")
    @Mapping(target = "bestPerformance", source = "bestPerformance")
    @Mapping(target = "worstPerformance", source = "worstPerformance")
    WalletPerformanceResponse toDto(Performance entity);

    @Mapping(target = "totalUsd", source = "total")
    @Mapping(target = "bestAsset", source = "bestAsset")
    @Mapping(target = "worstAsset", source = "worstAsset")
    @Mapping(target = "bestPerformance", source = "bestPerformance")
    @Mapping(target = "worstPerformance", source = "worstPerformance")
    @Mapping(target = "id", ignore = true)
    Performance toEntity(WalletPerformanceResponse dto, @Context MappingContext context);
}
