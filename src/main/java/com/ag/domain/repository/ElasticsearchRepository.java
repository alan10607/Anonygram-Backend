package com.ag.domain.repository;

public interface ElasticsearchRepository<Entity> {
    Entity get(Entity entity);
    Entity save(Entity entity);
    Entity delete(Entity entity);
}