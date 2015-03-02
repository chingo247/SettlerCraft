package com.chingo247.settlercraft.model.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QStructureLog is a Querydsl query type for StructureLog
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QStructureLog extends BeanPath<StructureLog> {

    private static final long serialVersionUID = -772459564L;

    public static final QStructureLog structureLog = new QStructureLog("structureLog");

    public final BooleanPath autoremoved = createBoolean("autoremoved");

    public final DateTimePath<java.sql.Timestamp> completedAt = createDateTime("completedAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> removedAt = createDateTime("removedAt", java.sql.Timestamp.class);

    public QStructureLog(String variable) {
        super(StructureLog.class, forVariable(variable));
    }

    public QStructureLog(Path<? extends StructureLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStructureLog(PathMetadata<?> metadata) {
        super(StructureLog.class, metadata);
    }

}

