package com.pgt.help.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pgt.constant.Constants;
import com.pgt.help.bean.HelpCategoryVo;
import com.pgt.help.bean.HelpCenter;
import com.pgt.help.service.HelpCenterService;

@RestController
@RequestMapping("/helpcenter")
public class HelpCenterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelpCenterController.class);

	@Autowired
	private HelpCenterService helpCenterService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ModelAndView queryAllHelpCenter(ModelAndView modelAndView) {

		List<HelpCategoryVo> HelpCategorVoList = helpCenterService.findAllHelpCategoryVo();
		modelAndView.addObject("helpCategorVoList", HelpCategorVoList);
		modelAndView.setViewName(Constants.HELP_CENTER);
		HelpCenter helpCenter= helpCenterService.findHelpCenterById(Integer.parseInt("1"));
		modelAndView.addObject("helpCenter",helpCenter);

		return modelAndView;
	}


	@RequestMapping(value = "/{helpCenterId}",method = RequestMethod.GET)
	public ModelAndView queryHelpCenterById(ModelAndView modelAndView, @PathVariable("helpCenterId") String helpCenterId){
		if(helpCenterId==null){
            helpCenterId="1";
		}
		List<HelpCategoryVo> HelpCategorVoList = helpCenterService.findAllHelpCategoryVo();
		modelAndView.addObject("helpCategorVoList", HelpCategorVoList);
		modelAndView.setViewName(Constants.HELP_CENTER);

		HelpCenter helpCenter= helpCenterService.findHelpCenterById(Integer.parseInt(helpCenterId));
		modelAndView.addObject("helpCenter",helpCenter);

		return modelAndView;
	}


    //添加一个帮助信息
	@RequestMapping(value = "/insert",method=RequestMethod.GET)
    public ModelAndView insertHelCenter(ModelAndView modelAndView, HelpCenter helpCenter){
		if(ObjectUtils.isEmpty(helpCenter.getContent())){
			LOGGER.debug("helpcenter content is null");
			return modelAndView;
		}
		if(ObjectUtils.isEmpty(helpCenter.getTitle())){
			LOGGER.debug("Title title is null");
			return modelAndView;
		}
		helpCenterService.createHelpCenter(helpCenter);
		modelAndView.setViewName("");
		return modelAndView;
	}
	
	@RequestMapping(value = "update/{helpCenterId}",method = RequestMethod.GET)
	public ModelAndView updateHelpCenter(ModelAndView modelAndView, @PathVariable("helpCenterId") String helpCenterId){
		if(helpCenterId==null){
            helpCenterId="1";
		}
		List<HelpCategoryVo> HelpCategorVoList = helpCenterService.findAllHelpCategoryVo();
		modelAndView.addObject("helpCategorVoList", HelpCategorVoList);
		modelAndView.setViewName("/help/editHelpcenter");

		HelpCenter helpCenter= helpCenterService.findHelpCenterById(Integer.parseInt(helpCenterId));
		modelAndView.addObject("helpCenter",helpCenter);

		return modelAndView;
	}
	
	@RequestMapping(value = "/update",method = RequestMethod.POST)
	public ModelAndView updateHlepCenter(HelpCenter helpCenter){
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:update/"+helpCenter.getId());
		if(ObjectUtils.isEmpty(helpCenter.getContent())){
			LOGGER.debug("helpcenter content is null");
			return mav;
		}
		if(ObjectUtils.isEmpty(helpCenter.getTitle())){
			LOGGER.debug("Title title is null");
			return mav;
		}
		helpCenterService.updateHelpCenter(helpCenter);
		return mav;
	}


	//删除一个帮助信息
	public ModelAndView deleteHelpCenterById(Integer helpCenterid,ModelAndView modelAndView){

		helpCenterService.deleteHelpCenterById(helpCenterid);
        return modelAndView;

	}


}
