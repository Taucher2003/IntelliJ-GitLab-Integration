/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class User {

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("username")
    private String username;

    @JsonProperty("state")
    private String state; // TODO: convert to enum

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("location")
    private String location;

    @JsonProperty("public_email")
    private String publicEmail;

    @JsonProperty("skype")
    private String skype;

    @JsonProperty("linkedin")
    private String linkedIn;

    @JsonProperty("twitter")
    private String twitter;

    @JsonProperty("website_url")
    private String websiteUrl;

    @JsonProperty("organization")
    private String organization;

    @JsonProperty("job_title")
    private String jobTitle;

    @JsonProperty("pronouns")
    private String pronouns;

    @JsonProperty("bot")
    private boolean bot;

    @JsonProperty("work_information")
    private String workInformation;

    @JsonProperty("followers")
    private long followers;

    @JsonProperty("following")
    private long following;

    @JsonProperty("bio_html")
    private String bioHtml;

    @JsonProperty("local_time")
    private String localTime;
}
