package com.leyou.manager;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.leyou.pojo.Person;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ESManager {

    RestHighLevelClient client=null;
    Gson gson=new Gson();

    @Before
    public void init(){
        client=new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1",9201,"http"),
                        new HttpHost("127.0.0.1",9202,"http"),
                        new HttpHost("127.0.0.1",9203,"http")));
    }

    @Test
    public void testDoc() throws IOException {
        Person p1=new Person("1","周杰伦","歌手","晴天",9999.0,"杰伦最棒");
        //IndexRequest是专门用于插入索引数据的对象
        IndexRequest request = new IndexRequest("person", "docs", p1.getId());
        //将要插入索引的对想转换为JSON
        String jsonString = JSON.toJSONString(p1);
        request.source(jsonString, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    public void testBulkAdd() throws IOException {
        List<Person> list = new ArrayList<>();
        list.add(new Person("2","柏原崇","演员","情书",3966.0,"好帅"));
        list.add(new Person("3","邓丽君","歌手","南海姑娘",4996.0,"好温柔"));
        list.add(new Person("4","王力宏","歌手","十二生肖",8666.0,"哈哈哈"));
        list.add(new Person("5","王菲","歌手","清风徐来",6166.0,"幽灵"));
        list.add(new Person("6","江直树","医生","手术",1666.0,"学霸"));
        list.add(new Person("7","艾玛","作家","one day",7999.0,"小众"));

        BulkRequest request = new BulkRequest();
        list.forEach(person ->{
            IndexRequest indexRequest = new IndexRequest("person", "docs", person.getId());
            String toJson = gson.toJson(person);
            indexRequest.source(toJson,XContentType.JSON);
            request.add(indexRequest);
        });
        client.bulk(request,RequestOptions.DEFAULT);
    }

    @Test
    public void testDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("person", "docs", "4");
        client.delete(request,RequestOptions.DEFAULT);
    }

    @Test
    public void testSearch() throws IOException {
        //1. 根据我们要查询的索引库构建索引库查询对象request
        SearchRequest searchRequest = new SearchRequest("person").types("docs");
        //2. 构建查询对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //3. 根据查询方式构建builder
        //3.1 查询所有
        //searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //searchSourceBuilder.from(0);
        //searchSourceBuilder.size(10);
        //searchSourceBuilder.sort("account", SortOrder.DESC);
        //3.2 term查询--注意这种查询不会进行查询词的分词,但是被查询的词歌手是被分词的,所以使用歌手查不到,使用分词查应该能
        //searchSourceBuilder.query(QueryBuilders.termQuery("career","歌"));
        //searchSourceBuilder.query(QueryBuilders.termQuery("career","歌手"));
        //searchSourceBuilder.postFilter(QueryBuilders.termQuery("work","晴天"));
        //3.3 分词查询
        //searchSourceBuilder.query(QueryBuilders.matchQuery("des","好温柔"));
        //searchSourceBuilder.fetchSource(new String[]{"name","career","work"},new String[]{"id","account","des"});
        //searchSourceBuilder.fetchSource(new String[]{"name","career","work"},null);
        //3.4 模糊查询
        //searchSourceBuilder.query(QueryBuilders.wildcardQuery("des","*柔*"));
        //3.5 容错查询
        //searchSourceBuilder.query(QueryBuilders.fuzzyQuery("name","江植树"));
        //3.6 范围
        //searchSourceBuilder.query(QueryBuilders.rangeQuery("account").lte(9999.1).gte(4999.0));
        //3.7 设置高亮
        /*HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);*/

        //3.8 聚合,注意高亮以及聚合的API借助hibana中的查询效果会比较明确
        searchSourceBuilder.aggregation(AggregationBuilders.terms("careerCount").field("career"));



        //4. 将builder放到request中
        searchRequest.source(searchSourceBuilder);

        //5. 执行查询
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //当需要显示聚合效果的时候,下面6,7内容都不需要
        //6. 接收返回值
        //SearchHits responseHits = searchResponse.getHits();
        //System.out.println("总记录数是:"+responseHits.getTotalHits());
        //7. 解析返回值
        //SearchHit[] searchHits = responseHits.getHits();
        //for (SearchHit searchHit : searchHits) {

            //将JSON转为string
            //String jsonString = searchHit.getSourceAsString();
            ////将String转成实体类对象
            //Person person = gson.fromJson(jsonString, Person.class);

            //获取高亮结果---有错
           /* Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            HighlightField highlightField = highlightFields.get("name");
            Text[] fragments = highlightField.getFragments();
            if(fragments!=null&&fragments.length>0){
                String name = fragments[0].toString();
                person.setName(name);
            }*/


           //获取聚合结果
            Aggregations aggregations = searchResponse.getAggregations();
            Terms terms=aggregations.get("careerCount");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            buckets.forEach(bucket->{
                System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
            });
            //System.out.println(person);
        }


    @After
    public void end() throws IOException {
        client.close();
    }

}
