package com.chingo247.settlercraft.model.entities;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSchematicEntity is a Querydsl query type for SchematicEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSchematicEntity extends EntityPathBase<SchematicEntity> {

    private static final long serialVersionUID = 51724069L;

    public static final QSchematicEntity schematicEntity = new QSchematicEntity("schematicEntity");

    public final NumberPath<Long> checksum = createNumber("checksum", Long.class);

    public final NumberPath<Integer> s_height = createNumber("s_height", Integer.class);

    public final NumberPath<Integer> s_length = createNumber("s_length", Integer.class);

    public final NumberPath<Integer> s_width = createNumber("s_width", Integer.class);

    public QSchematicEntity(String variable) {
        super(SchematicEntity.class, forVariable(variable));
    }

    public QSchematicEntity(Path<? extends SchematicEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchematicEntity(PathMetadata<?> metadata) {
        super(SchematicEntity.class, metadata);
    }

}

