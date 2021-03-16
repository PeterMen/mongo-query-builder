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
import org.junit.Test;

public class QueryBuilderTest {

    private QueryBuilderFactory qb = new QueryBuilderFactory();

    @Test
    public void testQueryBuilder(){

        String searchParam = "{\n" +
                "        \"iStart\":0,\n" +
                "        \"iRowSize\":10,\n" +
                "        \"include\":\"sdfs,properties.sdfs\",\n" +
                "        \"exclude\":\"sdfsd,properties.sdfsdf\",\n" +
                "        \"createTime1\":\"gt:INT:23\",\n" +
                "        \"createTime2\":\"lt:DATE:2012-12-12 12:12:12\",\n" +
                "        \"createTime2\":\"ra:DATE:2012-12-12 12:12:12,DATE:2012-12-13 12:12:12\",\n" +
                "        \"createTime3\":\"gte:23\",\n" +
                "        \"createTime4\":\"lte:23\",\n" +
                "        \"createTime5\":\"ra:23,45\",\n" +
                "        \"createTime6\":\"rar:23,45\",\n" +
                "        \"createTime7\":\"ral:23,56\",\n" +
                "        \"createTime8\":\"!:23\",\n" +
                "        \"createTime9\":\"and:23,45\",\n" +
                "        \"createTime10\":\"or:23,98\",\n" +
                "        \"createTime11\":\"23\",\n" +
                "        \"createTime12\":\"like:23\",\n" +
                "        \"createTime13\":\"prefix:23\",\n" +
                "        \"properties\":{\n" +
                "            \"qw\":\"qw\",\n" +
                "            \"qw2\":\"ARRAY:qw\"\n" +
                "        },\n" +
                "        \"or[1]\":{\n" +
                "            \"qw\":\"qw\",\n" +
                "            \"qw2\":\"qw\"\n" +
                "        },\n" +
                "        \"or[0]\":[{\n" +
                "            \"qw\":\"qw\",\n" +
                "            \"qw2\":\"qw\"\n" +
                "        }],\n" +
                "        \"and[0]\":{\n" +
                "            \"qw\":\"qw\",\n" +
                "            \"qw2\":\"qw\"\n" +
                "        },\n" +
                "        \"and[1]\":[{\n" +
                "            \"qw\":\"qw\",\n" +
                "            \"qw2\":\"qw\"\n" +
                "        }],\n" +
                "        \"existProperties\":\"sds,sdsd\",\n" +
                "        \"sort\":\"sdf desc\"\n" +
                "    }";

        System.out.println(qb.buildSearchRequest(JSON.parseObject(searchParam)));
    }

    @Test
    public void testQueryBuilder2(){

        String searchParam = "{\n" +
                "        \"opDomain\":\"gap\",\n" +
                "        \"iStart\":1,\n" +
                "        \"iRowSize\":20,\n" +
                "        \"and[0]\":{\n" +
                "\n" +
                "        },\n" +
                "        \"and[1]\":{\n" +
                "            \"type[0]\":\"\"\n" +
                "        },\n" +
                "        \"or[0]\":{\n" +
                "\n" +
                "        },\n" +
                "        \"or[1]\":{\n" +
                "            \"type[0]\":\"\"\n" +
                "        },\n" +
                "        \"lables\":\"\",\n" +
                "        \"properties\":{\n" +
                "            \"year\":\"\",\n" +
                "            \"materialType\":\"\"\n" +
                "        }\n" +
                "    }";

        System.out.println(qb.buildSearchRequest(JSON.parseObject(searchParam)));
    }

    @Test
    public void testQueryBuilder3(){

        String searchParam = "{\n" +
                "        \"opDomain\":\"anf\",\n" +
                "        \"iStart\":1,\n" +
                "        \"iRowSize\":20,\n" +
                "        \"path\":\"like:/Spring 2/\"\n" +
                "    }";

        System.out.println(qb.buildSearchRequest(JSON.parseObject(searchParam)));

        String ruleVal = "sdfsdfs&+ sdfsdf";
        ruleVal = ruleVal.replace("+", "\\\\+");
        System.out.println(ruleVal);
    }
}
