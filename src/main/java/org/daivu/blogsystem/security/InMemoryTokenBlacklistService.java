package org.daivu.blogsystem.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String tokenId, Instant expiresAt) {
        purgeExpired();
        blacklist.put(tokenId, expiresAt);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        Instant expiresAt = blacklist.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            blacklist.remove(tokenId);
            return false;
        }
        return true;
    }

    private void purgeExpired() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
