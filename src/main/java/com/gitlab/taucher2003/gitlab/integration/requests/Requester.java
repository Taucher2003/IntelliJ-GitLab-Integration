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

import com.intellij.util.concurrency.AppExecutorUtil;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Requester {

    private static final Logger LOGGER = LoggerFactory.getLogger(Requester.class);

    private final BlockingQueue<Request<?>> requests = new LinkedBlockingQueue<>();
    private final AtomicReference<ScheduledFuture<?>> currentQueueExecution = new AtomicReference<>();
    private final OkHttpClient httpClient = new OkHttpClient();

    public <T> boolean enqueue(Request<T> request) {
        var success = requests.add(request);
        drainQueue();
        return success;
    }

    private void drainQueue() {
        if(currentQueueExecution.get() != null) {
            return;
        }
        currentQueueExecution.set(AppExecutorUtil.getAppScheduledExecutorService()
                .schedule(this::executeQueue, 500, TimeUnit.MILLISECONDS)); // 500ms to wait for other requests
    }

    private void executeQueue() {
        while (!requests.isEmpty()) {
            var request = requests.peek();
            executeRequest(request);
            requests.poll();
        }
        currentQueueExecution.set(null);
    }

    private boolean executeRequest(Request<?> request) {
        request.getPreRequest().run();
        Response lastResponse;
        try {
            var attempt = 0;
            do {
                lastResponse = httpClient.newCall(request.asOk()).execute();
                if(lastResponse.code() < 500) {
                    break;
                }
                lastResponse.close();
                attempt++;
            } while (attempt < 3);
        } catch (IOException exception) {
            LOGGER.error("An I/O Error occurred while executing a REST request", exception);
            request.onFailure(exception);
            return false;
        }

        if(!lastResponse.isSuccessful() && !request.isReturnResponseCode()) {
            var exception = new ResponseStatusException(lastResponse.code(), lastResponse.message());
            request.onFailure(exception);
            lastResponse.close();
            return false;
        }

        request.onSuccess(lastResponse);
        lastResponse.close();
        return true;
    }
}
