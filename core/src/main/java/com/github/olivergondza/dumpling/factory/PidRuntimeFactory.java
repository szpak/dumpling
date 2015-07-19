/*
 * The MIT License
 *
 * Copyright (c) 2014 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.olivergondza.dumpling.factory;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.github.olivergondza.dumpling.model.ProcessRuntime;
import com.github.olivergondza.dumpling.model.dump.ThreadDumpRuntime;

/**
 * Create {@link ProcessRuntime} from running local process.
 *
 * Process ID is used as a locator.
 *
 * This implementations invokes jstack binary and delegates to {@link ThreadDumpFactory} so it shares its features
 * and limitations.
 *
 * @author ogondza
 */
public class PidRuntimeFactory {

    private final @Nonnull String javaHome;

    public PidRuntimeFactory() {
        this(System.getProperty("java.home"));
    }

    public PidRuntimeFactory(@Nonnull String javaHome) {
        this.javaHome = javaHome;
    }

    /**
     * @param pid Process id to examine.
     * @throws IOException When jstack invocation failed.
     * @throws InterruptedException When jstack invocation was interrupted.
     */
    public @Nonnull ThreadDumpRuntime fromProcess(int pid) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(jstackBinary(), "-l", Integer.toString(pid));

        Process process = pb.start();

        // Start consuming the output without waiting for process completion not to block both processes.
        ThreadDumpRuntime runtime = new ThreadDumpFactory().fromStream(process.getInputStream());

        int ret = process.waitFor();
        validateResult(process, ret);

        return runtime;
    }

    private void validateResult(Process process, int ret) throws IOException {
        if (ret == 0) return;

        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        while (process.getErrorStream().read(buffer) != -1) {
            sb.append(new String(buffer));
        }

        throw new IOException("jstack failed with code " + ret + ": " + sb.toString().trim());
    }

    private String jstackBinary() {
        return javaHome + "/../bin/jstack";
    }
}