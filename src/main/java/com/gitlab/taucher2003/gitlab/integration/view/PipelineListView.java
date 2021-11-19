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

import com.gitlab.taucher2003.gitlab.integration.GitlabIntegration;
import com.gitlab.taucher2003.gitlab.integration.model.api.ci.Pipeline;
import com.gitlab.taucher2003.gitlab.integration.model.api.ci.Status;
import com.gitlab.taucher2003.gitlab.integration.service.PipelineFetchService;
import com.gitlab.taucher2003.gitlab.integration.util.DateFormatter;
import com.gitlab.taucher2003.gitlab.integration.util.NamedFunction;
import com.gitlab.taucher2003.gitlab.integration.util.RemoteFinder;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PipelineListView {
    private final Project project;

    private JPanel formContent;
    private JPanel tablePanel;
    private JTable pipelineTable;

    private final PipelinesMappingTableModel tableModel;

    public PipelineListView(Project project) {
        this.project = project;
        this.tableModel = new PipelinesMappingTableModel();
        tableModel.rows.addAll(project.getService(PipelineFetchService.class).getExpandedPipelines());

        this.pipelineTable = new PipelinesMappingTable(tableModel);
        pipelineTable.addMouseListener(new PipelinesMouseAdapter());
        pipelineTable.setAutoCreateRowSorter(true);
        pipelineTable.setUpdateSelectionOnSort(true);
//        var sorter = (DefaultRowSorter<?, ?>) pipelineTable.getRowSorter();
//        var sortKeys = List.of(new RowSorter.SortKey(1, SortOrder.DESCENDING));
//        sorter.setSortKeys(sortKeys);

        createTablePanel(pipelineTable);

        project.getMessageBus().connect().subscribe(PipelineFetchService.ExpandedPipelinesUpdateListener.EXPANDED_PIPELINES_UPDATED, new PipelinesUpdateHandler());
    }

    private void createUIComponents() {
    }

    private void createTablePanel(JTable table) {
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(new JBScrollPane(table), BorderLayout.CENTER, 0);
    }

    public JPanel getPanel() {
        return formContent;
    }

    private static class PipelinesMappingTableModel extends AbstractTableModel {

        protected final List<Pipeline> rows = new ArrayList<>();
        protected final List<NamedFunction<Pipeline, Object>> columns = Arrays.asList(
                new NamedFunction<>("Project", pipeline -> RemoteFinder.findPathFromWeb(pipeline.getWebUrl())),
                new NamedFunction<>("Id", Pipeline::getId),
                new NamedFunction<>("Ref", Pipeline::getRef),
                new NamedFunction<>("Status", Pipeline::getRealStatus),
                new NamedFunction<>("% finished", Pipeline::getFinishedJobPercent),
                new NamedFunction<>("Created At", Pipeline::getCreatedAt)
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
            if (rowIndex >= rows.size()) {
                return null;
            }
            var row = rows.get(rowIndex);
            return columns.get(columnIndex).apply(row);
        }

        @Override
        public String getColumnName(int column) {
            return columns.get(column).getName();
        }
    }

    private static final class PipelinesMappingTable extends JBTable {
        private PipelinesMappingTable(TableModel tableModel) {
            super(tableModel);
        }

        @Override
        public TableCellRenderer getCellRenderer(int row, int column) {
            return new Renderer(super.getCellRenderer(row, column));
        }
    }

    private static final class Renderer implements TableCellRenderer {

        private final TableCellRenderer delegate;

        private Renderer(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            var component = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            var label = new JBLabel(((JLabel) component).getText());
            if(column == 0 || column == 1) {
                label.setForeground(JBColor.BLUE);
                var attributes = new HashMap<TextAttribute, Object>(label.getFont().getAttributes());
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                label.setFont(label.getFont().deriveFont(attributes));
            }
            if(value instanceof Status) {
                var status = (Status) value;
                label.setText(status.getName());
                label.setForeground(status.getColor());
            }
            if(value instanceof OffsetDateTime) {
                label.setText(DateFormatter.formatDate((OffsetDateTime) value));
            }
            return label;
        }
    }

    public final class PipelinesMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            var row = pipelineTable.getSelectedRow();
            var column = pipelineTable.columnAtPoint(e.getPoint());
            var value = pipelineTable.getValueAt(row, column);
            if(column == 0) {
                var remote = GitlabIntegration.getProjectHandler(project).getRemoteMappings()
                        .stream()
                        .filter(mapping -> mapping.getRepositoryPath().equals(String.valueOf(value)))
                        .findAny();
                if(remote.isEmpty()) {
                    return;
                }
                BrowserUtil.browse(RemoteFinder.getProjectUrl(remote.get().getUrl()));
            }
            if(column == 1) {
                handlePipelineClick(Integer.parseInt(String.valueOf(value)));
            }
        }

        private void handlePipelineClick(int pipelineId) {
            var pipeline = tableModel.rows.stream().filter(p -> p.getId() == pipelineId).findAny();
            if(pipeline.isEmpty()) {
                return;
            }
            BrowserUtil.browse(pipeline.get().getWebUrl());
        }
    }

    private class PipelinesUpdateHandler implements PipelineFetchService.ExpandedPipelinesUpdateListener {
        @Override
        public void handle(List<Pipeline> pipelines) {
            tableModel.rows.clear();
            tableModel.rows.addAll(pipelines);
        }
    }
}
