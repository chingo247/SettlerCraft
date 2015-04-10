package com.chingo247.structureapi.persistence.entities.settlement;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSettlementEntity is a Querydsl query type for SettlementEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSettlementEntity extends EntityPathBase<SettlementEntity> {

    private static final long serialVersionUID = 724842338L;

    public static final QSettlementEntity settlementEntity = new QSettlementEntity("settlementEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath worldName = createString("worldName");

    public final ComparablePath<java.util.UUID> worldUUID = createComparable("worldUUID", java.util.UUID.class);

    public QSettlementEntity(String variable) {
        super(SettlementEntity.class, forVariable(variable));
    }

    public QSettlementEntity(Path<? extends SettlementEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSettlementEntity(PathMetadata<?> metadata) {
        super(SettlementEntity.class, metadata);
    }

}

