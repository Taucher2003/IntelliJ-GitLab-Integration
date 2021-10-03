/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.gitlab.taucher2003.gitlab.integration.requests.Requester;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GitlabIntegration implements StartupActivity {

    private static final Map<Project, ProjectHandler> HANDLERS = new HashMap<>();
    private static final Map<String, GitlabCompatible> GITLAB_COMPATIBLE = new ConcurrentHashMap<>();
    public static final Requester REQUESTER = new Requester();
    public static final ObjectMapper OBJECT_MAPPER = createMapper();

    private static ObjectMapper createMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    private GitlabIntegration() {
    }

    public static ProjectHandler getProjectHandler(Project project) {
        return HANDLERS.computeIfAbsent(project, ProjectHandler::new);
    }

    public static GitlabCompatible getCompatible(RemoteMapping mapping) {
        return getCompatible(mapping.getInstanceUrl());
    }

    public static GitlabCompatible getCompatible(String remote) {
        return GITLAB_COMPATIBLE.computeIfAbsent(remote, ignored -> GitlabCompatible.NOT_CHECKED);
    }

    public static void setCompatible(String remote, GitlabCompatible compatible) {
        GITLAB_COMPATIBLE.put(remote, compatible);
    }

    @Override
    public void runActivity(@NotNull Project project) {
        HANDLERS.computeIfAbsent(project, ProjectHandler::new);
    }

    public enum GitlabCompatible {
        NOT_CHECKED(JBColor.DARK_GRAY, "Not Checked"),
        CHECKING(JBColor.GRAY, "Checking"),
        COMPATIBLE(JBColor.GREEN, "Compatible"),
        NOT_COMPATIBLE(JBColor.RED, "Not Compatible");

        private final JBColor color;
        private final String message;

        GitlabCompatible(JBColor color, String message) {
            this.color = color;
            this.message = message;
        }

        public JBColor getColor() {
            return color;
        }

        public String getMessage() {
            return message;
        }
    }
}
