package com.solidos.caia.api.common.utils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtHelper {

  @Value("${security.jwt.key.private}")
  private String privateKey;

  @Value("${security.jwt.user.generator}")
  private String userGenerator;

  public String createToken(Authentication auth) {
    Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

    String email = auth.getPrincipal().toString();

    return JWT.create()
        .withIssuer(this.userGenerator)
        .withSubject(email)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
        .withJWTId(UUID.randomUUID().toString())
        .withNotBefore(new Date(System.currentTimeMillis()))
        .sign(algorithm);
  }

  public DecodedJWT validateToken(String token) {

    if (token == null) {
      throw new JWTVerificationException("Not authorized to access this resource");
    }

    try {
      Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

      return JWT.require(algorithm).withIssuer(this.userGenerator).build().verify(token);

    } catch (JWTVerificationException e) {
      throw new JWTVerificationException("Not authorized to access this resourcexdddd");
    }
  }

  public String extractEmail(DecodedJWT decodedJWT) {
    return decodedJWT.getSubject().toString();
  }

  public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName) {
    return decodedJWT.getClaim(claimName);
  }

  public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT) {
    return decodedJWT.getClaims();
  }
}
