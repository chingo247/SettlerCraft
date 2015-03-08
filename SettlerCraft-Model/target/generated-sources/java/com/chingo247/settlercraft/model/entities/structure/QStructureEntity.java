package com.chingo247.settlercraft.model.entities.structure;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QStructureEntity is a Querydsl query type for StructureEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStructureEntity extends EntityPathBase<StructureEntity> {

    private static final long serialVersionUID = 1200292880L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStructureEntity structureEntity = new QStructureEntity("structureEntity");

    protected com.chingo247.settlercraft.model.entities.world.QCuboidDimension dimension;

    public final EnumPath<com.chingo247.settlercraft.common.world.Direction> direction = createEnum("direction", com.chingo247.settlercraft.common.world.Direction.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> parent = createNumber("parent", Long.class);

    public final SetPath<StructurePlayerEntity, QStructurePlayerEntity> players = this.<StructurePlayerEntity, QStructurePlayerEntity>createSet("players", StructurePlayerEntity.class, QStructurePlayerEntity.class, PathInits.DIRECT2);

    public final EnumPath<StructureState> state = createEnum("state", StructureState.class);

    public final EnumPath<StructureType> type = createEnum("type", StructureType.class);

    public final NumberPath<Double> value = createNumber("value", Double.class);

    public final StringPath world = createString("world");

    public final ComparablePath<java.util.UUID> worldUUID = createComparable("worldUUID", java.util.UUID.class);

    public QStructureEntity(String variable) {
        this(StructureEntity.class, forVariable(variable), INITS);
    }

    public QStructureEntity(Path<? extends StructureEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructureEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructureEntity(PathMetadata<?> metadata, PathInits inits) {
        this(StructureEntity.class, metadata, inits);
    }

    public QStructureEntity(Class<? extends StructureEntity> type, PathMetadata<?> metadata, PathInits inits) {
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

