package com.gerhardboer.fileditor.view;

import com.gerhardboer.fileditor.FileType;
import com.gerhardboer.fileditor.model.NgComponentEditor;
import com.gerhardboer.fileditor.model.NgComponentEditorHolder;
import com.gerhardboer.fileditor.state.NgComponentFileState;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout.Split;
import org.jdesktop.swingx.MultiSplitLayout.Node;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class NgComponentPanel extends JPanel {

  private JPanel main;
  private NgComponentEditorHolder editors;

  private NgComponentFileState fileState;

  public NgComponentPanel(NgComponentEditorHolder editors,
                          NgComponentFileState fileState) {
    this.editors = editors;
    this.main = new JPanel();
    this.fileState = fileState;

    init();
  }

  private void init() {
    setBorderLayout();

    addMain();
    addOptions();
  }

  private void setBorderLayout() {
    this.setLayout(new BorderLayout());
  }

  private void addMain() {
    setView(main);
    add(this.main, BorderLayout.CENTER);
  }

  private void setView(JComponent panel) {
    List<NgComponentEditor> editors = this.editors.activeWindows();
    panel.setLayout(new BorderLayout());

    // When there is just one editor we don't need MultiSplit
    if (editors.size() == 1) {
      panel.add(oneView(editors));
    } else {
      panel.add(multipleViews(editors));
    }

    panel.updateUI();
  }

  private void addOptions() {
    JPanel options = new JPanel();
    options.setLayout(new FlowLayout());
    for (NgComponentEditor editor : this.editors.all) {
      JCheckBox box = createHideShow(editor);
      options.add(box);
    }

    this.add(options, BorderLayout.SOUTH);
  }

  private JCheckBox createHideShow(NgComponentEditor editor) {

    JCheckBox box = new JCheckBox(editor.type);

    String fileName = editor.fileName;
    box.setActionCommand(fileName);

    box.setSelected(this.fileState.get(fileName));

    box.addActionListener(e -> {
      JCheckBox box1 = (JCheckBox) e.getSource();
      editor.active = box1.isSelected();
      updateState(fileName, editor.active);

      recalculateContent();
    });

    return box;
  }

  private void updateState(String name, boolean newState) {
    FileType.forFileName(name).map(fileType ->
        this.fileState.put(fileType, newState)
    );
  }

  private void recalculateContent() {
    this.main.removeAll();
    setView(this.main);
  }

  private JComponent oneView(List<NgComponentEditor> editors) {
    return editors.get(0).view;
  }

  // Source for the idea of multisplitpane
  // https://stackoverflow.com/questions/6117826/jxmultisplitpane-how-to-use
  // NOTE setWeight does not work
  private JXMultiSplitPane multipleViews(List<NgComponentEditor> editors) {
    List<Node> viewNodes = createViewNodes(editors);
    Split split = createSplit(viewNodes);

    JXMultiSplitPane sp = new JXMultiSplitPane();
    sp.setModel(split);

    editors.forEach(
        (NgComponentEditor editor) -> sp.add(editor.view, editor.fileName)
    );

    return sp;
  }

  private Split createSplit(List<Node> viewNodes) {
    Split split = new Split();
    split.setChildren(viewNodes);
    return split;
  }

  private List<Node> createViewNodes(List<NgComponentEditor> editors) {
    // This calculates the width (weight) of the leaf. 100 / 4 / 100 = 0.25 (25%)
    double preferredSize = (double) 100 / editors.size() / 100;

    // Keep track of the leafs in a List, to set later on on the split
    List<Node> nodeList = new ArrayList<>();
    for (int i = 0; i < editors.size(); i++) {
      NgComponentEditor editor = editors.get(i);

      Leaf leaf = new Leaf(editor.fileName);
      leaf.setWeight(preferredSize);

      nodeList.add(leaf);
      // After the last leaf is added, a divider is no longer needed
      if (i != (editors.size() - 1)) {
        nodeList.add(new Divider());
      }
    }
    return nodeList;
  }
}