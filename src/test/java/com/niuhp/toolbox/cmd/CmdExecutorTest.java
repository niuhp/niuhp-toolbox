package com.niuhp.toolbox.cmd;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by niuhp on 2016/4/27.
 */
public class CmdExecutorTest {
    @Test
    public void testExecCmd() throws IOException {
        System.out.println(CmdExecutor.INSTANCE.execCmd("ping baidu.com"));
        System.out.println(CmdExecutor.INSTANCE.execCmd("ping -t baidu.com", 10));

    }

    @Test
    public void testAsyncExecCmd() throws InterruptedException {
        String cmd = "ping -t baidu.com";
        LinkedBlockingQueue<String> result = new LinkedBlockingQueue<String>();
        String deadLine = "---------complete---------";
        CmdExecutor.INSTANCE.execCmd(cmd, null, 10, -1, result, deadLine);
        String str = result.take();
        while (str != deadLine) {
            System.out.println(str);
            str = result.take();
        }
    }
}
