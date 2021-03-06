package com.pgt.common.controller;

import com.pgt.configuration.Configuration;
import com.pgt.media.bean.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by carlwang on 12/23/15.
 */

@RestController
@RequestMapping("/upload")
public class UploadImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadImageController.class);
    @Autowired
    private Configuration configuration;


    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ModelAndView uploadImage(ModelAndView modelAndView) {
        modelAndView.setViewName("/upload");
        return modelAndView;
    }


    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam(required = false) MultipartFile uploadPicture, MediaType mediaType,
                                                           HttpServletResponse response,
                                                           HttpServletRequest request
    ) throws IOException {
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        if (ObjectUtils.isEmpty(uploadPicture)) {
            LOGGER.debug("The upload image is empty");
            return responseEntity;
        }
        if (ObjectUtils.isEmpty(mediaType)) {
            LOGGER.debug("The media type is null.");
            return responseEntity;
        }
        String originalFilename = uploadPicture.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."), originalFilename.length());
        originalFilename = new Date().getTime() + suffix;
        String filePath = configuration.getImagePath() + configuration.getImageFolder() + originalFilename;
        LOGGER.debug("The file path is {}.", filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        uploadPicture.transferTo(file);
        String imagePath = file.getAbsolutePath();
        LOGGER.debug("The image path is {}.", imagePath);
        Map<String, Object> body = responseEntity.getBody();
        body.put("imagePath", configuration.getImageFolder() + originalFilename);
        body.put("image", file);
        body.put("mediaType", mediaType);

        return responseEntity;
    }
}
