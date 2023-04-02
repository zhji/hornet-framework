package com.hornetmall.framework.domain.query;



import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class  QuerySpecification<T>  {

    private final List<BooleanExpression> predicates = new ArrayList<>();

    @Getter
    @Setter
    private Integer page = 0;
    @Getter
    @Setter
    private Integer size = 20;
    @Getter
    @Setter
    private boolean paged = true;

    public Pageable pageable() {
        return paged ? PageRequest.of(this.page, this.size) : Pageable.unpaged();
    }


    public void addExpression(BooleanExpression expression){
        predicates.add(expression);
    }


    protected abstract void buildExpression();


    public BooleanExpression toPredicate() {

        return Expressions.allOf(this.predicates.toArray(new BooleanExpression[this.predicates.size()]));
    }

}
