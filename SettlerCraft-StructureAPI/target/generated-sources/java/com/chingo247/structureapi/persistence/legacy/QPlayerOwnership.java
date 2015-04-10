package com.chingo247.structureapi.persistence.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPlayerOwnership is a Querydsl query type for PlayerOwnership
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPlayerOwnership extends EntityPathBase<PlayerOwnership> {

    private static final long serialVersionUID = -234672857L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlayerOwnership playerOwnership = new QPlayerOwnership("playerOwnership");

    public final StringPath name = createString("name");

    protected QPlayerOwnershipId ownershipId;

    public final EnumPath<PlayerOwnership.Type> ownerType = createEnum("ownerType", PlayerOwnership.Type.class);

    public final ComparablePath<java.util.UUID> player = createComparable("player", java.util.UUID.class);

    protected QStructure structure;

    public QPlayerOwnership(String variable) {
        this(PlayerOwnership.class, forVariable(variable), INITS);
    }

    public QPlayerOwnership(Path<? extends PlayerOwnership> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerOwnership(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerOwnership(PathMetadata<?> metadata, PathInits inits) {
        this(PlayerOwnership.class, metadata, inits);
    }

    public QPlayerOwnership(Class<? extends PlayerOwnership> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ownershipId = inits.isInitialized("ownershipId") ? new QPlayerOwnershipId(forProperty("ownershipId")) : null;
        this.structure = inits.isInitialized("structure") ? new QStructure(forProperty("structure"), inits.get("structure")) : null;
    }

    public QPlayerOwnershipId ownershipId() {
        if (ownershipId == null) {
            ownershipId = new QPlayerOwnershipId(forProperty("ownershipId"));
        }
        return ownershipId;
    }

    public QStructure structure() {
        if (structure == null) {
            structure = new QStructure(forProperty("structure"));
        }
        return structure;
    }

}

