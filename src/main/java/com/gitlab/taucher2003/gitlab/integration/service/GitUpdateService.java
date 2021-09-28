/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.service;

import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.gitlab.taucher2003.gitlab.integration.requests.Route;
import com.intellij.dvcs.repo.VcsRepositoryManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GitUpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitUpdateService.class);

    private final Project project;
    private final MessageBus messageBus;

    private final Collection<GitRepository> currentRepositories;

    public GitUpdateService(Project project) {
        this.project = project;
        this.messageBus = project.getMessageBus();
        this.currentRepositories = GitUtil.getRepositories(project);
        messageBus.connect().subscribe(VcsRepositoryManager.VCS_REPOSITORY_MAPPING_UPDATED, this::fireUpdate);
        messageBus.connect().subscribe(GitRepository.GIT_REPO_CHANGE, repository -> this.fireUpdate());
        messageBus.connect().subscribe(GitRemoteUpdateListener.GIT_REMOTES_UPDATED, this::checkGitlabCompatible);
        fireUpdate();
    }

    private void checkGitlabCompatible(Iterable<RemoteMapping> mappings) {
        checkGitlabCompatible(mappings, false);
    }

    public void reloadGitlabCompatible() {
        checkGitlabCompatible(getMappings(), true);
    }

    private void checkGitlabCompatible(Iterable<RemoteMapping> mappings, boolean checkAll) {
        var handler = GitlabIntegration.getProjectHandler(project);
        mappings.forEach(mapping -> {
            if (GitlabIntegration.getCompatible(mapping) != GitlabIntegration.GitlabCompatible.NOT_CHECKED && !checkAll) {
                return;
            }
            GitlabIntegration.setCompatible(mapping.getInstanceUrl(), GitlabIntegration.GitlabCompatible.NOT_CHECKED);

            var route = Route.GITLAB_CI_YAML_TEMPLATES.compile(mapping.getFullInstanceUrl());
            handler.createRequest(route, null, new GitlabIntegration.JacksonType<>())
                    .withPreRequest(() -> GitlabIntegration.setCompatible(mapping.getInstanceUrl(), GitlabIntegration.GitlabCompatible.CHECKING))
                    .returnResponseCode()
                    .queue(
                            value -> GitlabIntegration.setCompatible(
                                    mapping.getInstanceUrl(),
                                    value == 200
                                            ? GitlabIntegration.GitlabCompatible.COMPATIBLE
                                            : GitlabIntegration.GitlabCompatible.NOT_COMPATIBLE
                            ),
                            throwable -> GitlabIntegration.setCompatible(mapping.getInstanceUrl(), GitlabIntegration.GitlabCompatible.NOT_COMPATIBLE)
                    );
        });
    }

    private void fireUpdate() {
        var newCurrentRepositories = GitUtil.getRepositories(project);
        var added = newCurrentRepositories.stream().filter(Predicate.not(currentRepositories::contains)).collect(Collectors.toList());
        var removed = currentRepositories.stream().filter(Predicate.not(newCurrentRepositories::contains)).collect(Collectors.toList());

        var mappings = getMappings(newCurrentRepositories);

        currentRepositories.clear();
        currentRepositories.addAll(newCurrentRepositories);

        if (messageBus.isDisposed()) {
            return;
        }

        messageBus.syncPublisher(GitUpdateListener.GIT_REPOSITORIES_UPDATED).handle(added, removed);
        messageBus.syncPublisher(GitRemoteUpdateListener.GIT_REMOTES_UPDATED).handle(mappings);
    }

    public Collection<RemoteMapping> getMappings() {
        return getMappings(currentRepositories);
    }

    private List<RemoteMapping> getMappings(Collection<GitRepository> repositories) {
        return repositories.stream()
                .map(GitRepository::getRemotes)
                .reduce(new ArrayList<>(), (a, b) -> {
                    a.addAll(b);
                    return a;
                })
                .stream()
                .map(RemoteMapping::of)
                .distinct()
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface GitUpdateListener {

        Topic<GitUpdateListener> GIT_REPOSITORIES_UPDATED = Topic.create("Git Repositories Updated", GitUpdateListener.class);

        void handle(Collection<GitRepository> newRepositories, Collection<GitRepository> removedRepositories);

    }

    @FunctionalInterface
    public interface GitRemoteUpdateListener {

        Topic<GitRemoteUpdateListener> GIT_REMOTES_UPDATED = Topic.create("Git Remotes Updated", GitRemoteUpdateListener.class);

        void handle(List<RemoteMapping> mappings);

    }
}
