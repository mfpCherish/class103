package com.leyou.repository;

import com.leyou.pojo.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PersonsRepository extends ElasticsearchRepository<Person,String> {

    List<Person> findByName(String name);

    List<Person> findByCareer(String career);

    List<Person> findByAccountBetween(double v, double v1);

    List<Person> findByCareerAndAccountBetween(double v, double v1);
}
