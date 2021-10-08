/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.model.api.ci;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gitlab.taucher2003.gitlab.integration.model.api.Commit;
import com.gitlab.taucher2003.gitlab.integration.model.api.User;

import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

    @JsonProperty("id")
    private long id;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("stage")
    private String stage;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ref")
    private String ref;

    @JsonProperty("tag")
    private boolean tag;

    @JsonProperty("coverage")
    private Double coverage;

    @JsonProperty("allow_failure")
    private boolean allowFailure;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("started_at")
    private OffsetDateTime startedAt;

    @JsonProperty("finished_at")
    private OffsetDateTime finishedAt;

    @JsonProperty("duration")
    private double duration;

    @JsonProperty("queued_duration")
    private double queuedDuration;

    @JsonProperty("user")
    private User user;

    @JsonProperty("commit")
    private Commit commit;

    @JsonProperty("pipeline")
    private PipelineListEntry pipeline;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("artifacts")
    private List<Object> artifacts;

    @JsonProperty("runner")
    private Runner runner;

    @JsonProperty("artifacts_expire_at")
    private OffsetDateTime artifactsExpireAt;

    @JsonProperty("tag_list")
    private List<String> tagList;

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getStage() {
        return stage;
    }

    public String getName() {
        return name;
    }

    public String getRef() {
        return ref;
    }

    public boolean isTag() {
        return tag;
    }

    public Double getCoverage() {
        return coverage;
    }

    public boolean isAllowFailure() {
        return allowFailure;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getFinishedAt() {
        return finishedAt;
    }

    public double getDuration() {
        return duration;
    }

    public double getQueuedDuration() {
        return queuedDuration;
    }

    public User getUser() {
        return user;
    }

    public Commit getCommit() {
        return commit;
    }

    public PipelineListEntry getPipeline() {
        return pipeline;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public List<Object> getArtifacts() {
        return artifacts;
    }

    public Runner getRunner() {
        return runner;
    }

    public OffsetDateTime getArtifactsExpireAt() {
        return artifactsExpireAt;
    }

    public List<String> getTagList() {
        return tagList;
    }
}
