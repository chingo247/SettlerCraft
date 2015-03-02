package com.chingo247.settlercraft.model.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QPlayerMembershipId is a Querydsl query type for PlayerMembershipId
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QPlayerMembershipId extends BeanPath<PlayerMembershipId> {

    private static final long serialVersionUID = -1859511851L;

    public static final QPlayerMembershipId playerMembershipId = new QPlayerMembershipId("playerMembershipId");

    public final ComparablePath<java.util.UUID> player = createComparable("player", java.util.UUID.class);

    public final NumberPath<Long> structure = createNumber("structure", Long.class);

    public QPlayerMembershipId(String variable) {
        super(PlayerMembershipId.class, forVariable(variable));
    }

    public QPlayerMembershipId(Path<? extends PlayerMembershipId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlayerMembershipId(PathMetadata<?> metadata) {
        super(PlayerMembershipId.class, metadata);
    }

}

