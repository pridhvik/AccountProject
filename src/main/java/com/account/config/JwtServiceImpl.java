package com.account.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtServiceImpl {

	private String secretKey;
	private long jwtExpiration;

	public JwtServiceImpl(@Value("${security.jwt.secret-key}") String secretKey,
			@Value("${security.jwt.expiration-time}") long jwtExpiration) {
		this.secretKey = secretKey;
		this.jwtExpiration = jwtExpiration;
	}

	public long getExpirationTime() {
		return jwtExpiration;
	}

	private Key getSignInKey() {
		byte[] decode = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(decode);
	}

	public String extractUserName(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJwt(token).getBody();
		return claims.getSubject();
	}

	// build token
	public String buildToken(UserDetails user) {
		String token = Jwts.builder().setClaims(new HashMap<String, Object>()).setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
		return token;
	}

	// validate token
	public boolean isTokenValid(String token, UserDetails user) {
		Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
		return claims.getSubject().equals(user.getUsername()) && claims.getExpiration().before(new Date());
	}

}
