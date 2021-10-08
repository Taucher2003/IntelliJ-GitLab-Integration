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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class Pipeline {

    @JsonProperty("id")
    private long id;
    @JsonProperty("iid")
    private long iid;
    @JsonProperty("project_id")
    private long projectId;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("source")
    private String source; // TODO: move to enum
    @JsonProperty("ref")
    private String ref;
    @JsonProperty("sha")
    private String sha;
    @JsonProperty("before_sha")
    private String beforeSha;
    @JsonProperty("tag")
    private boolean tag;
    @JsonProperty("yaml_errors")
    private String yamlErrors;
    @JsonProperty("user")
    private TriggerUser user;
    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;
    @JsonProperty("started_at")
    private OffsetDateTime startedAt;
    @JsonProperty("finished_at")
    private OffsetDateTime finishedAt;
    @JsonProperty("committed_at")
    private OffsetDateTime committedAt;
    @JsonProperty("duration")
    private double duration;
    @JsonProperty("queued_duration")
    private double queuedDuration;
    @JsonProperty("coverage")
    private String coverage;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("detailed_status")
    private DetailedStatus detailedStatus;

    @JsonIgnore
    private int jobAmount;
    @JsonIgnore
    private int jobAmountFinished;

    public long getId() {
        return id;
    }

    public long getIid() {
        return iid;
    }

    public long getProjectId() {
        return projectId;
    }

    public Status getStatus() {
        return status;
    }

    public Status getRealStatus() {
        return detailedStatus.getGroup();
    }

    public String getRef() {
        return ref;
    }

    public String getSha() {
        return sha;
    }

    public boolean isTag() {
        return tag;
    }

    public String getYamlErrors() {
        return yamlErrors;
    }

    public TriggerUser getUser() {
        return user;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public OffsetDateTime getFinishedAt() {
        return finishedAt;
    }

    public OffsetDateTime getCommittedAt() {
        return committedAt;
    }

    public double getDuration() {
        return duration;
    }

    public double getQueuedDuration() {
        return queuedDuration;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public DetailedStatus getDetailedStatus() {
        return detailedStatus;
    }

    public int getJobAmount() {
        return jobAmount;
    }

    public int getJobAmountFinished() {
        return jobAmountFinished;
    }

    public String getFinishedJobPercent() {
        return String.format("%.2f", ((double) jobAmountFinished / (double) jobAmount) * 100).replaceAll("[.,]00", "");
    }

    void setJobAmount(int jobAmount) {
        this.jobAmount = jobAmount;
    }

    void setJobAmountFinished(int jobAmountFinished) {
        this.jobAmountFinished = jobAmountFinished;
    }
}
