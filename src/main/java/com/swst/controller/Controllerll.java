package com.swst.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Auther: fregun
 * @Date: 19-12-23 16:02
 * @Description:
 */
@RequestMapping("/websocket")
@Controller
public class Controllerll {
    @RequestMapping("/socket")

    public String websocket(ModelAndView mav){
        mav.setViewName("/demo/mse.html");
        return "h264";
    }
}
