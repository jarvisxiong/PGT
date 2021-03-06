package com.pgt.base.controller;

import java.io.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.Addressing;

import com.pgt.configuration.Configuration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pgt.utils.ResponseUtils;

import liquibase.util.file.FilenameUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 公共上传图片 异步上传 Created by ddjunshi 2015年11月24日
 */
@RequestMapping("/upload")
@RestController
public class UploadController {

	@Autowired
	private Configuration configuration;

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

	@RequestMapping(value = "/uploadPic")
	public void uploadPic(@RequestParam(required = false) MultipartFile uploadPic, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		JSONObject jo = new JSONObject();
		if(ObjectUtils.isEmpty(uploadPic)){
			LOGGER.debug("The upload image is empty.");
			ResponseUtils.renderJson(response, "uploda is null");
			return;
		}



		// 精确到毫秒
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String picName = df.format(new Date());
		Random r = new Random();
		for (int i = 0; i < 3; i++) {
			picName += r.nextInt(10);
		}
		// 获取扩展名
		String originalFilename = uploadPic.getOriginalFilename();
		String ext = FilenameUtils.getExtension(originalFilename);
		String basePath = configuration.getImagePath()+configuration.getImageFolder();
		LOGGER.debug("The file basePath is {}.", basePath);


		// 相对路径
		String path = picName + "." + ext;
		// 全路径
		String url = basePath +path;



		LOGGER.debug("The file url is {}.", url);
		// 新图片
		File file = new File(url);
		if(!file.exists()){
			file.mkdirs();
		}
		// 将内存中的文件写入磁盘
		uploadPic.transferTo(file);
		LOGGER.debug("The file absolutePath is {}.",file.getAbsolutePath());
		url= url.substring(url.indexOf("image"),url.length());

		jo.put("url", url);
		jo.put("path", path);
		ResponseUtils.renderJson(response, jo.toString());

	}

	@RequestMapping(value = "/uploadPicByPhone")
	public void uploadPicByPhone(HttpServletRequest request,HttpServletResponse response) {

        //接收上传的文件
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = mr.getFileMap();
		Set<Map.Entry<String, MultipartFile>> entrySet = fileMap.entrySet();

		JSONObject jo = new JSONObject();

		for (Map.Entry<String, MultipartFile> entry : entrySet) {

			//上传上来的图片
			MultipartFile pic = entry.getValue();
			//扩展名
			String ext = FilenameUtils.getExtension(pic.getOriginalFilename());
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String picName = df.format(new Date());
			Random r = new Random();
			for (int i = 0; i < 3; i++) {
				picName += r.nextInt(10);
			}
			String basePath = configuration.getImagePath()+configuration.getImageFolder();
			LOGGER.debug("The file basePath is {}.", basePath);
			File filemkdir = new File(basePath);
			if(!filemkdir.exists()){
				filemkdir.mkdirs();
			}

			// 相对路径
			String path = picName + "." + ext;
			// 全路径
			String url = basePath +path;
			LOGGER.debug("The file url is {}.", url);
			// 新图片
			File file = new File(url);

			try {
				// 将内存中的文件写入磁盘
				InputStream in = pic.getInputStream();
				int len =0;
				FileOutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				while((len=in.read(buffer))>0){
					out.write(buffer,0,len);
				}
				in.close();
				out.close();

			}catch(Exception e){
				e.printStackTrace();
			}

			LOGGER.debug("The file absolutePath is {}.",file.getAbsolutePath());
			url = url.substring(url.indexOf("image"), url.length());

			jo.put("url", url);
			jo.put("path", path);

		}
		 ResponseUtils.renderJson(response, jo.toString());
	}

}
