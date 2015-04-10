package com.chingo247.structureapi.persistence.legacy;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QDimension is a Querydsl query type for Dimension
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QDimension extends BeanPath<Dimension> {

    private static final long serialVersionUID = 564138943L;

    public static final QDimension dimension = new QDimension("dimension");

    public final NumberPath<Integer> maxX = createNumber("maxX", Integer.class);

    public final NumberPath<Integer> maxY = createNumber("maxY", Integer.class);

    public final NumberPath<Integer> maxZ = createNumber("maxZ", Integer.class);

    public final NumberPath<Integer> minX = createNumber("minX", Integer.class);

    public final NumberPath<Integer> minY = createNumber("minY", Integer.class);

    public final NumberPath<Integer> minZ = createNumber("minZ", Integer.class);

    public QDimension(String variable) {
        super(Dimension.class, forVariable(variable));
    }

    public QDimension(Path<? extends Dimension> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDimension(PathMetadata<?> metadata) {
        super(Dimension.class, metadata);
    }

}

