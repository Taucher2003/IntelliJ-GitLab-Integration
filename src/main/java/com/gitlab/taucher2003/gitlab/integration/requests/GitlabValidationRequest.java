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

import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.intellij.util.io.HttpRequests;

import java.io.IOException;

public final class GitlabValidationRequest {

    private GitlabValidationRequest() {
    }

    public static boolean isGitlabInstance(RemoteMapping mapping) throws IOException {
        var route = Route.GITLAB_CI_YAML_TEMPLATES.compile(mapping.getFullInstanceUrl());
        var code = HttpRequests.request(route.getComiledUrl())
                .connectTimeout(10000) // TODO: make configurable
                .readTimeout(10000)    // TODO: make configurable
                .throwStatusCodeException(false)
                .tryConnect();
        return code == 200;
    }
}
