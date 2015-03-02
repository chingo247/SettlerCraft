package com.chingo247.settlercraft.model.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPlayerMembership is a Querydsl query type for PlayerMembership
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPlayerMembership extends EntityPathBase<PlayerMembership> {

    private static final long serialVersionUID = -574001382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlayerMembership playerMembership = new QPlayerMembership("playerMembership");

    public final StringPath name = createString("name");

    protected QPlayerMembershipId playerMembershipId;

    protected QStructure structure;

    public final ComparablePath<java.util.UUID> uuid = createComparable("uuid", java.util.UUID.class);

    public QPlayerMembership(String variable) {
        this(PlayerMembership.class, forVariable(variable), INITS);
    }

    public QPlayerMembership(Path<? extends PlayerMembership> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerMembership(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPlayerMembership(PathMetadata<?> metadata, PathInits inits) {
        this(PlayerMembership.class, metadata, inits);
    }

    public QPlayerMembership(Class<? extends PlayerMembership> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.playerMembershipId = inits.isInitialized("playerMembershipId") ? new QPlayerMembershipId(forProperty("playerMembershipId")) : null;
        this.structure = inits.isInitialized("structure") ? new QStructure(forProperty("structure"), inits.get("structure")) : null;
    }

    public QPlayerMembershipId playerMembershipId() {
        if (playerMembershipId == null) {
            playerMembershipId = new QPlayerMembershipId(forProperty("playerMembershipId"));
        }
        return playerMembershipId;
    }

    public QStructure structure() {
        if (structure == null) {
            structure = new QStructure(forProperty("structure"));
        }
        return structure;
    }

}

