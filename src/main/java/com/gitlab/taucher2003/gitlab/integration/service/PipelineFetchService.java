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

import com.fasterxml.jackson.core.type.TypeReference;
import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.gitlab.taucher2003.gitlab.integration.model.api.pipeline.Pipeline;
import com.gitlab.taucher2003.gitlab.integration.model.api.pipeline.PipelineListEntry;
import com.gitlab.taucher2003.gitlab.integration.requests.Route;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.gitlab.taucher2003.gitlab.integration.util.RouteUtils;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PipelineFetchService {

    private final Project project;
    private final MessageBus messageBus;

    private final List<PipelineListEntry> pipelines = new ArrayList<>();
    private final List<Pipeline> expandedPipelines = new ArrayList<>();

    public PipelineFetchService(Project project) {
        this.project = project;
        this.messageBus = project.getMessageBus();
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(this::updatePipelines, 30, 30, TimeUnit.SECONDS);
    }

    private void updatePipelines() {
        var handler = GitlabIntegration.getProjectHandler(project);
        var mappings = handler.getRemoteMappings();
        pipelines.clear();
        var futures = new ArrayList<CompletableFuture<Void>>();
        for(var mapping : mappings) {
            var route = Route.Pipelines.LIST_PROJECT_PIPELINES.compile(
                    mapping.getFullInstanceUrl(),
                    RouteUtils.encodeRepositoryPath(mapping.getRepositoryPath())
            );
            var request = handler.createRequest(route, new TypeReference<List<PipelineListEntry>>() {});
            futures.add(request.queue(pipelines::addAll));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenAccept(ignored -> {
                    messageBus.syncPublisher(PipelinesUpdateListener.PIPELINES_UPDATED).handle(pipelines);
                    updateExpandedPipelines();
                });
    }

    private void updateExpandedPipelines() {
        var handler = GitlabIntegration.getProjectHandler(project);
        expandedPipelines.clear();
        var futures = new ArrayList<CompletableFuture<Void>>();
        for(var pipeline : pipelines) {
            var route = Route.Pipelines.GET_SINGLE_PIPELINE.compile(
                    RemoteFinder.getInstanceUrlFromWeb(pipeline.getWebUrl()),
                    pipeline.getProjectId(),
                    pipeline.getId()
            );
            var request = handler.createRequest(route, new TypeReference<Pipeline>() {});
            futures.add(request.queue(expandedPipelines::add));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenAccept(ignored -> messageBus.syncPublisher(ExpandedPipelinesUpdateListener.EXPANDED_PIPELINES_UPDATED).handle(expandedPipelines));
    }

    public List<PipelineListEntry> getPipelines() {
        return Collections.unmodifiableList(pipelines);
    }

    public List<Pipeline> getExpandedPipelines() {
        return expandedPipelines.stream().sorted(Comparator.comparingLong(Pipeline::getId)).collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface PipelinesUpdateListener {

        Topic<PipelinesUpdateListener> PIPELINES_UPDATED = Topic.create("Pipelines Updated", PipelinesUpdateListener.class);

        void handle(List<PipelineListEntry> pipelines);

    }

    @FunctionalInterface
    public interface ExpandedPipelinesUpdateListener {

        Topic<ExpandedPipelinesUpdateListener> EXPANDED_PIPELINES_UPDATED = Topic.create("Expanded Pipelines Updated", ExpandedPipelinesUpdateListener.class);

        void handle(List<Pipeline> pipelines);

    }
}
