package com.techcourse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.mvc.HandlerAdapter;
import nextstep.mvc.controller.asis.Controller;
import nextstep.mvc.view.JspView;
import nextstep.mvc.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualHandlerAdapter implements HandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ManualHandlerAdapter.class);
    @Override
    public boolean supports(Object handler) {
        return handler instanceof Controller;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Controller controller = (Controller)handler;
        try {
            String view = controller.execute(request, response);
            return new ModelAndView(new JspView(view));
        } catch (Exception e) {
            log.error("controller:{}", controller);
            throw new RuntimeException("execute를 실행하는데 실패했습니다.");
        }
    }
}