package com.niuhp.toolbox.aliyun.oss;

import com.aliyun.oss.OSSClient;
import com.niuhp.core.cache.KvExecutor;

/**
 * Created by niuhp on 2016/4/20.
 */
public class OssClientGenerator implements KvExecutor<OssClientKey, OSSClient> {

  @Override
  public OSSClient excute(OssClientKey ossClientKey) {
    OSSClient occClient = new OSSClient(ossClientKey.getEndPoint(), ossClientKey.getAccessId(), ossClientKey.getAccessKey());
    return occClient;
  }
}
