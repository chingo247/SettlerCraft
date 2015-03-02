package com.chingo247.settlercraft.model.entities.zone;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QConstructionZonePlayerEntity is a Querydsl query type for ConstructionZonePlayerEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QConstructionZonePlayerEntity extends EntityPathBase<ConstructionZonePlayerEntity> {

    private static final long serialVersionUID = -1003783048L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConstructionZonePlayerEntity constructionZonePlayerEntity = new QConstructionZonePlayerEntity("constructionZonePlayerEntity");

    protected QConstructionZoneEntity constructionZoneEntity;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<java.util.UUID> playerId = createComparable("playerId", java.util.UUID.class);

    public final StringPath playerName = createString("playerName");

    public QConstructionZonePlayerEntity(String variable) {
        this(ConstructionZonePlayerEntity.class, forVariable(variable), INITS);
    }

    public QConstructionZonePlayerEntity(Path<? extends ConstructionZonePlayerEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QConstructionZonePlayerEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QConstructionZonePlayerEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ConstructionZonePlayerEntity.class, metadata, inits);
    }

    public QConstructionZonePlayerEntity(Class<? extends ConstructionZonePlayerEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.constructionZoneEntity = inits.isInitialized("constructionZoneEntity") ? new QConstructionZoneEntity(forProperty("constructionZoneEntity"), inits.get("constructionZoneEntity")) : null;
    }

    public QConstructionZoneEntity constructionZoneEntity() {
        if (constructionZoneEntity == null) {
            constructionZoneEntity = new QConstructionZoneEntity(forProperty("constructionZoneEntity"));
        }
        return constructionZoneEntity;
    }

}

