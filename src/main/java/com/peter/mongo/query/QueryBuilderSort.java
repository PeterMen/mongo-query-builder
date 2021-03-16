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


import com.peter.mongo.query.queryparam.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * sort构造器
 *
 * @author 王海涛
 * @version 1.0
 * @date 2018年02月02日
 */
@Service(value = "SORT")
public class QueryBuilderSort implements QueryBuilder {

    /**
     * sort构造器
     *
     * @param queryParam   query
     * @param requestName  请求参数名称
     * @param requestValue 请求参数值,空，则采用默认值
     */
    @Override
    public void buildQuery(QueryParam queryParam, String requestName, String requestValue) {

        // 没有传入排序字段
        if (StringUtils.isNotBlank(requestValue)) {

            addSort(queryParam, requestValue);
        }
    }

    /**
     * 在添加默认排序, 配置文件配置的默认排序字段一律使用es的字段名称
     *
     * @param query   配置文件配置的排序，有一定规则
     * @param sortStr 排序字段
     */
    private void addSort(QueryParam query, String sortStr) {

        String[] sortStrArray = sortStr.split(Constants.FIELD_SPLIT_STR);
        for (String str : sortStrArray) {

            // 获取参数对应的ES使用的字段名称
            String[] strArray = str.trim().split(" ");
            if ("DESC".equals(strArray[1].trim().toUpperCase())) {

                // 降序
                query.getSortBuilderList().add(Sort.Order.desc((strArray[0])));
            } else {

                // 升序
                query.getSortBuilderList().add(Sort.Order.asc((strArray[0])));
            }
        }
    }
}
