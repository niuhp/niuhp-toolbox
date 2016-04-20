package com.niuhp.toolbox.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.niuhp.core.cache.CommonCache;
import com.niuhp.core.logadapter.LogX;
import com.niuhp.core.util.IoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by niuhp on 2016/4/20.
 */
public class OssClientProxy {
    private static LogX logx = LogX.getLogX(OssClientProxy.class);

    private OssClientProxy() {
    }

    private static CommonCache<OssClientKey, OSSClient> ossClientCache = new CommonCache<OssClientKey, OSSClient>(new OssClientGenerator());

    public static String putObject(String endPoint, String accessId, String accessKey, String bucketName, String objKey, InputStream objStream, ObjectMetadata meta, int urlExpiredSeconds) {
        OssClientKey clientKey = OssClientKeyBuilder.build(endPoint, accessId, accessKey);
        OSSClient ossClient = ossClientCache.getCache(clientKey);
        PutObjectResult result = ossClient.putObject(bucketName, objKey, objStream, meta);
        String resultUrl = "";
        if (urlExpiredSeconds > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, urlExpiredSeconds);
            resultUrl = ossClient.generatePresignedUrl(bucketName, objKey, cal.getTime()).toString(); //生成一个签名的Uri 有效期urlExpiredSeconds秒
        } else if (ossClient.getBucketAcl(bucketName).getGrants().isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 300);
            resultUrl = ossClient.generatePresignedUrl(bucketName, objKey, cal.getTime()).getPath(); //生成一个签名的Uri 有效期5分钟
        } else {
            resultUrl = String.format("http://%s.%s/%s", bucketName, endPoint, objKey);
        }
        return resultUrl;
    }

    public static String putObject(String endPoint, String accessId, String accessKey, String bucketName, String objKey, InputStream objStream, ObjectMetadata meta) {
        return putObject(endPoint, accessId, accessKey, bucketName, objKey, objStream, meta, 0);
    }

    public static String putImg(String endPoint, String accessId, String accessKey, String bucketName, String imgFileName, InputStream imgStream, int urlExpiredSeconds) {
        String imgKey = String.format("image/%s", imgFileName);
        ObjectMetadata meta = new ObjectMetadata();
        int index = imgFileName.lastIndexOf('.');
        meta.setContentType(String.format("image/%s", imgFileName.substring(index + 1).toLowerCase()));
        return putObject(endPoint, accessId, accessKey, bucketName, imgKey, imgStream, meta, urlExpiredSeconds);
    }

    public static String putImg(String endPoint, String accessId, String accessKey, String bucketName, String imgPath, int urlExpiredSeconds) {
        ObjectMetadata meta = new ObjectMetadata();
        File file = new File(imgPath);
        String imgFileName = file.getName();
        String imgKey = String.format("image/%s", imgFileName);
        int index = imgFileName.lastIndexOf('.');
        meta.setContentType(String.format("image/%s", imgFileName.substring(index + 1).toLowerCase()));

        InputStream imgStream = null;
        try {
            imgStream = new FileInputStream(file);
            return putObject(endPoint, accessId, accessKey, bucketName, imgKey, imgStream, meta, urlExpiredSeconds);
        } catch (Exception e) {
            logx.error("putObject error", e);
        } finally {
            IoUtil.close(imgStream);
        }
        return null;
    }
}
