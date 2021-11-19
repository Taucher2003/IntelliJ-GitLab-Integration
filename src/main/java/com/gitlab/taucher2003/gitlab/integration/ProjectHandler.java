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

import com.fasterxml.jackson.core.type.TypeReference;
import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.gitlab.taucher2003.gitlab.integration.requests.RequestAction;
import com.gitlab.taucher2003.gitlab.integration.requests.Route;
import com.gitlab.taucher2003.gitlab.integration.service.GitUpdateService;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.impl.NotificationsManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.BalloonLayoutData;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectHandler.class);

    private final Project project;

    public ProjectHandler(Project project) {
        this.project = project;
    }

    public void showNotification(NotificationCategory category, String categorySuffix, NotificationType type, String content) {
        NotificationGroupManager
                .getInstance()
                .getNotificationGroup(
                        category.getCategoryName() + categorySuffix
                )
                .createNotification(content, type)
                .notify(project);
    }

    public void showNotification(Notification notification) {
        var ideFrame = WindowManager.getInstance().getIdeFrame(project);
        if(ideFrame == null) {
            LOGGER.error("IDE Frame was null");
            return;
        }
        var layout = BalloonLayoutData.fullContent();
        var balloon = NotificationsManagerImpl.createBalloon(ideFrame, notification, false, false, layout, project);
        notification.setBalloon(balloon);
        notification.notify(project);
    }

    public Collection<RemoteMapping> getCompatibleRemoteMappings() {
        return project.getService(GitUpdateService.class).getCompatibleMappings();
    }

    public Collection<RemoteMapping> getRemoteMappings() {
        return project.getService(GitUpdateService.class).getMappings();
    }

    public <T> List<T> getDistinctFromMappings(Function<RemoteMapping, T> function) {
        return getRemoteMappings()
                .stream()
                .map(function)
                .distinct()
                .collect(Collectors.toList());
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
                .distinct()
                .collect(Collectors.toList());
    }

    public <T> RequestAction<T> createRequest(Route.CompiledRoute route, TypeReference<T> typeReference) {
        return new RequestAction<>(project, route, typeReference);
    }

    public <T> RequestAction<T> createRequest(Route.CompiledRoute route, RequestBody body, TypeReference<T> typeReference) {
        return new RequestAction<>(project, route, body, typeReference);
    }
}
