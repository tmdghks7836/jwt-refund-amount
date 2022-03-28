package com.jwt.szs.model.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAvailableSignUpMember is a Querydsl query type for AvailableSignUpMember
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAvailableSignUpMember extends EntityPathBase<AvailableSignUpMember> {

    private static final long serialVersionUID = 748699627L;

    public static final QAvailableSignUpMember availableSignUpMember = new QAvailableSignUpMember("availableSignUpMember");

    public final QBaseDateTime _super = new QBaseDateTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAvailableSignUpMember(String variable) {
        super(AvailableSignUpMember.class, forVariable(variable));
    }

    public QAvailableSignUpMember(Path<? extends AvailableSignUpMember> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAvailableSignUpMember(PathMetadata metadata) {
        super(AvailableSignUpMember.class, metadata);
    }

}

