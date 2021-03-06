package com.bsep.security.config;

import com.bsep.security.auth.XSSRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XSSFilter implements Filter {


	@Override
	public void init(FilterConfig filterConfig) throws ServletException { 
		/*
		 Implementing interface method
		 */
	}

	@Override
	public void destroy() {
		/*
		 Implementing interface method
		 */
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
	}

}
