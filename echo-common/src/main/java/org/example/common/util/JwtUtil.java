package org.example.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.example.common.enums.BizCodeEnum;
import org.example.common.exception.BusinessException;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;

@Slf4j
public class JwtUtil {

    private static final RSAPublicKey rsaPublicKey = RSAUtil.getPublicKey("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBUyRDjqMirzCDGDxofwVlZEX3bqION6k0Ntb8RqqpqMRY060G/pbQjPi4FrbUqWbLGkRIrVx6nQSwIPYcl/waE2Ode58g/EfXM4Eja1U5zLqr8pRmybf5ZASpHP+7ktgg/EivaVP79uqaLAPwpWdkzMPEkhf6/ZyCHrZrlJazkQIDAQAB");

    private static final RSAPrivateKey rsaPrivateKey = RSAUtil.getPrivateKey("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIFTJEOOoyKvMIMYPGh/BWVkRfduog43qTQ21vxGqqmoxFjTrQb+ltCM+LgWttSpZssaREitXHqdBLAg9hyX/BoTY517nyD8R9czgSNrVTnMuqvylGbJt/lkBKkc/7uS2CD8SK9pU/v26posA/ClZ2TMw8SSF/r9nIIetmuUlrORAgMBAAECgYA1PanFyECgBFin97/S00xA6C2nxlugF8kojlUmid8ztp+S5B4tLX2gQREaFHvThCQmBFvNQdW7Qs5Nxlwb/HQUHvpR3lvWF/YpzqDn/BfwbhCR1AiES22yE/HtqIy5HaXolm6/xHWOuwau+ll9lhotuESyyS9bFMiv6OmwPRPmawJBAJ9h02NHqZbHI9JSz3ng/KZDIRc+36haNiIof25WS89pzSBYjI234NU0EayAhfLXoKWqQIKpBsDbWFPjjTINU3MCQQDPuMc1fOX4gMiWiCcjpx6trjNzAuZ73u6eHDtYUupmvnMXbATJj1U37kbxYN7QmpgW4761cw9aiS9bMoALbEPrAkAsn6ocuNq9kWHxKChY2mpCbrccGAlszF0tsSMElHrDRr8c2E35+8qiRuLP5mgads7Os10+d+6hHoibbDGDGwILAkEAtgOBexrwICoomc7ADPuZZDKqnnLCJ3SWvjCax7AP+OuUycc7Aqr3z0SZIVTyqPNtpVD2gORLZdejiVPc4yWA9QJAGVTbr7S299owpaDk51dM0L0fkKGA05dq7yT1AF8MBRx0A0Wm+GLBQ9Qzk004emSL5Izn9hBqDXD3RtGQaHKw1w==");


    public static String generateToken(Long userId, Duration duration) {
        try {
            Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
            return JWT.create()
                    .withIssuer("echo")
                    .withClaim("audience", userId)
                    .withExpiresAt(new Date(System.currentTimeMillis() + duration.toMillis()))
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("生成token失败");
            throw e;
        }
    }

    public static Long parse(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("echo")
                    .build();
            final DecodedJWT decode = JWT.decode(token);
            DecodedJWT decodedJWT = verifier.verify(decode);
            return decodedJWT.getClaim("audience").asLong();
        } catch (TokenExpiredException e) {
            throw new BusinessException(BizCodeEnum.AUTH_ACCESS_TOKEN_EXPIRATION);
        } catch (Exception e) {
            log.error("解密token失败");
            throw e;
        }
    }
}
