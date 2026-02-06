package bcu.cmp5332.bookingsystem.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing and verification.
 *
 * <p>
 * Passwords are hashed using SHA-256 before being stored.
 * Plain text passwords are never persisted.
 * </p>
 */
public final class PasswordUtil {

    private PasswordUtil() {}

    /**
     * Hashes a raw password using SHA-256.
     *
     * @param rawPassword the plain text password
     * @return the hashed password as a hexadecimal string
     */
    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(rawPassword.getBytes());
            return bytesToHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available");
        }
    }

    /**
     * Verifies a raw password against a stored hash.
     *
     * @param rawPassword the password entered by the user
     * @param storedHash the stored hashed password
     * @return true if the password matches, false otherwise
     */
    public static boolean verify(String rawPassword, String storedHash) {
        return hash(rawPassword).equals(storedHash);
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param bytes the byte array
     * @return hexadecimal representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
