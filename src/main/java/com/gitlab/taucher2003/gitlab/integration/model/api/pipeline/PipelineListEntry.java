/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.model.api.pipeline;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.gitlab.taucher2003.gitlab.integration.requests.Route;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.openapi.project.Project;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

public class PipelineListEntry {

    @JsonProperty("id")
    private long id;
    @JsonProperty("project_id")
    private long projectId;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("source")
    private String source;
    @JsonProperty("ref")
    private String ref;
    @JsonProperty("sha")
    private String sha;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    @JsonProperty("web_url")
    private String webUrl;

    public long getId() {
        return id;
    }

    public long getProjectId() {
        return projectId;
    }

    public Status getStatus() {
        return status;
    }

    public String getSource() {
        return source;
    }

    public String getRef() {
        return ref;
    }

    public String getSha() {
        return sha;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public CompletableFuture<Pipeline> expand(Project project) {
        var instanceUrl = RemoteFinder.getInstanceUrlFromWeb(webUrl);
        var route = Route.Pipelines.GET_SINGLE_PIPELINE.compile(instanceUrl, projectId, id);
        var request = GitlabIntegration.getProjectHandler(project).createRequest(route, new TypeReference<Pipeline>() {});
        return request.submit();
    }
}