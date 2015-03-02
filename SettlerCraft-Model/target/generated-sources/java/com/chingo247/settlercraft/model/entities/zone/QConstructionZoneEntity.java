package com.chingo247.settlercraft.model.entities.zone;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QConstructionZoneEntity is a Querydsl query type for ConstructionZoneEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QConstructionZoneEntity extends EntityPathBase<ConstructionZoneEntity> {

    private static final long serialVersionUID = 2060169527L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConstructionZoneEntity constructionZoneEntity = new QConstructionZoneEntity("constructionZoneEntity");

    public final ListPath<ConstructionZonePlayerEntity, QConstructionZonePlayerEntity> allowedPlayers = this.<ConstructionZonePlayerEntity, QConstructionZonePlayerEntity>createList("allowedPlayers", ConstructionZonePlayerEntity.class, QConstructionZonePlayerEntity.class, PathInits.DIRECT2);

    protected com.chingo247.settlercraft.model.entities.world.QCuboidDimension dimension;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath permitsAll = createBoolean("permitsAll");

    public final ComparablePath<java.util.UUID> world = createComparable("world", java.util.UUID.class);

    public QConstructionZoneEntity(String variable) {
        this(ConstructionZoneEntity.class, forVariable(variable), INITS);
    }

    public QConstructionZoneEntity(Path<? extends ConstructionZoneEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QConstructionZoneEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QConstructionZoneEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ConstructionZoneEntity.class, metadata, inits);
    }

    public QConstructionZoneEntity(Class<? extends ConstructionZoneEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dimension = inits.isInitialized("dimension") ? new com.chingo247.settlercraft.model.entities.world.QCuboidDimension(forProperty("dimension")) : null;
    }

    public com.chingo247.settlercraft.model.entities.world.QCuboidDimension dimension() {
        if (dimension == null) {
            dimension = new com.chingo247.settlercraft.model.entities.world.QCuboidDimension(forProperty("dimension"));
        }
        return dimension;
    }

}

