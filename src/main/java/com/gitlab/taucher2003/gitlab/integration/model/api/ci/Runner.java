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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Runner {

    @JsonProperty("id")
    private long id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("is_shared")
    private boolean isShared;

    @JsonProperty("runner_type")
    private RunnerType runnerType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("online")
    private boolean online;

    @JsonProperty("status")
    private String status;

    public enum RunnerType {
        @JsonProperty("instance_type")
        INSTANCE_TYPE,

        @JsonProperty("group_type")
        GROUP_TYPE,

        @JsonProperty("project_type")
        PROJECT_TYPE
    }
}
