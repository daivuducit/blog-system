package org.daivu.blogsystem.security;

import java.time.Instant;

/**
 * Danh sách đen token (dùng cho logout).
 * Tuần 1: lưu trong bộ nhớ. Tuần 3 sẽ thay bằng bản cài đặt dùng Redis.
 */
public interface TokenBlacklistService {

    void blacklist(String tokenId, Instant expiresAt);

    boolean isBlacklisted(String tokenId);
}
