package com.jwt.szs.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseDateTime is a Querydsl query type for BaseDateTime
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QBaseDateTime extends EntityPathBase<BaseDateTime> {

    private static final long serialVersionUID = -835802372L;

    public static final QBaseDateTime baseDateTime = new QBaseDateTime("baseDateTime");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBaseDateTime(String variable) {
        super(BaseDateTime.class, forVariable(variable));
    }

    public QBaseDateTime(Path<? extends BaseDateTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseDateTime(PathMetadata metadata) {
        super(BaseDateTime.class, metadata);
    }

}

