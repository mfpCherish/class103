package com.leyou;

import com.leyou.pojo.Person;
import com.leyou.repository.PersonsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SDETest{

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private PersonsRepository personsRepository;

    @Test
    public void testAddIndex(){
        esTemplate.createIndex(Person.class);
    }


    @Test
    public void TestAddMapping(){
        esTemplate.putMapping(Person.class);
    }

    @Test
    public void testSearch(){
        //List<Person> personList=personsRepository.findByName("周杰伦");
       // List<Person> personList=personsRepository.findByCareer("歌手");
        //List<Person> personList=personsRepository.findByAccountBetween(6666.6,9999.9);
        List<Person> personList=personsRepository.findByCareerAndAccountBetween(6666.6,9999.9);

        personList.forEach(person ->
                System.out.println(person));
    }
	
	@Test

	public void fun05(){
        System.out.println("hello 05"));
    }
}
