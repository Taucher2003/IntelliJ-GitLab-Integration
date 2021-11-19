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
import com.intellij.notification.NotificationType;
import com.intellij.ui.JBColor;

public enum Status {

    @JsonProperty("failed")
    FAILED(JBColor.RED, "red", "Failed", NotificationType.ERROR, true),

    @JsonProperty("warning")
    WARNING(JBColor.ORANGE, "orange", "Warnings", NotificationType.WARNING, true),

    @JsonProperty("pending")
    PENDING(JBColor.ORANGE, "orange", "Pending", NotificationType.INFORMATION, false),

    @JsonProperty("running")
    RUNNING(JBColor.BLUE, "blue", "Running", NotificationType.INFORMATION, false),

    @JsonProperty("manual")
    MANUAL(JBColor.WHITE, "white", "Manual", NotificationType.INFORMATION, false),

    @JsonProperty("scheduled")
    SCHEDULED(JBColor.WHITE, "white", "Scheduled", NotificationType.INFORMATION, false),

    @JsonProperty("canceled")
    CANCELED(JBColor.DARK_GRAY, "dark-grey", "Canceled", NotificationType.WARNING, true),

    @JsonProperty("success")
    SUCCESS(JBColor.GREEN, "green", "Passed", NotificationType.INFORMATION, true),

    @JsonProperty("success-with-warnings")
    SUCCESS_WARNINGS(JBColor.ORANGE, "orange", "Passed with warnings", NotificationType.WARNING, true),

    @JsonProperty("skipped")
    SKIPPED(JBColor.GRAY, "gray", "Skipped", NotificationType.WARNING, true),

    @JsonProperty("created")
    CREATED(JBColor.LIGHT_GRAY, "light-gray", "Created", NotificationType.INFORMATION, false);

    private final JBColor color;
    private final String colorName;
    private final String name;
    private final NotificationType notificationType;
    private final boolean finishedState;

    Status(JBColor color, String colorName, String name, NotificationType notificationType, boolean finishedState) {
        this.color = color;
        this.colorName = colorName;
        this.name = name;
        this.notificationType = notificationType;
        this.finishedState = finishedState;
    }

    public JBColor getColor() {
        return color;
    }

    public String getColorName() {
        return colorName;
    }

    public String getName() {
        return name;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public boolean isFinishedState() {
        return finishedState;
    }
}
