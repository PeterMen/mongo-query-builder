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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * property构造器
 *
 * @author 王海涛
 * @version 1.0
 * @date 2018年02月02日
 */
@Service(value = "PROPERTY")
public class QueryBuilderProperty implements QueryBuilder {

    public static final String PROPERTIES = "properties";

    public static final String PROPERTIES_VALUE = "value";
    public static final String PROPERTIES_KEY = "key";
    public static final String ARRAY = "ARRAY:";

    @Autowired
    private QueryBuilderFQ fq;

    /**
     * 设置默认返回条数
     *
     * @param queryParam   es query
     * @param requestName  请求参数名称
     * @param requestValue 请求参数值,空，则采用默认值
     */
    @Override
    public void buildQuery(QueryParam queryParam, String requestName, String requestValue) {

        if (ObjectUtils.isEmpty(requestValue)) {
            return;
        }
        JSONObject p = JSON.parseObject(requestValue);
        if (p.isEmpty()) {
            return;
        }
        Criteria[] elements = new Criteria[p.size()];
        int index = 0;
        for (Map.Entry<String, Object> key : p.entrySet()) {

            // 过滤空
            if (StringUtils.isEmpty(key.getValue())) continue;
            String propertyValue = p.getString(key.getKey());
            Criteria keyCondition;
            if (propertyValue.startsWith(ARRAY)) {
                // 前缀模糊查询
                Pattern pattern = Pattern.compile("^" + key.getKey() + ".*$", Pattern.CASE_INSENSITIVE);
                keyCondition = Criteria.where(PROPERTIES_KEY).regex(pattern);
                propertyValue = propertyValue.replaceFirst(ARRAY, "");
            } else {
                keyCondition = Criteria.where(PROPERTIES_KEY).is(key.getKey());
            }

            QueryParam qp = new QueryParam();
            fq.buildQuery(qp, PROPERTIES_VALUE, propertyValue);

            keyCondition.andOperator(qp.getCriteriaArray()[0]);

            elements[index++] = Criteria.where(PROPERTIES).elemMatch(keyCondition);
        }

        // 过滤空
        if (index == 0) return;

        queryParam.getCriteriaList().add(new Criteria().andOperator(elements));
    }

    public QueryBuilderProperty setFq(QueryBuilderFQ fq) {
        this.fq = fq;
        return this;
    }
}
