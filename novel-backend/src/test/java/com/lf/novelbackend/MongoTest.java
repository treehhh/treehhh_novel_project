package com.lf.novelbackend;

import com.lf.novelbackend.model.entity.Chapter;
import com.lf.novelbackend.model.entity.Novel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MongoTest {

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {
        Novel novel = new Novel();
        List<Chapter> list = new ArrayList<>();
        Chapter chapter = new Chapter();
        chapter.setTitle("111");
        chapter.setContent("666");
        list.add(chapter);
        list.add(chapter);
        list.add(chapter);
        novel.setChapters(list);
        Novel insert = mongoTemplate.insert(novel);
        System.out.println(insert.getChapters().get(0).getTitle());
    }
}
