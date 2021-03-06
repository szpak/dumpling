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

package com.github.olivergondza.dumpling.model.jvm;

import java.lang.ref.WeakReference;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.github.olivergondza.dumpling.model.ProcessThread;
import com.github.olivergondza.dumpling.model.mxbean.MXBeanThread;

/**
 * {@link ProcessThread} with {@link Thread} reference.
 *
 * @author ogondza
 */
public final class JvmThread extends MXBeanThread<JvmThread, JvmThreadSet, JvmRuntime> {

    final @Nonnull JvmThread.Builder state;

    /*package*/ JvmThread(@Nonnull JvmRuntime runtime, @Nonnull JvmThread.Builder builder) {
        super(runtime, builder);
        this.state = builder;
    }

    /**
     * Get {@link Thread} represented by this {@link ProcessThread}.
     *
     * @return <tt>null</tt> in case the thread does not longer exist.
     */
    public @CheckForNull Thread getThread() {
        return state.thread.get();
    }

    public final static class Builder extends MXBeanThread.Builder<Builder> {

        // Using weak reference not to keep the thread in memory once terminated
        private final @Nonnull WeakReference<Thread> thread;

        public Builder(@Nonnull Thread thread) {
            this.thread = new WeakReference<Thread>(thread);
        }
    }
}
