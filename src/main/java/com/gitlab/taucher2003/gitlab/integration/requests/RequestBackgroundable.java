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

import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.google.common.base.Stopwatch;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestBackgroundable<T> extends Task.Backgroundable {

    private final Request<T> request;
    private final CompletableFuture<T> future;

    public RequestBackgroundable(Project project, Request<T> request, CompletableFuture<T> future) {
        super(project, request.getMethod() + " " + request.getRoute(), false);
        this.request = request;
        this.future = future;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        var watch = Stopwatch.createStarted();
        GitlabIntegration.REQUESTER.enqueue(request);
        try {
            future.get(Requester.REQUEST_TIMEOUT_SECONDS + 10, TimeUnit.SECONDS);
            if (watch.elapsed(TimeUnit.MILLISECONDS) < 1000) {
                Thread.sleep(500);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        } finally {
            indicator.stop();
        }
    }
}
