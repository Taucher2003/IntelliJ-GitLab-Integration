/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.action;

import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

public class OpenRepositoryAction extends AnAction implements DumbAware {

    private final String url;
    private final String remoteUrl;

    // used by intellij
    public OpenRepositoryAction() {
        this.url = null;
        this.remoteUrl = null;
    }

    protected OpenRepositoryAction(String url, String displayName) {
        super(displayName, null, null);
        this.url = url;
        this.remoteUrl = RemoteFinder.getProjectUrl(url);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var project = e.getProject();
        var remoteUrls = GitlabIntegration.getProjectHandler(project).getRemoteUrls();
        if(url == null) { // if url null -> instantiated by intellij
            // only show if exact one remote url, otherwise hide to use the group
            e.getPresentation().setEnabledAndVisible(remoteUrls.size() == 1);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if(project == null) {
            return;
        }
        String url;
        if(this.url == null) {
            url = RemoteFinder.getProjectUrl(GitlabIntegration.getProjectHandler(project).getRemoteUrls().get(0));
        } else {
            url = this.remoteUrl;
        }
        BrowserUtil.browse(url);
    }

    @Override
    public String toString() {
        return "OpenRepositoryAction{" +
                "url='" + url + '\'' +
                '}';
    }
}