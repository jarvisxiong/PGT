package com.pgt.aboutus;


import com.pgt.city.bean.Province;
import com.pgt.city.service.CityService;
import com.pgt.configuration.URLConfiguration;
import com.pgt.constant.UserConstant;
import com.pgt.user.bean.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by liqiang on 16-2-18.
 *
 * about-us
 */
@Controller
@RequestMapping("/about-us")
public class AboutUsController {
    private static final Logger LOGGER	= LoggerFactory.getLogger(AboutUsController.class);
    @Autowired
    private URLConfiguration urlConfiguration;

    /**
     * add address method
     * @param modelAndView
     * @return
     */
    @RequestMapping(value = "main")
    public ModelAndView addAddressPage(ModelAndView modelAndView){
        modelAndView.setViewName("about-us/about-us");
        LOGGER.debug("success and go to about-us/");
        return modelAndView;
    }
}
