package com.chingo247.settlercraft.model.entities.world;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QCuboidDimension is a Querydsl query type for CuboidDimension
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QCuboidDimension extends BeanPath<CuboidDimension> {

    private static final long serialVersionUID = 253851749L;

    public static final QCuboidDimension cuboidDimension = new QCuboidDimension("cuboidDimension");

    public final NumberPath<Integer> maxX = createNumber("maxX", Integer.class);

    public final NumberPath<Integer> maxY = createNumber("maxY", Integer.class);

    public final NumberPath<Integer> maxZ = createNumber("maxZ", Integer.class);

    public final NumberPath<Integer> minX = createNumber("minX", Integer.class);

    public final NumberPath<Integer> minY = createNumber("minY", Integer.class);

    public final NumberPath<Integer> minZ = createNumber("minZ", Integer.class);

    public QCuboidDimension(String variable) {
        super(CuboidDimension.class, forVariable(variable));
    }

    public QCuboidDimension(Path<? extends CuboidDimension> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCuboidDimension(PathMetadata<?> metadata) {
        super(CuboidDimension.class, metadata);
    }

}

