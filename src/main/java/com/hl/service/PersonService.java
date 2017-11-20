package com.hl.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hl.domain.Person;

public interface PersonService {

	List<Person> getTenPerson(Integer page, String keyword);

	boolean addPerson(Person person,HttpServletRequest request) throws IOException;

	boolean deletePerson(Integer person_id);

}
