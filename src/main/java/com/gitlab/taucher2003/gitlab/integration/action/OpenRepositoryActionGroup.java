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
import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class OpenRepositoryActionGroup extends ActionGroup {

    @Override
    public boolean hideIfNoVisibleChildren() {
        return true;
    }

    @Override
    public boolean disableIfNoVisibleChildren() {
        return true;
    }

    @Override
    @NotNull
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        if(e == null) {
            return new AnAction[0];
        }
        var urls = GitlabIntegration.getProjectHandler(e.getProject())
                .getRemoteMappings()
                .stream()
                .collect(Collectors.groupingBy(RemoteMapping::getInstanceUrl));

        var actions = new ArrayList<AnAction>();

        if(urls.size() == 1) {
            var instance =  new ArrayList<>(urls.keySet()).get(0);
            var mappings = urls.get(instance);
            if(mappings.size() == 1) {
                return new AnAction[0];
            }

            for(var mapping : mappings) {
                actions.add(new OpenRepositoryAction(mapping.getUrl(), mapping.getRepositoryPath()));
            }
            return actions.toArray(AnAction[]::new);
        }

        for(var instance : urls.keySet()) {
            actions.add(new OpenRepositoryRemoteGroup(instance));
        }

        return actions.toArray(AnAction[]::new);
    }

    private static final class OpenRepositoryRemoteGroup extends ActionGroup {

        private final String instanceUrl;

        private OpenRepositoryRemoteGroup(String instanceUrl) {
            super(instanceUrl, true);
            this.instanceUrl = instanceUrl;
        }

        @Override
        public @NotNull AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            var actions = GitlabIntegration.getProjectHandler(e.getProject())
                    .getRemoteMappings()
                    .stream()
                    .filter(mapping -> mapping.getInstanceUrl().equals(instanceUrl))
                    .map(RemoteMapping::getUrl)
                    .map(url -> new OpenRepositoryAction(url, RemoteFinder.findPath(url)))
                    .toArray(AnAction[]::new);
            return actions;
        }
    }
}
