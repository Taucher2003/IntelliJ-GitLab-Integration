/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.requests;

import com.gitlab.taucher2003.gitlab.integration.util.NamedSupplier;
import com.google.common.base.Stopwatch;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class AsyncRequest {

    private AsyncRequest() {
    }

    public static <T> CompletableFuture<T> request(Project project, NamedSupplier<T> request) {
        var future = new CompletableFuture<T>();
        var requestTask = new RequestBackgroundable<>(project, request, future);
        var indicator = new BackgroundableProcessIndicator(requestTask);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(requestTask, indicator);
        return future;
    }

    private static final class RequestBackgroundable<T> extends Task.Backgroundable {

        private final NamedSupplier<T> supplier;
        private final CompletableFuture<T> future;

        private RequestBackgroundable(Project project, NamedSupplier<T> request, CompletableFuture<T> future) {
            super(project, request.getName(), false);
            this.supplier = request;
            this.future = future;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            var watch = Stopwatch.createStarted();
            try {
                var result = supplier.get();
                future.complete(result);
                if(watch.elapsed(TimeUnit.MILLISECONDS) < 1000) {
                    // keep progress indicator alive for at least one second
                    Thread.sleep(500);
                }
            } catch (InterruptedException ignored) {
            } finally {
                indicator.stop();
            }
        }
    }
}
