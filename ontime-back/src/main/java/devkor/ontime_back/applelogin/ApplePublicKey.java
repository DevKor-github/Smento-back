package devkor.ontime_back.applelogin;

public record ApplePublicKey(String kty, String kid, String alg, String n, String e) {
}
