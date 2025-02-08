package devkor.ontime_back.global.oauth.apple;

import java.util.List;

public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {
    public ApplePublicKey getMatchedKey(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid JWT: No matching Apple Public Key found"));
    }
}
