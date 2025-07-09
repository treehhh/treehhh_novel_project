package com.lf.novelbackend;

import com.lf.novelbackend.model.entity.Novel;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;

@SpringBootTest
public class MongoTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId("686b86424f753d7d460bc883")));
        query.addCriteria(Criteria.where("isDelete").is(0));
        query.addCriteria(Criteria.where("chapters").elemMatch(
                Criteria.where("chapterNumber").is(2)
                        .and("isDelete").is(0)
        ));

        Update update = new Update();
        update.set("chapters.$.isDelete", 0);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Novel.class);
        System.out.println(updateResult.getModifiedCount() > 0);


    }
}
