package com.intelizign.pl.utils;

import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import com.intelizign.pl.authentication.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils 
{
	Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${intelizign.pl.jwtSecret}")
	private String jwtSecret;

	@Value("${intelizign.pl.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	private Environment environment;

	public String getJwtFromCookies(HttpServletRequest request) 
	{
		Cookie cookie = WebUtils.getCookie(request, "token");
		if (cookie != null) 
		{
			return cookie.getValue();
		} 
		else 
		{
			return null;
		}
	}
	
	public String generateJwtToken(UserDetailsImpl userPrincipal) 
	{
		return generateTokenFromUsername(userPrincipal.getUsername());
	}
	
	public ResponseCookie getCleanJwtCookie() 
	{
		ResponseCookie cookie = ResponseCookie.from("token", null).domain(environment.getProperty("pl.cookies.allow.domain")).path("/").build();
		return cookie;
	}

	public String getUserNameFromJwtToken(String token) 
	{
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken)
	{
		try 
		{
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String generateTokenFromUsername(String username) 
	{
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}
}
