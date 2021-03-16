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
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * FQ构造器基类
 *
 * @author 王海涛
 * @date 2018年02月02日
 * @version 1.0
 */
@Service(value = "FQ")
public class QueryBuilderFQ implements QueryBuilder {

    public static final String EXCLAMATION = "!";
    public static final String COLON_BEHALF = "&colon;";
    public static final String COLON = ":";
    public static final String DATE = "DATE:";
    public static final String INT = "INT:";
    public static final String EMPTY = "";
    public static final String BOOL = "BOOL:";
    public static final String ObjectId = "ObjectId:";

    /**
     * FQ类型枚举
     * */
    public enum FQ_TYPE {
        /**大于*/GT("gt"),
        /**小于*/LT("lt"),
        /**大于等于*/GTE("gte"),
        /**小于等于*/LTE("lte"),
        /**或*/OR("or"),
        /**且*/AND("and"),
        /**区间*/RA("ra"),
        /**左闭右开*/RAL("ral"),
        /**左开右闭*/RAR("rar"),
        /**前缀匹配*/PREFIX("prefix"),
        /**模糊查询*/LIKE("like");

        private String name;

        FQ_TYPE(String name){
            this.name = name;
        }
        /**
         * get set 方法
         * */
        public String getName() {
            return name;
        }
    }
  
    /**
     * query构造器
     * 
     * @param queryParam  query
     * @param paramName 请求参数名称
     * @param paramValue 请求参数值
     * 
     * 规则名 传参格式                   解析后的格式             备注
     * gt   gt:5    {5 TO *}    大于
     * lt   lt:5    {* TO 5}    小于
     * ge   ge:5    [5 TO *]    大于等于
     * le   le:5    [* TO 5]    小于等于
     * or   or:1,10,15  (1 OR10 OR 15)  or
     * an   an:1,10,15  (1 AND 10 AND 15)   and
     * ra   ra:1,100 [1 TO 100]  闭合区间
     * ral  ral:1,100 [1 TO 100 } 左闭右开区间
     * rar  rar:1,100 {1 TO 100 ] 左开右闭区间
     * not  !xxx:23
     * 
     * */
    @Override
    public void buildQuery(QueryParam queryParam, String paramName, String paramValue){

        Criteria criteria = new Criteria();
        queryParam.getCriteriaList().add(criteria);

        // 判斷是否取反
        boolean mustNot = false;
        if(paramValue.startsWith(EXCLAMATION)){
            mustNot = true;
            // 去除感叹号
            paramValue = paramValue.substring(1);
            if(paramValue.startsWith(COLON)){
                // 如果去掉！之后，是冒号开始的，说明没有规则符，需要把冒号删除
                paramValue = paramValue.substring(1);

            }
        }
        int charAt = paramValue.indexOf(COLON);
        if(charAt == -1){
            // 日期格式中，冒号需要转义
            buildNoRule(criteria, paramName, mustNot, paramValue.replaceAll(COLON_BEHALF, COLON));
        } else {

            // 规则名，规则值
            String ruleName = paramValue.substring(0, charAt);
            String ruleVal = paramValue.substring(charAt+1).replaceAll(COLON_BEHALF, COLON);

            // 枚举名称转为枚举类型
            FQ_TYPE fqType = getEnum(ruleName.toUpperCase());

            buildFQ(paramName, paramValue, criteria, mustNot, ruleVal, fqType);
        }
    }

    private void buildFQ(String paramName, String paramValue, Criteria criteria, boolean mustNot, String ruleVal, FQ_TYPE fqType) {
        if(FQ_TYPE.GT == fqType){
            buildGt(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.LT== fqType){
            buildLt(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.GTE== fqType){
            buildGte(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.LTE == fqType){
            buildLte(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.OR == fqType){
            buildOr(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.AND == fqType){
            buildAnd(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.RA == fqType){
            buildRa(criteria, paramName, mustNot, ruleVal, true, true);
        } else if(FQ_TYPE.RAL == fqType){
            buildRa(criteria, paramName, mustNot, ruleVal, true, false);
        } else if(FQ_TYPE.RAR == fqType){
            buildRa(criteria, paramName, mustNot, ruleVal, false, true);
        } else if(FQ_TYPE.PREFIX == fqType){
            buildPrefix(criteria, paramName, mustNot, ruleVal);
        } else if(FQ_TYPE.LIKE == fqType){
            buildLike(criteria, paramName, mustNot, ruleVal);
        } else {
            // 规则名不存在，按无规则方式处理
            buildNoRule(criteria, paramName, mustNot, paramValue.replaceAll(COLON_BEHALF, COLON));
        }
    }

    /**
     * 枚举名称转为枚举类型
     * */
    private FQ_TYPE getEnum(String enumName){
        try{
            return FQ_TYPE.valueOf(enumName);
        } catch (EnumConstantNotPresentException ex){
            return null;
        } catch (IllegalArgumentException ex){
            return null;
        }
    }

    /**
     * 无规则FQ
     * */
    private void buildNoRule(Criteria criteria, String paramName, boolean mustNot, String ruleVal) {

        // 等于
        if(mustNot){
            criteria.norOperator(Criteria.where(paramName).is(dataTypeChange(ruleVal)));
        } else {
            criteria.andOperator(Criteria.where(paramName).is(dataTypeChange(ruleVal)));
        }
    }

    private void buildRa(Criteria criteria, String paramName, boolean mustNot,String ruleVal, boolean left, boolean right) {
        String[] arr = ruleVal.split(Constants.FIELD_SPLIT_STR);
        if (arr.length > 1) {
            Criteria c;
            if(left && right){
                c = Criteria.where(paramName).gte(dataTypeChange(arr[0])).lte(dataTypeChange(arr[1]));
            } else if(left){
                c = Criteria.where(paramName).gte(dataTypeChange(arr[0])).lt(dataTypeChange(arr[1]));
            } else if(right){
                c = Criteria.where(paramName).gt(dataTypeChange(arr[0])).lte(dataTypeChange(arr[1]));
            } else{
                c = Criteria.where(paramName).gt(dataTypeChange(arr[0])).lt(dataTypeChange(arr[1]));
            }
            if(mustNot){
                criteria.norOperator(c);
            } else {
                criteria.andOperator(c);
            }
        }
    }

    private void buildAnd(Criteria criteria, String esName, boolean mustNot,String ruleVal) {

        String[] arr = ruleVal.split(Constants.FIELD_SPLIT_STR);
        if (arr.length > 1) {
            Criteria[] cs = new Criteria[arr.length];
            int index = 0;
            for (String s : arr) {
                cs[index++] = Criteria.where(esName).is(dataTypeChange(s));
            }
            if(mustNot){
                criteria.norOperator(cs);
            } else {
                criteria.andOperator(cs);
            }
        } else {
            if(mustNot){
                criteria.norOperator(Criteria.where(esName).is(dataTypeChange(arr[0])));
            }else {
                criteria.andOperator(Criteria.where(esName).is(dataTypeChange(arr[0])));
            }
        }
    }

    private void buildLike(Criteria criteria, String esName, boolean mustNot, String ruleVal) {

        ruleVal = ruleVal.replace("+", "\\\\+");
        // name需要模糊查询
        Pattern pattern = Pattern.compile("^.*" + ruleVal+".*$", Pattern.CASE_INSENSITIVE);
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).regex(pattern));
        }else {
            criteria.andOperator(Criteria.where(esName).regex(pattern));
        }
    }

    private void buildPrefix(Criteria criteria, String esName, boolean mustNot,String ruleVal) {
        ruleVal = ruleVal.replace("+", "\\\\+");
        // name需要模糊查询
        Pattern pattern = Pattern.compile( "^"+ruleVal+".*$", Pattern.CASE_INSENSITIVE);
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).regex(pattern));
        }else {
            criteria.andOperator(Criteria.where(esName).regex(pattern));
        }
    }

    private void buildOr(Criteria criteria, String esName, boolean mustNot,String ruleVal) {

        // 包含
        String[] arr = ruleVal.split(Constants.FIELD_SPLIT_STR);
        Criteria[] cs = new Criteria[arr.length];
        if (arr.length > 1) {
            int index = 0;
            for (String s : arr) {
                cs[index++] = Criteria.where(esName).is(dataTypeChange(s));
            }
        } else {
            cs[0] = Criteria.where(esName).is(dataTypeChange(ruleVal));
        }

        if(mustNot){
            criteria.norOperator(cs);

        }else {
            criteria.orOperator(cs);
        }
    }

    private void buildLte(Criteria criteria, String esName, boolean mustNot,String ruleVal) {
        // 小于等于
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).lte(dataTypeChange(ruleVal)));
        }else {
            criteria.andOperator(Criteria.where(esName).lte(dataTypeChange(ruleVal)));
        }
    }

    private void buildGte(Criteria criteria, String esName, boolean mustNot,String ruleVal) {
        // 大于等于
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).gte(dataTypeChange(ruleVal)));
        }else {
            criteria.andOperator(Criteria.where(esName).gte(dataTypeChange(ruleVal)));
        }
    }

    private void buildLt(Criteria criteria, String esName, boolean mustNot,String ruleVal) {
        // 小于
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).lt(dataTypeChange(ruleVal)));
        }else {
            criteria.andOperator(Criteria.where(esName).lt(dataTypeChange(ruleVal)));
        }
    }

    private void buildGt(Criteria criteria, String esName, boolean mustNot,String ruleVal) {
        // 大于
        if(mustNot){
            criteria.norOperator(Criteria.where(esName).gt(dataTypeChange(ruleVal)));
        }else {
            criteria.andOperator(Criteria.where(esName).gt(dataTypeChange(ruleVal)));
        }
    }

    /**
     * 根据数据格式来转换数据类型
     * */
    private Object dataTypeChange(String value){
        if(value.startsWith(DATE)){
            try {
                return DateUtils.parseDate(value.replace(DATE, EMPTY), "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e){
                return null;
            }
        } else if(value.startsWith(INT)){
            return Integer.valueOf(value.replace(INT, EMPTY));
        } else if(value.startsWith(BOOL)){
            return Boolean.valueOf(value.replace(BOOL, EMPTY));
        } else if(value.startsWith(ObjectId)){
            return new ObjectId(value.replace(ObjectId, EMPTY));
        }
        return value;
    }
}
