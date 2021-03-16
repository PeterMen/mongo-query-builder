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
package com.peter.mongo.query;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.peter.mongo.query.queryparam.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 复合FQ解析器--OR
 *
 * @author 王海涛
 * @date 2018年02月02日
 * @version 1.0
 */
@Service(value = "MULTIPLE_OR")
public class QueryBuilderOR implements QueryBuilder {

    @Autowired
    QueryBuilderFactory queryBuilderFactory;

    /**
     * 复合FQ解析器：value 格式为json
     * 
     * @param queryParam solr query
     * @param paramName 请求参数名称
     * @param paramValue 请求参数值
     * 
     * */
    @Override
    public void buildQuery(QueryParam queryParam, String paramName, String paramValue){

        if(StringUtils.isEmpty(paramValue)){
            return;
        }

        Criteria c = new Criteria();

        // 默认bool中的条件最少要满足一项，但是假如一项都不满足，就需要用should
        if(paramValue.startsWith(Constants.JSON_ARRAY_PREFIX)){
            // value是数组
            JSONArray values = JSON.parseArray(paramValue);

            // 过滤 空
            if(values.isEmpty()) return;

            Criteria[] criterias = new Criteria[values.size()];
            for(int i = 0; i < values.size(); i++){
                Object p = values.get(i);
                if(p instanceof JSONObject){
                    // 调用构造器工厂进行query build
                    QueryParam subEsQuery = new QueryParam();
                    queryBuilderFactory.buildProvidedQuery((JSONObject)p, subEsQuery);

                    // 过滤空
                    if(CollectionUtils.isEmpty(subEsQuery.getCriteriaList())) continue;

                    criterias[i] = new Criteria().andOperator(subEsQuery.getCriteriaArray());
                }
            }
            c.orOperator(criterias);

        } else {

            // 过滤空
            JSONObject paramValueJson = JSON.parseObject(paramValue);
            if(paramValueJson.isEmpty()) return;

            // 非数组，直接调用构造器工厂进行query build
            QueryParam subEsQuery = new QueryParam();
            queryBuilderFactory.buildProvidedQuery(JSON.parseObject(paramValue), subEsQuery);

            // 过滤空
            if(CollectionUtils.isEmpty(subEsQuery.getCriteriaList())) return;

            c.orOperator(subEsQuery.getCriteriaArray());
        }

        queryParam.getCriteriaList().add(c);
    }

    public QueryBuilderOR setQueryBuilderFactory(QueryBuilderFactory queryBuilderFactory) {
        this.queryBuilderFactory = queryBuilderFactory;
        return this;
    }
}
