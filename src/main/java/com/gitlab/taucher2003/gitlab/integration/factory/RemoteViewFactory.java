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

import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.util.concurrent.TimeUnit;

public class RemoteViewFactory implements ToolWindowFactory {
    @Override
    public boolean isApplicable(@NotNull Project project) {
        GitlabIntegration.EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                var window = ToolWindowManager.getInstance(project).getToolWindow("GitLab");
                if (window == null) {
                    return;
                }
                window.setAvailable(isAvailable(project));
            });
        }, 1, 10, TimeUnit.SECONDS);
        return true;
    }

    public boolean isAvailable(Project project) {
        return !GitRepositoryManager.getInstance(project).getRepositories().isEmpty();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    }
}
