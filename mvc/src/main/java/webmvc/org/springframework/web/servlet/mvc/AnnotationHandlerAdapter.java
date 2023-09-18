package webmvc.org.springframework.web.servlet.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import webmvc.org.springframework.web.servlet.ModelAndView;
import webmvc.org.springframework.web.servlet.mvc.tobe.HandlerExecution;

public class AnnotationHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(final Object handler) {
        return (handler instanceof HandlerExecution);
    }

    @Override
    public ModelAndView handle(final Object handler, final HttpServletRequest request, final HttpServletResponse response) {
        HandlerExecution handlerExecution = (HandlerExecution) handler;
        return handlerExecution.execute(request, response);
    }
}
