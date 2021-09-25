/*
 * Copyright 2021 Niklas van Schrick and the IntelliJ GitLab Integration contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gitlab.taucher2003.gitlab.integration.view;

import com.gitlab.taucher2003.gitlab.integration.model.RemoteMapping;
import com.gitlab.taucher2003.gitlab.integration.model.TableColumnDefinition;
import com.gitlab.taucher2003.gitlab.integration.service.GitUpdateService;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RemoteMappingView {

    private final Project project;

    private JPanel formContent;
    private JPanel tablePanel;
    private JTable remoteTable;

    private final RemoteMappingTableModel tableModel;

    public RemoteMappingView(Project project) {
        this.project = project;
        this.tableModel = new RemoteMappingTableModel();
        project.getService(GitUpdateService.class)
                .getMappings()
                .forEach(this::registerRemoteMapping);

        this.remoteTable = new JBTable(tableModel);
        remoteTable.setAutoCreateRowSorter(true);
        remoteTable.setUpdateSelectionOnSort(true);
        createTablePanel(remoteTable);

        project.getMessageBus().connect().subscribe(GitUpdateService.GitRemoteUpdateListener.GIT_REMOTES_UPDATED, new GitUpdateHandler());
    }

    private void createTablePanel(JTable table) {
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(new JBScrollPane(table), BorderLayout.CENTER, 0);
    }

    private void createUIComponents() {
    }

    public void registerRemoteMapping(RemoteMapping mapping) {
        tableModel.rows.add(mapping);
    }

    public JPanel getPanel() {
        return formContent;
    }

    private static class RemoteMappingTableModel extends AbstractTableModel {

        protected final List<RemoteMapping> rows = new ArrayList<>();
        protected final List<TableColumnDefinition<RemoteMapping, Object>> columns = Arrays.asList(
                new TableColumnDefinition<>("Name", RemoteMapping::getName),
                new TableColumnDefinition<>("URL", RemoteMapping::getUrl),
                new TableColumnDefinition<>("Instance URL", RemoteMapping::getInstanceUrl),
                new TableColumnDefinition<>("Repository Path", RemoteMapping::getRepositoryPath)
        );

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return columns.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var row = rows.get(rowIndex);
            return columns.get(columnIndex).apply(row);
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column).getTitle();
        }
    }

    private class GitUpdateHandler implements GitUpdateService.GitRemoteUpdateListener {
        @Override
        public void handle(Collection<RemoteMapping> mappings) {
            tableModel.rows.clear();
            mappings.forEach(RemoteMappingView.this::registerRemoteMapping);
        }
    }
}
