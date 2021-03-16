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
import com.alibaba.fastjson.JSONObject;
import com.peter.mongo.query.queryparam.QueryParam;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 构造器工厂
 * 目前支持的query构造器 key:Q, FQ, FACET, GEO, MULTIPLE_FQ, SORT, HL, GROUP, FL, EDISMAX, POLYGON, PAGE_START, PAGE_SIZE, DF
 *
 * @author 王海涛
 * @version 1.0
 * @date 2018年02月02日
 */
public class QueryBuilderFactory {

    private static final Map<String, QueryBuilder> queryTypes = new HashMap<>(16);

    public static final String FQ_DEFAULT = "#fq_default#";

    static  {
        QueryBuilderFactory qb = new QueryBuilderFactory();
        QueryBuilderFQ fq = new QueryBuilderFQ();
        queryTypes.put("iStart", new QueryBuilderPageStart());
        queryTypes.put("iRowSize", new QueryBuilderPageSize());
        queryTypes.put("or", new QueryBuilderOR().setQueryBuilderFactory(qb));
        queryTypes.put("and", new QueryBuilderAND().setQueryBuilderFactory(qb));
        queryTypes.put("include", new QueryBuilderInclude());
        queryTypes.put("exclude", new QueryBuilderExclude());
        queryTypes.put("properties", new QueryBuilderProperty().setFq(fq));
        queryTypes.put("existProperties", new QueryBuilderPropertyExist());
        queryTypes.put("sort", new QueryBuilderSort());
        queryTypes.put(FQ_DEFAULT, fq);
    }

    /**
     * 封装mongoDB Query
     *
     * @param paramsJson 调用方传入的参数
     * @return 查询对象构造器
     **/
    public static Query buildSearchRequest( JSONObject paramsJson) {

        // 创建查询参数对象
        QueryParam esQuery = new QueryParam();

        // 循环遍历查询参数，解析query
        buildProvidedQuery(paramsJson, esQuery);

        return esQuery.getQuery();
    }


    /**
     * 构造指定的query
     * boolQuery都用and关系连接
     */
    public static void buildProvidedQuery(JSONObject paramsJson, QueryParam esQuery) {
        if (paramsJson == null) return;
        Iterator<String> keyStr = paramsJson.keySet().iterator();
        while (keyStr.hasNext()) {

            String requestName = keyStr.next();
            // 从容器工厂中获取对应的query构造器，进行query build
            Object requestValueObject = paramsJson.get(requestName);
            String requestValue = paramsJson.getString(requestName);
            if (requestValueObject instanceof List || requestValueObject instanceof Map || requestValueObject instanceof Set) {
                requestValue = JSON.toJSONString(requestValueObject);
            }
            if (StringUtils.isEmpty(requestValue)) continue;

            // 获取ES对应的key名称和query构造器,同名参数用[1/2/3]区分
            requestName = requestName.replaceAll("\\[\\d+\\]", "");
            QueryBuilder baseQueryBuilder = queryTypes.get(requestName);
            if (baseQueryBuilder == null) {
                baseQueryBuilder = queryTypes.get(FQ_DEFAULT);
            }
            baseQueryBuilder.buildQuery(esQuery,  requestName, requestValue);

        }
    }


}
