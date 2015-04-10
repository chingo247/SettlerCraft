package com.chingo247.structureapi.persistence.entities.settlement;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPlayerSettlerEntity is a Querydsl query type for PlayerSettlerEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPlayerSettlerEntity extends EntityPathBase<PlayerSettlerEntity> {

    private static final long serialVersionUID = -1560812237L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlayerSettlerEntity playerSettlerEntity = new QPlayerSettlerEntity("playerSettlerEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<java.util.UUID> playerId = createComparable("playerId", java.util.UUID.class);

    public final StringPath playerName = createString("playerName");

    protected QSettlementEntity settlementEntity;

    public QPlayerSettlerEntity(String variable) {
        this(PlayerSettlerEntity.class, forVariable(variable), INITS);
    }

    public QPlayerSettlerEntity(Path<? extends PlayerSettlerEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerSettlerEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerSettlerEntity(PathMetadata<?> metadata, PathInits inits) {
        this(PlayerSettlerEntity.class, metadata, inits);
    }

    public QPlayerSettlerEntity(Class<? extends PlayerSettlerEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.settlementEntity = inits.isInitialized("settlementEntity") ? new QSettlementEntity(forProperty("settlementEntity")) : null;
    }

    public QSettlementEntity settlementEntity() {
        if (settlementEntity == null) {
            settlementEntity = new QSettlementEntity(forProperty("settlementEntity"));
        }
        return settlementEntity;
    }

}

