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

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectHandler {

    private final Project project;

    public ProjectHandler(Project project) {
        this.project = project;
    }

    public void showNotification(NotificationCategory category, NotificationType type, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(category.getCategoryName())
                .createNotification(content, type)
                .notify(project);
    }

    public List<String> getRemoteUrls() {
        return GitRepositoryManager.getInstance(project).getRepositories()
                .stream()
                .map(GitRepository::getRemotes)
                .reduce(new HashSet<>(), (a, b) -> {a.addAll(b); return a;})
                .stream()
                .map(GitRemote::getUrls)
                .reduce(new ArrayList<>(), (a, b) -> {a.addAll(b); return a;})
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
