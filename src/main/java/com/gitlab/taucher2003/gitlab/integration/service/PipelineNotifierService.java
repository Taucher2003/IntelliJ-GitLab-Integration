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
import com.gitlab.taucher2003.gitlab.integration.model.api.ci.PipelineListEntry;
import com.gitlab.taucher2003.gitlab.integration.model.api.ci.Status;
import com.gitlab.taucher2003.gitlab.integration.util.DataPair;
import com.gitlab.taucher2003.gitlab.integration.util.DateFormatter;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class PipelineNotifierService {

    private final Project project;
    private final Collection<PipelineListEntry> currentPipelines = new ArrayList<>();

    public PipelineNotifierService(Project project) {
        this.project = project;
        project.getMessageBus().connect().subscribe(PipelineFetchService.PipelinesUpdateListener.PIPELINES_UPDATED, this::onPipelineUpdates);
    }

    private void onPipelineUpdates(Collection<PipelineListEntry> pipelines) {
        try {
            var modifiedPipelines = pipelines.stream()
                    .map(pipeline -> new DataPair<>(pipeline, findCurrentFromId(pipeline.getId())))
                    .filter(pair -> {
                        if (pair.getSecond().isEmpty()) {
                            return !pair.getFirst().getStatus().isFinishedState();
                        }
                        return pair.getFirst().getStatus() != pair.getSecond().get().getStatus();
                    })
                    .map(DataPair::getFirst)
                    .collect(Collectors.toList());
            modifiedPipelines.forEach(pipeline -> {
                var notification = new PipelineStatusNotification(pipeline);
                notification.addAction(new PipelineStatusNotificationAction("Open in Browser", () -> BrowserUtil.browse(pipeline.getWebUrl())));
                GitlabIntegration.getProjectHandler(project).showNotification(notification);
            });
            currentPipelines.clear();
            currentPipelines.addAll(pipelines);
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }

    private Optional<PipelineListEntry> findCurrentFromId(long id) {
        return currentPipelines.stream().filter(pipeline -> pipeline.getId() == id).findFirst();
    }

    private final class PipelineStatusNotification extends Notification {

        private PipelineStatusNotification(PipelineListEntry pipeline) {
            super(getGroupForPipelineStatus(pipeline.getStatus()), getContentForPipelineStatus(pipeline), pipeline.getStatus().getNotificationType());
        }
    }

    private String getGroupForPipelineStatus(Status status) {
        return Status.class.getCanonicalName() + "." + status.name();
    }

    private String getContentForPipelineStatus(PipelineListEntry pipeline) {
        var builder = new StringBuilder();

        if(GitlabIntegration.getProjectHandler(project).getCompatibleRemoteMappings().size() != 1) {
            builder.append(RemoteFinder.findPathFromWeb(pipeline.getWebUrl())).append(" ");
        }
        builder.append(pipeline.getRef()).append(": ")

                .append("<span style=\"color:").append(pipeline.getStatus().getColorName()).append("\">")
                .append(pipeline.getStatus().getName())
                .append("</span>")

                .append("<br>")
                .append("Created: ").append(DateFormatter.formatDate(pipeline.getCreatedAt()))

                .append("<br>")
                .append("Updated: ").append(DateFormatter.formatDate(pipeline.getUpdatedAt()))
        ;

        return builder.toString();
    }

    private static final class PipelineStatusNotificationAction extends AnAction {
        private final Runnable runnable;

        private PipelineStatusNotificationAction(String name, Runnable runnable) {
            super(name);
            this.runnable = runnable;
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            runnable.run();
        }

        @Override
        public boolean isDumbAware() {
            return true;
        }
    }
}
