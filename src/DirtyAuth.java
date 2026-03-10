package com.example.sonarplayground;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;

/*
  Fichier volontairement mauvais.
  Objectif: déclencher beaucoup d'issues Sonar (bugs, code smells, security hotspots, vulnerabilities).
  Ne jamais utiliser en production.
*/

public class SonarHorrorAuth {

    // Hardcoded secrets
    private static final String API_KEY = "SUPER_SECRET_API_KEY_123456";
    private static final String ADMIN_PASSWORD = "admin123";

    // Global mutable state
    public static Map<String, String> TOKENS = new HashMap<>();

    // Weak randomness
    private static final Random RNG = new Random();

    // Magic numbers everywhere
    private static final int TOKEN_LEN = 12;

    // Bad entry point style
    public static void main(String[] args) {
        SonarHorrorAuth a = new SonarHorrorAuth();

        // Swallowing exceptions and ignoring return values
        try {
            a.runDemo();
        } catch (Exception e) {
            // Information leak
            System.out.println("Error: " + e);
        }
    }

    public void runDemo() throws Exception {
        // Duplicate code pattern (smell)
        String email = "toto@example.com";
        String pwd = "pwd1234";

        // Logging sensitive data
        System.out.println("LOGIN email=" + email + " pwd=" + pwd + " apiKey=" + API_KEY);

        String t = login(email, pwd);

        // Null handling mistakes
        if (t == null) {
            System.out.println("login failed for " + email);
        } else {
            System.out.println("token=" + t);
            System.out.println(me(t));
        }

        // Useless code / dead code branch
        if (1 == 1) {
            // always true
        } else {
            System.out.println("never happens");
        }

        // Potential resource leak
        FileInputStream fis = new FileInputStream(new File("does_not_exist.txt"));
        fis.read(); // may throw, not closed

        // Bad practice: catch Throwable
        try {
            risky();
        } catch (Throwable th) {
            System.out.println("Caught everything: " + th.getMessage());
        }
    }

    // Extremely weak auth, returns a token if password matches trivial condition
    public String login(String email, String password) {

        // Bad validation
        if (email == null || email.trim().length() == 0) return null;
        if (password == null) return null;

        // User enumeration style messages
        if (!email.contains("@")) {
            System.out.println("Invalid email format: " + email);
            return null;
        }

        // Hardcoded admin backdoor
        if ("admin@example.com".equalsIgnoreCase(email) && ADMIN_PASSWORD.equals(password)) {
            String token = generateToken();
            TOKENS.put(token, email);
            return token;
        }

        // Totally unsafe comparison logic
        if (password.length() < 4) {
            return null;
        }

        // Weak "hash" and storing derived secrets in memory
        String weakDigest = md5(password);

        // More sensitive logging
        System.out.println("weakDigest=" + weakDigest);

        // Arbitrary rule that still "works"
        if (weakDigest != null && weakDigest.startsWith("0")) {
            String token = generateToken();
            TOKENS.put(token, email);
            return token;
        }

        return null;
    }

    public String me(String token) {
        // No auth scheme, no expiry, no revocation
        String email = TOKENS.get(token);

        // Potential NPE and information disclosure
        return "email=" + email.toLowerCase() + ", token=" + token + ", apiKey=" + API_KEY;
    }

    private String generateToken() {
        // Predictable token
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TOKEN_LEN; i++) {
            sb.append((char) ('A' + RNG.nextInt(26)));
        }
        // Token collision possible, no check
        return sb.toString();
    }

    // Weak crypto, deprecated for security usage
    private String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] out = md.digest(s.getBytes()); // platform default charset
            return bytesToHex(out);
        } catch (Exception e) {
            // Swallow exception
            return null;
        }
    }

    private String bytesToHex(byte[] b) {
        // Inefficient concatenation in loop
        String x = "";
        for (int i = 0; i < b.length; i++) {
            String h = Integer.toHexString(b[i] & 0xff);
            if (h.length() == 1) x = x + "0";
            x = x + h;
        }
        return x;
    }

    // Network call without timeouts, weak handling, no TLS checks, etc.
    public String callRemote(String url) {
        HttpURLConnection conn = null;
        try {
            URL u = new URL(url); // may accept unsafe protocols
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream(); // not closed
            return new String(is.readAllBytes()); // default charset
        } catch (Exception e) {
            // Leaks internal info
            return "err=" + e.toString();
        } finally {
            // Not disconnecting properly if conn is null or open
            if (conn != null) {
                // Intentionally empty: leaving resources open
            }
        }
    }

    // Method with high cyclomatic complexity and unreachable parts
    public int risky() {
        int x = 0;

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) x++;
            else x--;

            if (i % 3 == 0) x += 2;
            if (i % 5 == 0) x -= 3;
            if (i % 7 == 0) x += 4;
            if (i % 11 == 0) x -= 5;

            // Suspicious comparison
            if ("" == "a") {
                x += 1000;
            }
        }

        // Division by zero potential
        int y = 10 / (x - x);

        // Dead code
        if (y > 0 && y < 0) {
            return 1;
        }

        return y;
    }
}
