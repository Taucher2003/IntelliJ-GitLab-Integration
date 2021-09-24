/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.remote;

import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepositoryManager;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class RemoteMappingView {

    private final Project project;

    private JPanel panel1;
    private JTable table1;

    public RemoteMappingView(Project project) {
        this.project = project;
    }

    private void createUIComponents() {
    }

    public RemoteMappingView update() {
        var model = new DefaultTableModel(0, 0);
        model.addColumn("Name");
        model.addColumn("URL");
        model.addColumn("Instance URL");
        model.addColumn("Repository Path");

        var gitRepository = GitRepositoryManager.getInstance(project);
        gitRepository.getRepositories().forEach(repository -> repository.getRemotes().forEach(remote -> {
            var vector = new Vector<String>();
            vector.add(remote.getName());
            remote.getUrls().forEach(url -> {
                vector.add(url);
                vector.add(RemoteFinder.findBase(url));
                vector.add(RemoteFinder.findPath(url));
                model.addRow(vector);
                vector.clear();
                vector.add("");
            });
        }));
        table1.setModel(model);
        return this;
    }

    public JPanel getPanel() {
        return panel1;
    }

    public void reload() {
        table1.repaint();
    }
}
