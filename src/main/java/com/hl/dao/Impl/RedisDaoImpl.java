package com.hl.dao.Impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.util.Hash;
import org.springframework.data.redis.core.RedisTemplate;

import com.hl.dao.RedisDao;
import com.hl.util.Const;

public class RedisDaoImpl implements RedisDao {
	
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void leftPush(String array_name,String uuid){
		redisTemplate.opsForList().leftPush(array_name, uuid);
	}
	
	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void addKey(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}


	@Override
	public String pop(String array_name) {
		//从右边弹出队列,同时返回
		return (String) redisTemplate.opsForList().rightPop(array_name);
	}

	@Override
	public Object getValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	
	@Override
	public String getRight(String array_name,Long size) {
		return (String) redisTemplate.opsForList().index(array_name, size -1);
	}

	@Override
	public List<Object> getRangeId(String array_name) {
		//得到整个队列
		return redisTemplate.opsForList().range(array_name, 0l, -1l);
	}

	@Override
	public void deleteKey(String key) {
		//删除key
		redisTemplate.delete(key);
	}
	
	@Override
	public void removeListIndex(String array_name,String uuid){
		//删除对应uuid的元素
		redisTemplate.opsForList().remove(array_name, 1, uuid);
	}
	
	@Override
	public void addSelf(String key){
		redisTemplate.opsForValue().increment(key, 1);
	}

	
	@Override
	public void addRankNum(Integer person_id) {
		redisTemplate.opsForZSet().incrementScore("good_rank", person_id.toString(), Double.valueOf("1"));
	}
	
	@Override
	public List<Integer> getRankNum(Integer page){
		Long start = new Integer(page*10).longValue();
		Long end = start + 10;
		Set<Object>set = null;
		try {
			set = redisTemplate.opsForZSet().reverseRange("good_rank", start, end);	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		List<Integer>person_id_list = new ArrayList<>();
		Iterator<Object>iterator = set.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			person_id_list.add(new Integer((String) object));
		}
		return person_id_list;
	}

	
	@Override
	public void removeRank(Integer person_id) {
		redisTemplate.opsForZSet().remove("good_rank", person_id.toString());
		
	}
}
