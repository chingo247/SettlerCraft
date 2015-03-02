package com.chingo247.settlercraft.model.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QStructure is a Querydsl query type for Structure
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStructure extends EntityPathBase<Structure> {

    private static final long serialVersionUID = 1632122192L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStructure structure = new QStructure("structure");

    public final NumberPath<Long> checksum = createNumber("checksum", Long.class);

    protected com.chingo247.settlercraft.model.entities.world.QCuboidDimension dimension;

    public final NumberPath<Direction> direction = createNumber("direction", Direction.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    protected com.chingo247.settlercraft.model.entities.world.QLocation location;

    protected QStructureLog logEntry;

    public final SetPath<PlayerMembership, QPlayerMembership> memberships = this.<PlayerMembership, QPlayerMembership>createSet("memberships", PlayerMembership.class, QPlayerMembership.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final SetPath<PlayerOwnership, QPlayerOwnership> ownerships = this.<PlayerOwnership, QPlayerOwnership>createSet("ownerships", PlayerOwnership.class, QPlayerOwnership.class, PathInits.DIRECT2);

    public final NumberPath<Double> refundValue = createNumber("refundValue", Double.class);

    public final EnumPath<Structure.State> state = createEnum("state", Structure.State.class);

    public final StringPath structureRegion = createString("structureRegion");

    public QStructure(String variable) {
        this(Structure.class, forVariable(variable), INITS);
    }

    public QStructure(Path<? extends Structure> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructure(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QStructure(PathMetadata<?> metadata, PathInits inits) {
        this(Structure.class, metadata, inits);
    }

    public QStructure(Class<? extends Structure> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.dimension = inits.isInitialized("dimension") ? new com.chingo247.settlercraft.model.entities.world.QCuboidDimension(forProperty("dimension")) : null;
        this.location = inits.isInitialized("location") ? new com.chingo247.settlercraft.model.entities.world.QLocation(forProperty("location")) : null;
        this.logEntry = inits.isInitialized("logEntry") ? new QStructureLog(forProperty("logEntry")) : null;
    }

    public com.chingo247.settlercraft.model.entities.world.QCuboidDimension dimension() {
        if (dimension == null) {
            dimension = new com.chingo247.settlercraft.model.entities.world.QCuboidDimension(forProperty("dimension"));
        }
        return dimension;
    }

    public com.chingo247.settlercraft.model.entities.world.QLocation location() {
        if (location == null) {
            location = new com.chingo247.settlercraft.model.entities.world.QLocation(forProperty("location"));
        }
        return location;
    }

    public QStructureLog logEntry() {
        if (logEntry == null) {
            logEntry = new QStructureLog(forProperty("logEntry"));
        }
        return logEntry;
    }

}

