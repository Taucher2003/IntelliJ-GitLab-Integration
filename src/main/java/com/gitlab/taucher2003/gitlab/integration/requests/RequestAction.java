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

import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class RequestAction<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAction.class);

    private static Consumer<Object> defaultSuccess = t -> {};
    private static Consumer<Throwable> defaultFailure = t -> LOGGER.error("An unhandled exception occurred", t);

    public static void setDefaultSuccess(Consumer<Object> defaultSuccess) {
        RequestAction.defaultSuccess = defaultSuccess;
    }

    public static void setDefaultFailure(Consumer<Throwable> defaultFailure) {
        RequestAction.defaultFailure = defaultFailure;
    }

    protected final Project project;
    protected final Route.CompiledRoute route;
    protected final RequestBody requestBody;
    protected final TypeReference<T> typeReference;

    protected Runnable preRequest = () -> {};
    protected boolean returnResponseCode;

    public RequestAction(Project project, Route.CompiledRoute route, TypeReference<T> typeReference) {
        this(project, route, null, typeReference);
    }

    public RequestAction(Project project, Route.CompiledRoute route, RequestBody requestBody, TypeReference<T> typeReference) {
        this.project = project;
        this.route = route;
        this.requestBody = requestBody;
        this.typeReference = typeReference;
    }

    public RequestAction<T> withPreRequest(Runnable runnable) {
        this.preRequest = runnable;
        return this;
    }

    public RequestAction<Integer> returnResponseCode() {
        var action = new RequestAction<>(project, route, requestBody, new TypeReference<Integer>() {});
        action.withPreRequest(preRequest);
        action.returnResponseCode = true;
        return action;
    }

    public <U extends RequestAction<U>> RequestAction<U> flatMap(Function<T, U> function) {
        return new FlatmapRequestAction<>(this, function);
    }

    public CompletableFuture<Void> queue() {
        return queue(defaultSuccess);
    }

    public CompletableFuture<Void> queue(Consumer<? super T> success) {
        return queue(success, defaultFailure);
    }

    public CompletableFuture<Void> queue(Consumer<? super T> success, Consumer<? super Throwable> error) {
        var future = new CompletableFuture<Void>();
        submit().whenComplete((t, e) -> {
            if(e != null) {
                error.accept(e);
                future.completeExceptionally(e);
                return;
            }
            success.accept(t);
            future.complete(null);
        });
        return future;
    }

    public CompletableFuture<T> submit() {
        var future = new CompletableFuture<T>();
        var request = createRequest(future);
        executeRequest(request, future);
        return future;
    }

    protected Request<T> createRequest(CompletableFuture<T> future) {
        return new Request<>(this, future::complete, future::completeExceptionally, preRequest, typeReference, returnResponseCode);
    }

    protected void executeRequest(Request<T> request, CompletableFuture<T> future) {
        var requestBackgroundable = new RequestBackgroundable<>(project, request, future);
        var indicator = new BackgroundableProcessIndicator(requestBackgroundable);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(requestBackgroundable, indicator);
    }

    Route.CompiledRoute getRoute() {
        return route;
    }

    RequestBody getRequestBody() {
        return requestBody;
    }
}
