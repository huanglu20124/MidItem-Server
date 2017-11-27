package com.hl.dao.Impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hl.dao.PersonDao;
import com.hl.domain.Person;

public class PersonDaoImpl extends JdbcDaoSupport implements PersonDao{

	@Resource(name="solrServer")
	private SolrServer solrServer;
	
	class PersonRowmapper implements RowMapper<Person>{

		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			Person person = new Person();
			person.setName(rs.getString("name"));
			person.setSex(rs.getString("sex"));
			person.setPerson_date(rs.getString("person_date"));
			person.setAbility(rs.getInt("ability"));
			person.setAudio_url(rs.getString("audio_url"));
			person.setCountry(rs.getString("country"));
			person.setGood(rs.getInt("good"));
			person.setHometown(rs.getString("hometown"));
			person.setPerson_id(rs.getInt("person_id"));
			person.setHead_url(rs.getString("head_url"));
			person.setSecond_name(rs.getString("second_name"));
			person.setUuid(rs.getString("uuid"));
			person.setDescription(rs.getString("description"));
			person.setPerson_field(rs.getInt("person_field"));
			return person;
		}
		
	}
	
	@Override
	public List<Person> getTenPerson(Integer page) {
		String sql = "select * from person LIMIT ?,10";
		return getJdbcTemplate().query(sql, new PersonRowmapper(),page*10);
	}

	
	@Override
	public List<Person> getTenPersonKeyword(Integer page, String keyword) {
		String sql = "select * from person where name LIKE '%"+ keyword +"%' LIMIT ?,10";
		return getJdbcTemplate().query(sql, new PersonRowmapper(),page*10);
	}


	@Override
	public int addPerson(final Person person) {
		//返回主键
		final String sql = "insert into person values(null,?,?,?,?,"
				+ " ?,?,?,?,?,"
				+ " ?,?,?,?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement psm = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				psm.setString(1, person.getName());
				psm.setString(2, person.getSex());
				psm.setString(3, person.getCountry());
				psm.setString(4, person.getPerson_date());
				psm.setString(5, person.getHometown());
				psm.setString(6, person.getDescription());
				psm.setInt(7, person.getAbility());
				psm.setInt(8, person.getGood());
				psm.setString(9, person.getHead_url());
				psm.setString(10, person.getAudio_url());
				psm.setString(11, person.getSecond_name());
				psm.setString(12, person.getUuid());
				psm.setInt(13, person.getPerson_field());
				return psm;
			}
		},keyHolder);
/*		getJdbcTemplate().update(sql,person.getName(),person.getSex(),person.getCountry(),person.getPerson_date(),
				person.getHometown(),person.getDescription(),person.getAbility(),person.getGood(),
				person.getHead_url(),person.getAudio_url(),person.getSecond_name(),
				person.getUuid(),person.getPerson_field(),keyHolder);*/
		return keyHolder.getKey().intValue();		
	}


	@Override
	public Person getPersonById(Integer person_id) {
		String sql = "select * from person where person_id = ?";
		return getJdbcTemplate().queryForObject(sql, new PersonRowmapper(),person_id);
	}


	@Override
	public void deletePersonById(Integer person_id) {
		String sql = "delete from person where person_id = ?";
		getJdbcTemplate().update(sql,person_id);
	}


	@Override
	public List<Person> solrGetTenPersonKeyword(Integer page, String keyword) {
		System.out.println("page=" + page + " keyword=" + keyword);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(keyword);
		//设置默认域
		solrQuery.set("df", "person_keyword");
		solrQuery.setStart(page * 10);
		solrQuery.setRows(10);
		//得到结果
		QueryResponse response;
		try {
			response = solrServer.query(solrQuery);
		} catch (SolrServerException e) {
			System.out.println("连接搜索引擎出错");
			e.printStackTrace();
			return null;
		}
		// 文档结果集
		SolrDocumentList docs = response.getResults();
		System.out.println("记录条数为=" + docs.getNumFound());
		List<Person>list = new ArrayList<>();
		for(SolrDocument document : docs){
			Person person = new Person();
			person.setName((String) document.get("person_name"));
			person.setSex((String) document.get("sex"));
			person.setPerson_id((Integer) document.get("person_id"));
			person.setUuid((String) document.get("id"));
			person.setCountry((String) document.get("country"));
			person.setPerson_date((String) document.get("person_date"));
			person.setAbility((Integer) document.get("ability"));
			person.setGood((Integer) document.get("good"));
			person.setHead_url((String) document.get("head_url"));
			person.setAudio_url((String) document.get("audio_url"));
			person.setHometown((String) document.get("hometown"));
			person.setDescription((String) document.get("person_description"));
			person.setSecond_name((String) document.get("second_name"));
			list.add(person);
		}
		return list;
	}


	
	@Override
	public void solrAddPerson(Person person) {
		//添加到索引库
		SolrInputDocument document = new SolrInputDocument();
		document.setField("person_name", person.getName());
		document.setField("sex", person.getSex());
		document.setField("country", person.getCountry());
		document.setField("person_date", person.getPerson_date());
		document.setField("person_id", person.getPerson_id());
		document.setField("ability", person.getAbility());
		document.setField("good", person.getGood());
		document.setField("person_description", person.getDescription());
		document.setField("hometown", person.getHometown());
		document.setField("second_name", person.getSecond_name());
		document.setField("id", person.getUuid());
		document.setField("head_url", person.getHead_url());
		document.setField("audio_url", person.getAudio_url());
		document.setField("person_field", person.getPerson_field());
		try {
			solrServer.add(document);
			solrServer.commit();
			System.out.println("成功添加到索引库");
		} catch (SolrServerException e) {
			System.out.println("添加到索引库失败");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void solrDeletePersonByUuid(String uuid) {
		if(uuid != null){
			try {
				solrServer.deleteByQuery("id:" + uuid ,1000);
			} catch (SolrServerException e) {
				System.out.println("删除索引失败");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("删除索引失败");
				e.printStackTrace();
			}
		}
	}


	
	@Override
	public Boolean updatePerson(Person person){
		String sql = "update person set name=?, sex=?,country=?,person_date=?,hometown=?,"
				+ " description=?, ability=?, good=?, head_url=?, audio_url=?, "
				+ " second_name=? where person_id=?";
		try {
			getJdbcTemplate().update(sql,person.getName(), person.getSex(), person.getCountry(),
					 person.getPerson_date(), person.getHometown(),
					 person.getDescription(), person.getAbility(), person.getGood(),person.getHead_url(), person.getAudio_url(),
					 person.getSecond_name(), person.getPerson_id());
		} catch (Exception e) {
			return false;
		}
		return true;
		
	}


	
	@Override
	public void addGoodNum(Integer person_id, Integer good) {
		String sql = "update person set good = ? where person_id = ?";
		getJdbcTemplate().update(sql,good+1, person_id);
		
	}


	

}
