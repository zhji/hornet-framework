package com.hornetmall.framework.domain.query;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class  QuerySpecification<T> implements Specification<T> {

    private final List<Predicate> predicates = new ArrayList<>();
    private Integer page = 0;
    private Integer size = 20;
    private boolean paged = true;

    public Pageable pageable() {
        return paged ? PageRequest.of(this.page, this.size) : Pageable.unpaged();
    }


    protected abstract void buildPredicates(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder);

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

}
