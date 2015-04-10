package com.chingo247.structureapi.persistence.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QPlayerOwnershipId is a Querydsl query type for PlayerOwnershipId
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QPlayerOwnershipId extends BeanPath<PlayerOwnershipId> {

    private static final long serialVersionUID = 2112653474L;

    public static final QPlayerOwnershipId playerOwnershipId = new QPlayerOwnershipId("playerOwnershipId");

    public final ComparablePath<java.util.UUID> player = createComparable("player", java.util.UUID.class);

    public final NumberPath<Long> structure = createNumber("structure", Long.class);

    public QPlayerOwnershipId(String variable) {
        super(PlayerOwnershipId.class, forVariable(variable));
    }

    public QPlayerOwnershipId(Path<? extends PlayerOwnershipId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlayerOwnershipId(PathMetadata<?> metadata) {
        super(PlayerOwnershipId.class, metadata);
    }

}

