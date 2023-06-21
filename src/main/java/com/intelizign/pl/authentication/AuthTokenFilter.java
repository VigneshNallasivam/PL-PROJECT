package com.intelizign.pl.authentication;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.intelizign.pl.service.UserDetailsServiceImpl;
import com.intelizign.pl.utils.JwtUtils;

public class AuthTokenFilter extends OncePerRequestFilter 
{

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private Environment env;

	private String token = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException 
	{
		try 
		{
			token = Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("token")).findFirst()
					.map(Cookie::getValue).orElse(null);
			
			String jwt = parseJwt(token);
			if (jwt != null && jwtUtils.validateJwtToken(jwt))
			{
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				// Refresh token
				String newToken = jwtUtils.generateTokenFromUsername(username);

				ResponseCookie tokencookie = ResponseCookie.from("token", newToken).httpOnly(false).secure(true)
						.domain(env.getProperty("pl.cookies.allow.domain")).path("/").maxAge(7 * 24 * 60 * 60).build();

				response.addHeader("Set-Cookie", tokencookie.toString());
			}
		} 
		catch (Exception ex) 
		{
			LOGGER.error("Cannot set User Authentication in filter method" + ex.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(String token) 
	{
		String headerAuth = token;
		if (StringUtils.hasText(headerAuth)) 
		{
			return headerAuth;
		}

		return null;
	}
}
