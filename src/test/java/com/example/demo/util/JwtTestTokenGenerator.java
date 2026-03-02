package com.example.demo.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Utility class for generating test JWT tokens.
 * This is for DEMO/TESTING purposes only.
 */
@Slf4j
public class JwtTestTokenGenerator {

    // Demo RSA key pair (for testing only - DO NOT use in production)
    private static final String DEMO_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChPZwxpxZ6+c+l9KiUQ0w/Z4+kceoHFZLjX2SDNpfEeZ3QIswafdLRSIEldqEY5lNx1xIRgRe+fIJfQ6ergQgskyPsVN5zsRpp3DMIuIuovZazYw9ayf0yvZosguVX/fLwhOYCVHnIoEkEc7V7MDdHBDyiQpGVfsBUqLtMXJ0LzYMuttp44LtflRWlNbQZxBnLG0yx3iomEqA560mrrNhNu9fMkEupYg3Lxmaiy3ytR7rRj2l8r2dZ1TpYHY3bYid66h4vi3MqpmQjEzyq2RCIdfQlcC/GrIZlnjEw5g32qi93YxpcrC9A3oS4oXJK1R/HpzhCS1ZqMGUuOABa6uvRAgMBAAECggEACTrVH5SAeW6mX8oXAlGt4i1D8DZjHp+yBlj2EHrGCkWp7nmndFbChB9h4SqewyWgHjKhAqaAnPNUpS5iwTMELEUJ9+PNzhKTv/6OX0/tEq4zm7YucVSBrW9kDWs6xKQxTWpWHkkGpaDjPSGAgHo8l9CaxfEIk2Wrjc0Q16ivhrbYJ/AA+vJNdz6sXBS6yoNOSvmxoKNNYDiFAOrtoKLqQKSePPWKZ+iUMK47tYzQ/OvsQhBqhuwm/hXdN8/d1J6A638wJZqyoJqaip3GDeuyXcQHrGxQs4UJOCpjqS6K98poayRxe7OVULN6ZRdz2eXhM+RpGmAcPXQuXAQNG6+czwKBgQDRJ2kAWvUKa7SWHwQBOSQYATBSG8pTRengUsmdQQa0uPz1YKbZwOW0p6mYeV2rjTGubhQUrsSSwoZNg8ujAjx8A2Zqp26Uw2lCBYf0IEukkRsMOze5ibb1FDGXuBksZCS6h89aSVb80R9o/PtetvV2lZ1aV15GxdVpc9OFTFNpjwKBgQDFWuupQsjlJwwHGTWzEMugDP+DCtcAxeTnhST64zDgkOYhVTzGQWcVvKq3c/HSx+1MF0fWpApLaeefxm1xNmhWRRzAnRtixF6pueShlJEE0zIAJKJcYvyLvif/JMij3HeXBJqzv5k7A+iF6KD/HbOSSFhXq9u4bX8PgQsc3tfknwKBgFLBgDdmxewgn6yCiygcvt5Mohzq6Aw47ogW2bVCBHA/fb9tRbeFLp5jdKRi6SA21sCIx6NDX34eP9ut2UYfXWVIRx7OLt/nzd8Upy1+FdAApi4ZxDvCdMTV/6Vjb3p0CWBQ2keEG0ofSIlXB/L6+3a3TjJvvVrNblqyPLBSefKbAoGANGdSEB21rnNR1EizY5rgFt2cszUbpg7mGzvVd/D/t4GCOfOiMZqIPtNcGV5mJewEbKH551qjiSbT5C8SPG+QAOhVHCgbb3xpDnRX6zfB0iAqNLOFkTaPjKOvhyZDSI9wCd+lHHmRvIPrKDN/HH7MgdlH6++sZipdBMblJoNdnE0CgYEAsPskILjrtN864AB6pC0cXLVKWtIGAznaCUpq10k636hC+mOSsrVKQ0ypte83wyf8up23MNuYHqjEx3C1E/okyjTNoojMl4Q9qRUK6Q/ZmRUgYsGziIv0ighfVXFzb/kUaafPSZCGGIRkqU7KUsEzhW6OotwiX/bQssCpr4SHk44=";
    private static final String DEMO_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoT2cMacWevnPpfSolENMP2ePpHHqBxWS419kgzaXxHmd0CLMGn3S0UiBJXahGOZTcdcSEYEXvnyCX0Onq4EILJMj7FTec7EaadwzCLiLqL2Ws2MPWsn9Mr2aLILlV/3y8ITmAlR5yKBJBHO1ezA3RwQ8okKRlX7AVKi7TFydC82DLrbaeOC7X5UVpTW0GcQZyxtMsd4qJhKgOetJq6zYTbvXzJBLqWINy8Zmost8rUe60Y9pfK9nWdU6WB2N22IneuoeL4tzKqZkIxM8qtkQiHX0JXAvxqyGZZ4xMOYN9qovd2MaXKwvQN6EuKFyStUfx6c4QktWajBlLjgAWurr0QIDAQAB";

    public static void main(String[] args) {
        try {
            // Generate a valid token
            String validToken = generateValidToken();
            System.out.println("\n=== VALID JWT TOKEN (for testing) ===");
            System.out.println(validToken);
            System.out.println("\nUse this token in the Authorization header:");
            System.out.println("Authorization: Bearer " + validToken);

            // Generate an invalid token (wrong issuer)
            String invalidIssuerToken = generateTokenWithWrongIssuer();
            System.out.println("\n\n=== INVALID TOKEN (wrong issuer) ===");
            System.out.println(invalidIssuerToken);

            // Generate an invalid token (wrong audience)
            String invalidAudienceToken = generateTokenWithWrongAudience();
            System.out.println("\n\n=== INVALID TOKEN (wrong audience) ===");
            System.out.println(invalidAudienceToken);

            System.out.println("\n\n=== Public Key (already configured in application.properties) ===");
            System.out.println(DEMO_PUBLIC_KEY);

        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            e.printStackTrace();
        }
    }

    /**
     * Generate a valid JWT token with correct issuer and audience
     */
    public static String generateValidToken() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + 3600000); // 1 hour

        return Jwts.builder()
                .setSubject("test-user@example.com")
                .setIssuer("https://demo.example.com")
                .setAudience("demo-api")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .claim("scope", "read write")
                .claim("name", "Test User")
                .claim("email", "test-user@example.com")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Generate token with wrong issuer (will fail validation)
     */
    public static String generateTokenWithWrongIssuer() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + 3600000);

        return Jwts.builder()
                .setSubject("test-user@example.com")
                .setIssuer("https://wrong-issuer.com") // Wrong issuer
                .setAudience("demo-api")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Generate token with wrong audience (will fail validation)
     */
    public static String generateTokenWithWrongAudience() throws Exception {
        PrivateKey privateKey = getPrivateKey();

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + 3600000);

        return Jwts.builder()
                .setSubject("test-user@example.com")
                .setIssuer("https://demo.example.com")
                .setAudience("wrong-audience") // Wrong audience
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private static PrivateKey getPrivateKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(DEMO_PRIVATE_KEY);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private static PublicKey getPublicKey() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(DEMO_PUBLIC_KEY);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * Generate a new RSA key pair (for reference only)
     */
    public static void generateNewKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key: " + publicKey);
    }
}

