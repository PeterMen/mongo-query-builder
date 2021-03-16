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
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 返回字段构造器
 *
 * @author 王海涛
 * @version 1.0
 * @date 2018年02月02日
 */
@Service(value = "INCLUDE")
public class QueryBuilderInclude implements QueryBuilder {


    public static final String PROPERTIES = "properties";

    /**
     * query构造器
     *
     * @param queryParam query
     * @param paramName  请求参数名称
     * @param paramValue 请求参数值
     */
    @Override
    public void buildQuery(QueryParam queryParam, String paramName, String paramValue) {

        if (!ObjectUtils.isEmpty(paramValue)) {

            // 传入指定返回字段
            String[] flFieldArray = paramValue.replaceAll(" ", "").split(Constants.FIELD_SPLIT_STR);

            StringBuilder fieldStr = new StringBuilder();

            boolean includeProperties = false;
            // 转换为ES内部使用的字段名称
            for (String flField : flFieldArray) {
                if (StringUtils.isEmpty(flField)) {
                    continue;
                }
                if (flField.startsWith("properties.")) {
                    includeProperties = true;
                    continue;
                }
                fieldStr.append(flField).append(Constants.FIELD_SPLIT_STR);
            }
            if (includeProperties) {
                fieldStr.append(PROPERTIES).append(Constants.FIELD_SPLIT_STR);
            }

            if (fieldStr.length() == 0) return;
            fieldStr.substring(fieldStr.lastIndexOf(Constants.FIELD_SPLIT_STR), fieldStr.length());
            queryParam.setIncludes(fieldStr.toString().split(Constants.FIELD_SPLIT_STR));

        }
    }
}
