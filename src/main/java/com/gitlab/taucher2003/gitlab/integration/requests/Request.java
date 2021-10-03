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
import okhttp3.Headers;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class Request<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);

    protected final RequestAction<T> requestAction;
    protected final Consumer<? super T> onSuccess;
    protected final Consumer<? super Headers> onSuccessHeaders;
    protected final Consumer<? super Throwable> onFailure;
    protected final Runnable preRequest;
    private final TypeReference<T> typeReference;
    private final boolean returnResponseCode;

    Request(RequestAction<T> requestAction, Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure,
            Runnable preRequest, TypeReference<T> typeReference, boolean returnResponseCode) {
        this(requestAction, onSuccess, null, onFailure, preRequest, typeReference, returnResponseCode);
    }

    Request(RequestAction<T> requestAction, Consumer<? super T> onSuccess, Consumer<? super Headers> onSuccessHeaders, Consumer<? super Throwable> onFailure,
            Runnable preRequest, TypeReference<T> typeReference, boolean returnResponseCode) {
        this.requestAction = requestAction;
        this.onSuccess = onSuccess;
        this.onSuccessHeaders = onSuccessHeaders;
        this.onFailure = onFailure;
        this.preRequest = preRequest;
        this.typeReference = typeReference;
        this.returnResponseCode = returnResponseCode;
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

    boolean isReturnResponseCode() {
        return returnResponseCode;
    }

    void onSuccess(Response response) {
        if(onSuccessHeaders != null) {
            onSuccessHeaders.accept(response.headers());
        }
        if(returnResponseCode) {
            //noinspection unchecked
            onSuccess.accept((T)(Integer) response.code());
            return;
        }
        T object;
        try {
            object = GitlabIntegration.OBJECT_MAPPER.readValue(response.body().byteStream(), typeReference);
        } catch (JsonParseException e) {
            LOGGER.error("Failed to map object", e);
            onFailure(e);
            return;
        } catch (IOException e) {
            LOGGER.error("Failed to read from stream", e);
            onFailure(e);
            return;
        } catch (Throwable e) {
            LOGGER.error("Unknown error while mapping objects", e);
            onFailure(e);
            return;
        }
        // call onSuccess outside of try-catch to let exceptions thrown here throw
        onSuccess.accept(object);
    }

    void onFailure(Throwable response) {
        onFailure.accept(response);
    }
}
