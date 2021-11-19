/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.factory;

import com.gitlab.taucher2003.gitlab.integration.service.GitUpdateService;
import com.gitlab.taucher2003.gitlab.integration.view.PipelineListView;
import com.gitlab.taucher2003.gitlab.integration.view.RemoteMappingView;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;

public class ToolViewFactory implements ToolWindowFactory, DumbAware {

    private final Map<Project, RemoteMappingView> remoteMappingViews = new HashMap<>();
    private final Map<Project, PipelineListView> pipelineListViews = new HashMap<>();

    @Override
    public boolean isApplicable(@NotNull Project project) {
        remoteMappingViews.computeIfAbsent(project, RemoteMappingView::new);
        pipelineListViews.computeIfAbsent(project, PipelineListView::new);
        project.getMessageBus().connect().subscribe(
                GitUpdateService.GitRemoteUpdateListener.GIT_REMOTES_UPDATED,
                mappings -> SwingUtilities.invokeLater(() -> {
                    var window = ToolWindowManager.getInstance(project).getToolWindow("GitLab");
                    if (window == null) {
                        return;
                    }
                    window.setAvailable(!mappings.isEmpty());
                })
        );
        return true;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var remoteMappingView = remoteMappingViews.get(project);
        var remoteMappingContent = contentFactory.createContent(remoteMappingView.getPanel(), "Remotes", false);
        var pipelineListView = pipelineListViews.get(project);
        var pipelineListContent = contentFactory.createContent(pipelineListView.getPanel(), "Pipelines", false);
        toolWindow.getContentManager().addContent(remoteMappingContent);
        toolWindow.getContentManager().addContent(pipelineListContent);
    }
}
