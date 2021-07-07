package com.wzw.gulishop.search;

import com.alibaba.fastjson.JSON;
import com.wzw.gulishop.search.config.GulishopElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class GulishopSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void searchData() throws IOException {
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.1）构建检索条件
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//          sourceBuilder.aggregation();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //1.2）按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);
        //1.3)计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balace");
        sourceBuilder.aggregation(balanceAvg);

        System.out.println(sourceBuilder.toString());
        searchRequest.source(sourceBuilder);


        //2.执行检索
        final SearchResponse searchResponse = client.search(searchRequest, GulishopElasticSearchConfig.COMMON_OPTIONS);
        //3.分析结果searchResponse
        System.out.println(searchResponse.toString());
//        Map map = JSON.parseObject(searchResponse.toString(), Map.class);

        //3.1获取所有查到的数据
        final SearchHits hits = searchResponse.getHits();
        final SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit:searchHits){
            String string = hit.getSourceAsString();
            Account account = JSON.parseObject(string, Account.class);
            System.out.println(account);
        }
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println(keyAsString+"---"+bucket.getDocCount());
        }

        Avg ageAvg1 = aggregations.get("ageAvg");
        System.out.println("ageAvg:"+ageAvg1.getValue());

        Avg balanceAvg1  = aggregations.get("balanceAvg");
        System.out.println("balanceAvg:"+balanceAvg1.getValue());
    }



    @Test
    public void indexData() throws IOException {

        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("userName","zhangsan","age",18);
        User user = new User();
        user.setUserName("zhangsan");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        //执行操作
        IndexResponse index = client.index(indexRequest, GulishopElasticSearchConfig.COMMON_OPTIONS);
        //提取有用的相应数据
        System.out.println(index);


    }


    @Test
    public void contextLoads() {

        System.out.println(client);

    }


    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;

    }


    @ToString
    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }


}
