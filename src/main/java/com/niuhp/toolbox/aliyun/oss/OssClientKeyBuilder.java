package com.niuhp.toolbox.aliyun.oss;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by niuhp on 2016/4/20.
 */
public class OssClientKeyBuilder {
    private OssClientKeyBuilder() {
    }

    private static ConcurrentHashMap<String, OssClientKey> keyCache = new ConcurrentHashMap<String, OssClientKey>();

    public static OssClientKey build(String endPoint, String accessId, String accessKey) {
        String key = String.format("%s_%s_%s", endPoint, accessId, accessKey);
        OssClientKey ossClientKey = keyCache.get(key);
        if (ossClientKey == null) {
            OssClientKey temp = new OssClientKey(endPoint, accessId, accessKey);
            ossClientKey = keyCache.putIfAbsent(key, temp);
            if (ossClientKey == null) {
                ossClientKey = temp;
            }
        }
        return ossClientKey;
    }
}
