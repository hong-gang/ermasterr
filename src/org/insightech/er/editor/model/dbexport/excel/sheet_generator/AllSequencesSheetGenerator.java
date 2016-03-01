package org.insightech.er.editor.model.dbexport.excel.sheet_generator;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.ObjectModel;
import org.insightech.er.editor.model.dbexport.excel.ExportToExcelManager.LoopDefinition;
import org.insightech.er.editor.model.diagram_contents.not_element.sequence.Sequence;
import org.insightech.er.editor.model.progress_monitor.ProgressMonitor;
import org.insightech.er.util.POIUtils;

public class AllSequencesSheetGenerator extends SequenceSheetGenerator {

    @Override
    public void generate(final ProgressMonitor monitor, final HSSFWorkbook workbook, final int sheetNo, final boolean useLogicalNameAsSheetName, final Map<String, Integer> sheetNameMap, final Map<String, ObjectModel> sheetObjectMap, final ERDiagram diagram, final Map<String, LoopDefinition> loopDefinitionMap) throws InterruptedException {

        final LoopDefinition loopDefinition = loopDefinitionMap.get(getTemplateSheetName());

        final HSSFSheet newSheet = createNewSheet(workbook, sheetNo, loopDefinition.sheetName, sheetNameMap);

        final String sheetName = workbook.getSheetName(workbook.getSheetIndex(newSheet));

        sheetObjectMap.put(sheetName, diagram.getDiagramContents().getSequenceSet());

        final HSSFSheet oldSheet = workbook.getSheetAt(sheetNo);

        boolean first = true;

        for (final Sequence sequence : diagram.getDiagramContents().getSequenceSet()) {
            monitor.subTaskWithCounter(sheetName + " - " + sequence.getName());

            if (first) {
                first = false;

            } else {
                POIUtils.copyRow(oldSheet, newSheet, loopDefinition.startLine - 1, oldSheet.getLastRowNum(), newSheet.getLastRowNum() + loopDefinition.spaceLine + 1);
            }

            setSequenceData(workbook, newSheet, sequence, diagram);

            newSheet.setRowBreak(newSheet.getLastRowNum() + loopDefinition.spaceLine);

            monitor.worked(1);
        }

        if (first) {
            for (int i = loopDefinition.startLine - 1; i <= newSheet.getLastRowNum(); i++) {
                final HSSFRow row = newSheet.getRow(i);
                if (row != null) {
                    newSheet.removeRow(row);
                }
            }
        }
    }

    @Override
    public String getTemplateSheetName() {
        return "all_sequences_template";
    }

}
