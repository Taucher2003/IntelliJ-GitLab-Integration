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

public class DetailedStatus {

    @JsonProperty("icon")
    private String icon;
    @JsonProperty("text")
    private String text;
    @JsonProperty("label")
    private String label;
    @JsonProperty("group")
    private Status group;
    @JsonProperty("tooltip")
    private String tooltip;
    @JsonProperty("has_details")
    private boolean hasDetails;
    @JsonProperty("details_path")
    private String detailsPath;
    @JsonProperty("illustration")
    private Object illustration; // no clue what type illustration is
    @JsonProperty("favicon")
    private String favicon;

    public String getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public String getLabel() {
        return label;
    }

    public Status getGroup() {
        return group;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isHasDetails() {
        return hasDetails;
    }

    public String getDetailsPath() {
        return detailsPath;
    }

    public Object getIllustration() {
        return illustration;
    }

    public String getFavicon() {
        return favicon;
    }
}
