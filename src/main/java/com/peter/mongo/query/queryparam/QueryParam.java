/*
 * The MIT License (MIT)
 * Copyright © 2020 <bz-tech-component>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.peter.mongo.query.queryparam;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * query构造器基类
 *
 * @author wanght
 * @date 2018年02月02日
 * @version 1.0
 */
public class QueryParam {

    /**
     * 起始行
     * */
    Long from;

    /**
     * 返回条数
     * */
    int size = 10;

    /**
     * 查询路由
     * */
    String routing;

    /**
     * 布尔查询条件
     * */
   List<Criteria> criteriaList = new ArrayList<>();

    /**
     * 返回字段信息
     * */
    String[] includes;

    /**
     * 返回字段信息
     * */
    String[] excludes;


    /**
     * 排序信息
     * */
    List<Sort.Order> sortBuilderList = new ArrayList<>();

    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }

    public Query getQuery(){

        Query query = new Query();
        if(getCriteriaArray().length > 0){
            query.addCriteria(new Criteria().andOperator(getCriteriaArray()));
        }
        if(excludes != null){
            for(String s : excludes){
                query.fields().exclude(s);
            }
        }
        if(includes != null){
            for(String s : includes){
                query.fields().include(s);
            }
        }
        if(from != null){
            query.skip(from);
        }
        query.limit(size);
        if(!sortBuilderList.isEmpty()){
            query.with(Sort.by(sortBuilderList));
        }
        return query;
    }
    public Criteria[] getCriteriaArray() {
        return criteriaList.toArray(new Criteria[criteriaList.size()]);
    }

    public void setCriteriaList(List<Criteria> criteriaList) {
        this.criteriaList = criteriaList;
    }

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public List<Sort.Order> getSortBuilderList() {
        return sortBuilderList;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }


}
