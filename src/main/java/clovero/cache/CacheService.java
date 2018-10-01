package clovero.cache;

import clovero.Constant;
import clovero.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class CacheService {
    private static final Long cacheExpiredDurationInSeconds = 86400L;

    CacheRepository cacheRepository;

    public Optional<Cache> get(String key) {
        return cacheRepository
                .findByKey(key)
                .filter(cache -> ZonedDateTime.now(Constant.ZONE_ID_UTC).isBefore(cache.getExpiredAt()));
    }

    public void put(String key, Object value) {
        final Cache cache = cacheRepository.findByKey(key).orElseGet(Cache::new);
        cache.setKey(key);
        cache.setValue(JsonUtils.toJson(value));
        cache.setExpiredAt(ZonedDateTime.now(Constant.ZONE_ID_UTC).plusSeconds(cacheExpiredDurationInSeconds));
        cacheRepository.save(cache);
    }
}
