package com.ag.domain.service;

public interface CRUDService<Entity> {
    Entity get(Entity entity);
    Entity create(Entity entity);
    Entity update(Entity entity);
    Entity patch(Entity entity);
    Entity delete(Entity entity);
}
