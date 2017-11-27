package com.hl.dao;

import java.util.List;

import com.hl.domain.Person;

public interface PersonDao {
	public List<Person> getTenPerson(Integer page);

	public List<Person> getTenPersonKeyword(Integer page, String keyword);

	public int addPerson(Person person);

	public Person getPersonById(Integer person_id);

	public void deletePersonById(Integer person_id);

	public List<Person> solrGetTenPersonKeyword(Integer page, String keyword);

	public void solrAddPerson(Person person);

	public void solrDeletePersonByUuid(String uuid);

	public Boolean updatePerson(Person person);

	public void addGoodNum(Integer person_id, Integer good);
}
