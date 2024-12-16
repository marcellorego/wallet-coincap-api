package com.example.demo.service;

import com.example.demo.entity.Asset;
import com.example.demo.mapper.AssetMapper;
import com.example.demo.mapper.MappingContext;
import com.example.demo.repository.AssetRepository;
import com.example.demo.service.client.AssetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

/**
 * Service implementation for Asset.
 */

@Slf4j
@Service
public class AssetService {

    private final AssetMapper assetMapper;
    private final AssetRepository assetRepository;
    private final MappingContext mappingContext;

    public AssetService(final Clock defaultClock,
                        final AssetMapper assetMapper,
                        final AssetRepository assetRepository) {
        this.assetMapper = assetMapper;
        this.assetRepository = assetRepository;
        this.mappingContext = new MappingContext(defaultClock);
    }

    /**
     * Saves asset data.
     *
     * @param assetData the asset data
     */
    @Transactional
    public void saveAsset(final AssetData assetData) {
        final Asset asset = assetMapper.toAsset(assetData, this.mappingContext);
        assetRepository.save(asset);
    }

    /**
     * Saves a list of asset data.
     *
     * @param assetDataList the list of asset data
     */
    @Transactional
    public void saveAssets(final List<AssetData> assetDataList) {
        assetDataList.forEach(this::saveAsset);
    }
}
