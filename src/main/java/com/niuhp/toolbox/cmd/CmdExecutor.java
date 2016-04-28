package com.niuhp.toolbox.cmd;

import com.niuhp.core.log.api.LogX;
import com.niuhp.core.logadapter.LogXManager;
import com.niuhp.core.util.CommonUtil;
import com.niuhp.core.util.IoUtil;
import com.niuhp.core.util.ReflectUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Queue;

/**
 * Created by niuhp on 2016/4/27.
 */
public enum CmdExecutor {
    INSTANCE;

    private LogX logx;

    private CmdExecutor() {
        init();
    }

    private String defaultEncoding;

    public String execCmd(String cmd) {
        return execCmd(cmd, defaultEncoding);
    }

    public String execCmd(String cmd, int maxLine) {
        return execCmd(cmd, defaultEncoding, maxLine, 60000);
    }

    public String execCmd(String cmd, String encoding) {
        return execCmd(cmd, encoding, 100, 60000);
    }

    private void init() {
        logx = LogXManager.getLogX(CmdExecutor.class);
        Charset charset = ReflectUtil.getDefaultFieldValue(Console.class, "cs", Charset.class);
        if (charset == null) {
            defaultEncoding = System.getProperty("file.encoding");
        } else {
            defaultEncoding = charset.name();
        }
        logx.info(String.format("defalut encoding is %s", defaultEncoding));
    }

    public String execCmd(String cmd, String encoding, int maxLine, long maxWaitMills) {
        StringBuilder resultBuilder = new StringBuilder();

        int currentLine = 1;
        long startTimeMillis = System.currentTimeMillis();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            inputStream = exec.getInputStream();
            if (CommonUtil.isBlank(encoding)) {
                encoding = defaultEncoding;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, encoding));
            while (true) {
                if (maxLine > 0 && currentLine > maxLine) {
                    break;
                }
                if (maxWaitMills > 0 && System.currentTimeMillis() - startTimeMillis > maxWaitMills) {
                    break;
                }
                String line = bufferedReader.readLine();
                if (line == null) {
                    return resultBuilder.toString();
                }
                resultBuilder.append(line).append("\n");
                currentLine++;
            }
        } catch (IOException e) {
            logx.error(String.format("execCmd:%s with encoding=%s,maxLine=%s,maxWaitMills=%s error", cmd, encoding, maxLine, maxWaitMills), e);
        } finally {
            IoUtil.close(inputStream);
            IoUtil.close(bufferedReader);
        }
        return resultBuilder.toString();
    }

    public void execCmd(final String cmd, final String encoding, final int maxLine, final long maxWaitMills, final Queue<String> result, final String deadLine) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                int currentLine = 1;
                long startTimeMillis = System.currentTimeMillis();

                InputStream inputStream = null;
                BufferedReader bufferedReader = null;
                try {
                    Process exec = Runtime.getRuntime().exec(cmd);
                    inputStream = exec.getInputStream();
                    if (CommonUtil.isBlank(encoding)) {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, defaultEncoding));
                    } else {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, encoding));
                    }
                    while (true) {
                        if (maxLine > 0 && currentLine > maxLine) {
                            result.add(deadLine);
                            break;
                        }
                        if (maxWaitMills > 0 && System.currentTimeMillis() - startTimeMillis > maxWaitMills) {
                            result.add(deadLine);
                            break;
                        }
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            result.add(deadLine);
                            break;
                        }
                        result.add(line);
                        currentLine++;
                    }
                } catch (IOException e) {
                    logx.error(String.format("execCmd:%s with encoding=%s,maxLine=%s,maxWaitMills=%s error", cmd, encoding, maxLine, maxWaitMills), e);
                } finally {
                    IoUtil.close(inputStream);
                    IoUtil.close(bufferedReader);
                }
            }
        };
        new Thread(task).start();
    }
}
