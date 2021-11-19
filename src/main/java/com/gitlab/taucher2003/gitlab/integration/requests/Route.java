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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gitlab.taucher2003.gitlab.integration.requests.Route.Method.GET;

public final class Route {

    public enum Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        HEAD
    }

    public static final String API_BASE = "/api/v4";

    public static final Route GITLAB_CI_YAML_TEMPLATES = new Route("/templates/gitlab_ci_ymls", GET);

    public static final class Pipelines {
        public static final Route LIST_PROJECT_PIPELINES = new Route("/projects/{project_id}/pipelines", GET);
        public static final Route GET_SINGLE_PIPELINE = new Route("/projects/{project_id}/pipelines/{pipeline_id}", GET);
        public static final Route GET_PIPELINE_JOBS = new Route("/projects/{project_id}/pipelines/{pipeline_id}/jobs", GET);

        private Pipelines() {
        }
    }

    private final String path;
    private final Method method;

    private Route(String path, Method method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public CompiledRoute compile(String instanceUrl, Object... args) {
        if(!instanceUrl.startsWith("http://") && !instanceUrl.startsWith("https://")) {
            throw new IllegalArgumentException("Instance URL must specify a protocol");
        }
        var compiledUrl = instanceUrl + API_BASE + path;
        var param = 0;
        while(compiledUrl.contains("{") && compiledUrl.contains("}")) {
            var paramStart = compiledUrl.indexOf("{");
            var paramEnd = compiledUrl.indexOf("}");
            compiledUrl = compiledUrl.substring(0, paramStart) + args[param++] + compiledUrl.substring(paramEnd+1);
        }
        return new CompiledRoute(this, compiledUrl);
    }

    public static final class CompiledRoute {
        private final Route route;
        private final String comiledUrl;
        private Map<String, String> params = new HashMap<>();

        private CompiledRoute(Route route, String compiledUrl) {
            this.route = route;
            this.comiledUrl = compiledUrl;
        }

        public Route getRoute() {
            return route;
        }

        public String getComiledUrl() {
            return comiledUrl + "?" + params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
        }

        public CompiledRoute addParam(String key, String value) {
            params.put(key, value);
            return this;
        }

        public Method getMethod() {
            return getRoute().getMethod();
        }
    }
}
