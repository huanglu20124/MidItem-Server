package com.hl.service.Impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.util.IOUtils;
import com.hl.dao.PersonDao;
import com.hl.dao.RedisDao;
import com.hl.domain.Person;
import com.hl.service.PersonService;
import com.hl.util.Const;
import com.hl.util.IOUtil;

@Service("personService")
public class PersonServiceImpl implements PersonService {

	@Resource(name="personDao")
	private PersonDao personDao;
	
	@Resource(name="redisDao")
	private RedisDao redisDao;
	
	@Override
	public List<Person> getTenPerson(Integer page, String keyword) {
		List<Person>list = null;
		if(page == null){
			page = 0;
		}
		if(keyword == null){
			list = personDao.getTenPerson(page);
		}else {
			//先查找page的0的集合
			List<Person>list_zero = personDao.getTenPersonKeyword(0,keyword);
			if(list_zero.size() ==0){
				//前民按照名字搜索不到，用solr搜索
				list = personDao.solrGetTenPersonKeyword(page,keyword);
			}else {
				if(page == 0){
					list = list_zero;
				}else {
					list = personDao.getTenPersonKeyword(page,keyword);;
				}
			}			
		}
		//将url加上ip
		for(Person person : list){
			String head_url = person.getHead_url();
			String audio_url = person.getAudio_url();
			if(head_url != null){
				person.setHead_url(Const.IP + head_url);
			}
			if (audio_url != null) {
				person.setAudio_url(Const.IP + audio_url);
			}	
		}
		//假如page=0，搜索是否名字就有带keyword的，放到第一位去
		if(page == 0 && list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				Person person = list.get(i);
				if(person.getName().equals(keyword)){
					list.remove(i);
					list.add(0, person);
				}
			}
		}	
		return list;
	}


	@Override
	public Integer addPerson(Person person,HttpServletRequest request){
		String head_url = null;
		String audio_url = null;
		String uuid = person.getUuid();//uuid必须有，由客户端提供
		//先添加文件
		CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getServletContext());
		if (cmr.isMultipart(request)){
			MultipartHttpServletRequest request2 = (MultipartHttpServletRequest) request;
			Iterator<String> files = request2.getFileNames();
			// 获取对象锁
			while (files.hasNext()){
				MultipartFile file = request2.getFile(files.next());
				String file_name = file.getOriginalFilename();
				System.out.println("文件名为" + file_name);
				File temp_file = null;
				//随机名字
				if(file_name.endsWith("jpg")){
					head_url = "head/" + uuid +".jpg";
					temp_file = new File(Const.FILE_PATH + "head/" + uuid+ ".jpg");
					System.out.println("收到图片文件");
				}
				else if(file_name.endsWith("png")){
					head_url = "head/" + uuid +".png";
					temp_file = new File(Const.FILE_PATH + "head/" + uuid + ".png");
					System.out.println("收到图片文件");
				}
				else if(file_name.endsWith("mp3")){
					audio_url = "audio/" + uuid +".mp3";
					temp_file = new File(Const.FILE_PATH + "audio/" + uuid+ ".mp3");
					System.out.println("收到音频文件");
				}
				//保存文件
				try {
					FileOutputStream fos = new FileOutputStream(temp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("保存文件出现异常");
					return null;
				}

			}
		}
		//然后处理文字信息
		person.setHead_url(head_url);
		person.setAudio_url(audio_url);
		//随机分配五行信息
		Integer person_field = new Random().nextInt(5)+1;
		person.setPerson_field(person_field);
		//设置初始变量
		person.setGood(0);
		int num = new Random().nextInt(5)+1;
		person.setAbility(num);
		int key = personDao.addPerson(person);
		person.setPerson_id(key);
		//添加到索引库
		personDao.solrAddPerson(person);
		//最后，根据武力值，录入到redis对应队列里
		redisDao.leftPush("ability"+person.getAbility(), person.getPerson_id().toString());
		System.out.println("添加人物成功");
		return person.getPerson_id();
	}
	

	@Override
	public boolean deletePerson(Integer person_id) {
		//先找到人物
		Person person = personDao.getPersonById(person_id);
		if(person == null){
			return false;
		}else {
			//删除文件
			String head_url = person.getHead_url();
			String audio_url = person.getAudio_url();
			if(head_url != null){
				File file = new File(Const.FILE_PATH + head_url);
				System.out.println("要删除的文件名为="+Const.FILE_PATH + head_url);
				if(file.exists()){
					file.delete();
				}
			}
			if(audio_url != null){
				File file = new File(Const.FILE_PATH + audio_url);
				if(file.exists()){
					file.delete();
				}
			}
			
			personDao.deletePersonById(person_id);
			//删除索引库的
			personDao.solrDeletePersonByUuid(person.getUuid());
			//从武力值队列里删除
			redisDao.removeListIndex("ability"+person.getAbility(), 
					person.getPerson_id().toString());
			//从排行榜中删除
			redisDao.removeRank(person_id);
		}
		
		return true;
	}


	
	@Override
	public Boolean updatePerson(Person person, HttpServletRequest request) {
		String uuid = person.getUuid();//uuid必须有，由客户端提供
		String head_url = person.getHead_url();
		String temp = null;
		if(head_url != null && head_url.startsWith("http")){
			//需要做特殊处理。。。
			temp = head_url.substring(35, head_url.length());
			person.setHead_url(temp);
		}
		String audio_url = person.getAudio_url();
		//先添加文件
		CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getServletContext());
		if (cmr.isMultipart(request)){
			MultipartHttpServletRequest request2 = (MultipartHttpServletRequest) request;
			Iterator<String> files = request2.getFileNames();
			// 获取对象锁
			while (files.hasNext()){
				MultipartFile file = request2.getFile(files.next());
				String file_name = file.getOriginalFilename();
				System.out.println("文件名为" + file_name);
				File temp_file = null;
				if(file_name.endsWith("jpg")){
					head_url = "head/" + uuid +".jpg";
					temp_file = new File(Const.FILE_PATH + "head/" + uuid+ ".jpg");
					System.out.println("收到图片文件");
				}
				else if(file_name.endsWith("png")){
					head_url = "head/" + uuid +".png";
					temp_file = new File(Const.FILE_PATH + "head/" + uuid + ".png");
					System.out.println("收到图片文件");
				}
				else if(file_name.endsWith("mp3")){
					audio_url = "audio/" + uuid +".mp3";
					temp_file = new File(Const.FILE_PATH + "audio/" + uuid+ ".mp3");
					System.out.println("收到音频文件");
				}
				//保存文件
				try {
					//假如原来的文件还存在的话，删除
					if(temp_file.exists()){
						temp_file.delete();
					}
					FileOutputStream fos = new FileOutputStream(temp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("保存文件出现异常");
					return false;
				}

			}
		}
		//然后处理文字信息
		if(temp == null){
			person.setHead_url(head_url);
		}else {
			person.setHead_url(temp);
		}
		person.setAudio_url(audio_url);
		//数据库更新信息
		Boolean flag = personDao.updatePerson(person);
		if(flag == false){
			return false;
		}
		//添加到索引库
		personDao.solrAddPerson(person);
		System.out.println("修改人物成功");
		return true;
	}


		
	@Override
	public boolean addGood(Integer person_id) {
		//人物点赞功能
		//先检查是否有人
		Person person = null;
		if((person = personDao.getPersonById(person_id)) == null){
			return false;
		}else {
			redisDao.addRankNum(person_id);
			personDao.addGoodNum(person_id,person.getGood());
			Integer good = person.getGood();
			person.setGood(good+1);
			//solr也更新
			personDao.solrAddPerson(person);
			return true;
		}
	}


	
	@Override
	public List<Person> getTenRankList(Integer page) {
		List<Integer>person_id_list = redisDao.getRankNum(page);
		List<Person>person_list = new ArrayList<>();
		for(Integer person_id : person_id_list){
			Person person = personDao.getPersonById(person_id);
			person.setHead_url(Const.IP + person.getHead_url());
			person_list.add(person);
		}
		return person_list;
	}

	
}
