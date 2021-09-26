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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class Request<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    protected final RequestAction<T> requestAction;
    protected final Consumer<? super T> onSuccess;
    protected final Consumer<? super Throwable> onFailure;
    protected final Runnable preRequest;
    private final TypeReference<T> typeReference;

    Request(RequestAction<T> requestAction, Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure,
            Runnable preRequest, TypeReference<T> typeReference) {
        this.requestAction = requestAction;
        this.onSuccess = onSuccess;
        this.onFailure = onFailure;
        this.preRequest = preRequest;
        this.typeReference = typeReference;
    }

    okhttp3.Request asOk() {
        var builder = new okhttp3.Request.Builder()
                .url(requestAction.getRoute().getComiledUrl())
                .method(requestAction.getRoute().getMethod().name(), requestAction.getRequestBody());
        return builder.build();
    }

    Route.Method getMethod() {
        return requestAction.getRoute().getMethod();
    }

    String getRoute() {
        return requestAction.getRoute().getComiledUrl();
    }

    Runnable getPreRequest() {
        return preRequest;
    }

    void onSuccess(Response response) {
        try {
            var object = GitlabIntegration.OBJECT_MAPPER.readValue(response.body().byteStream(), typeReference);
            onSuccess.accept(object);
        } catch (JsonParseException e) {
            LOGGER.error("Failed to map object", e);
            onFailure(e);
        } catch (IOException e) {
            LOGGER.error("Failed to read from stream", e);
            onFailure(e);
        } catch (Throwable e) {
            LOGGER.error("Unknown error while mapping objects", e);
            onFailure(e);
        }
    }

    void onFailure(Throwable response) {
        onFailure.accept(response);
    }
}
