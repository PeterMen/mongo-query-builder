#说明
该类库实现了从简单的json查询条件转化为MongoDB的查询对象，从而达到简化查询语法的，
极大的降低了关于查询代码的编写难度

使用步骤:
=====
###1): 引入如下包
```

<dependency>
    <groupId>com.peter.mongo.query</groupId>
    <artifactId>mongo-query-builder</artifactId>
    <version>xxx</version>
</dependency>

```
###2): 代码示例
```

            JSONObject queryJson = new JSONObject();
            queryJson.put("name", "王小丽");
            queryJson.put("class", "三年级二班");
            queryJson.put("iStart", 0);
            queryJson.put("iRowSize", 10);
            queryJson.put("include", "type,path,name");
            List resultList = mongoTemplate.find(QueryBuilderFactory.buildSearchRequest(queryJson), XXX.class);

```
###3): 查询语法
参数key示例

|参数名|值类型|说明|
|:----   |:----- |-----   |
|iStart  |Integer |起始行  |
|iRowSize   |Integer |返回条数  ,最大100|
|include  |string |要返回的字段，多个字段用逗号隔开，属性字段的返回控制只能控制第一层级，属性返回字段需要加前缀properties.|
|exclude  |string |不要返回的字段，多个字段用逗号隔开  |
|or  |json/jsonArray ，searchParam| 或逻辑拼接的查询条件  |
|and  |json/jsonArray ，searchParam|and逻辑拼接的查询条件  |
|*  |any | 通用条件，key是自己的业务字段 |
|sort  |string |排序条件，多个排序条件用逗号隔开，如:createTime desc,id asc ,注：不支持属性字段排序  |

**value值示例：** 

|规则|示例|说明|
|:----    |:---|:-----   |
|gt	|gt:5	|大于5	
|lt	|lt:5	|小于5	
|gte|	gte:5|	大于等于5	
|lte|	lte:5|	小于等于5	
|or	|or:1,2,3|	等于1或等于2或等于3	
|and|	and:1,2,3	|等于1且等于2且等于3	
|ra	|ra:1,2	|大于等于1小于等于2	
|ral|	ral:1,2	|大于等于1小于2	
|rar|	rar:1,2	|大于1小于等于2	
|prefix	|prefix:code0	|前缀匹配查询like 'code0%'	
|like	|like:code0	|模糊查询like '%code0%'	
|!	|!:1	 |不等于1

**value类型说明示例：** 

|规则|示例|说明|
|:----    |:--- |-----   |
|DATE	|DATE:2020-12-12 12:12:12	|日期格式数据	|
|INT	|INT:5	|整形数据格式	|
|ARRAY	|ARRAY:5	|如果值是数组，则前缀加“ARRAY:”	|
|BOOL	|BOOL:5	|如果值是布尔类型，则前缀加“BOOL:”	|
|ObjectId	|ObjectId:4551e2x4s	|查询mongo的物理id	|
|-|	5|	字符型数据	|


###4): 传值示例
```
**示例1：**
{
    "include":"sdfs,properties.sdfs",
    "createTime9":"and:23,45",
    "createTime11":"23",
    "createTime10":"or:23,98",
    "iRowSize":10,
    "createTime13":"prefix:23",
    "createTime12":"like:23",
    "sort":"sdf desc",
    "iStart":0,
    "and[1]":[
        {
            "qw":"qw",
            "qw2":"qw"
        }
    ],
    "and[0]":{
        "qw":"qw",
        "qw2":"qw"
    },
    "or[0]":[
        {
            "qw":"qw",
            "qw2":"qw"
        }
    ],
    "or[1]":{
        "qw":"qw",
        "qw2":"qw"
    },
    "createTime5":"ra:23,45",
    "existProperties":"sds,sdsd",
    "createTime6":"rar:23,45",
    "exclude":"sdfsd,properties.sdfsdf",
    "createTime7":"ral:23,56",
    "createTime8":"!:23",
    "createTime1":"gt:INT:23",
    "createTime2":"ra:DATE:2012-12-12 12:12:12,DATE:2012-12-13 12:12:12",
    "createTime3":"gte:23",
    "properties":{
        "qw":"qw",
        "qw2":"ARRAY:qw"
    },
    "createTime4":"lte:23"
}
```
**示例2**
```
{
    "and[1]":{
        "type[0]":""
    },
    "and[0]":{

    },
    "or[0]":{

    },
    "or[1]":{
        "type[0]":""
    },
    "iRowSize":20,
    "iStart":1,
    "lables":"",
    "properties":{
        "year":"",
        "color":""
    }
}
```