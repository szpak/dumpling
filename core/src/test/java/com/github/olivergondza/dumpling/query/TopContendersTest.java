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
package com.github.olivergondza.dumpling.query;

import static com.github.olivergondza.dumpling.model.ProcessThread.nameIs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.github.olivergondza.dumpling.Util;
import com.github.olivergondza.dumpling.factory.ThreadDumpFactory;
import com.github.olivergondza.dumpling.model.dump.ThreadDumpRuntime;
import com.github.olivergondza.dumpling.model.dump.ThreadDumpThread;
import com.github.olivergondza.dumpling.model.dump.ThreadDumpThreadSet;

public class TopContendersTest {

    @Test
    public void trivial() throws Exception {
        ThreadDumpRuntime runtime = new ThreadDumpFactory().fromStream(Util.resource("jstack/producer-consumer.log"));

        TopContenders.Result<ThreadDumpThreadSet, ThreadDumpRuntime, ThreadDumpThread> contenders = new TopContenders().query(runtime.getThreads());

        final ThreadDumpThread owning = runtime.getThreads().where(nameIs("owning_thread")).onlyThread();

        assertThat(contenders.getBlockers().size(), equalTo(1));
        assertThat(contenders.blockedBy(owning), equalTo(runtime.getThreads().where(nameIs("blocked_thread"))));
    }

    @Test
    public void contenders() throws Exception {
        ThreadDumpRuntime runtime = new ThreadDumpFactory().fromStream(Util.resource("jstack/contention.log"));

        TopContenders.Result<ThreadDumpThreadSet, ThreadDumpRuntime, ThreadDumpThread> contenders = new TopContenders().query(runtime.getThreads());

        ThreadDumpThreadSet ts = runtime.getThreads();
        final ThreadDumpThread producerProcessThread = ts.where(nameIs("producer")).onlyThread();

        assertThat(contenders.getBlockers().size(), equalTo(1));
        assertThat(contenders.blockedBy(producerProcessThread).size(), equalTo(3));
    }
}
