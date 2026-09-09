package com.sz.resource.service;

import com.sz.resource.model.ResourceRef;

import java.util.List;
import java.util.Optional;

/**
 * 受保护资源引用的统一契约。
 */
public interface ResourceReferenceService {

    Optional<ResourceRef> findActiveReference(Long resourceId);

    List<ResourceRef> normalizeForCreate(List<ResourceRef> submitted, String sceneCode, Long userId);

    List<ResourceRef> normalizeForUpdate(List<ResourceRef> existing, List<ResourceRef> submitted, String sceneCode, Long userId);
}
