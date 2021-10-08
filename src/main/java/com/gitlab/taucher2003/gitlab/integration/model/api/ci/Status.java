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
import com.intellij.ui.JBColor;

public enum Status {

    @JsonProperty("failed")
    FAILED(JBColor.RED, "Failed", true),

    @JsonProperty("warning")
    WARNING(JBColor.ORANGE, "Warnings", true),

    @JsonProperty("pending")
    PENDING(JBColor.ORANGE, "Pending", false),

    @JsonProperty("running")
    RUNNING(JBColor.BLUE, "Running", false),

    @JsonProperty("manual")
    MANUAL(JBColor.WHITE, "Manual", false),

    @JsonProperty("scheduled")
    SCHEDULED(JBColor.WHITE, "Scheduled", false),

    @JsonProperty("canceled")
    CANCELED(JBColor.DARK_GRAY, "Canceled", true),

    @JsonProperty("success")
    SUCCESS(JBColor.GREEN, "Passed", true),

    @JsonProperty("success-with-warnings")
    SUCCESS_WARNINGS(JBColor.ORANGE, "Passed with warnings", true),

    @JsonProperty("skipped")
    SKIPPED(JBColor.GRAY, "Skipped", true),

    @JsonProperty("created")
    CREATED(JBColor.LIGHT_GRAY, "Created", false);

    private final JBColor color;
    private final String name;
    private final boolean finishedState;

    Status(JBColor color, String name, boolean finishedState) {
        this.color = color;
        this.name = name;
        this.finishedState = finishedState;
    }

    public JBColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public boolean isFinishedState() {
        return finishedState;
    }
}
