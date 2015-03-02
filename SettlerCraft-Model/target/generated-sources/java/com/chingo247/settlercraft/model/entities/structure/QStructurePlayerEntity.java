package com.chingo247.settlercraft.model.entities.structure;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QStructurePlayerEntity is a Querydsl query type for StructurePlayerEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStructurePlayerEntity extends EntityPathBase<StructurePlayerEntity> {

    private static final long serialVersionUID = 1540196497L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStructurePlayerEntity structurePlayerEntity = new QStructurePlayerEntity("structurePlayerEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<java.util.UUID> player = createComparable("player", java.util.UUID.class);

    public final StringPath playerName = createString("playerName");

    public final EnumPath<StructurePlayerRole> playerRole = createEnum("playerRole", StructurePlayerRole.class);

    protected QStructureEntity structureentity;

    public final NumberPath<Long> structureId = createNumber("structureId", Long.class);

    public QStructurePlayerEntity(String variable) {
        this(StructurePlayerEntity.class, forVariable(variable), INITS);
    }

    public QStructurePlayerEntity(Path<? extends StructurePlayerEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructurePlayerEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructurePlayerEntity(PathMetadata<?> metadata, PathInits inits) {
        this(StructurePlayerEntity.class, metadata, inits);
    }

    public QStructurePlayerEntity(Class<? extends StructurePlayerEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.structureentity = inits.isInitialized("structureentity") ? new QStructureEntity(forProperty("structureentity"), inits.get("structureentity")) : null;
    }

    public QStructureEntity structureentity() {
        if (structureentity == null) {
            structureentity = new QStructureEntity(forProperty("structureentity"));
        }
        return structureentity;
    }

}

