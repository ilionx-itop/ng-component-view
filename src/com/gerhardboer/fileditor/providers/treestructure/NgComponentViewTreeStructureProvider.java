package com.gerhardboer.fileditor.providers.treestructure;

import com.gerhardboer.fileditor.Constants;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NgComponentViewTreeStructureProvider implements TreeStructureProvider {

//    private Set<String> parsedComponents = new HashSet<>();

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        Set<String> parsedComponents = new HashSet<>();
        ArrayList<AbstractTreeNode> nodes = new ArrayList<AbstractTreeNode>();

        for (AbstractTreeNode child : children) {
            if (child instanceof PsiFileNode) {
                VirtualFile file = ((PsiFileNode) child).getVirtualFile();
                if (file != null) {
                    addAsNgView(project, settings, file, nodes, parsedComponents);
                }

            }

            nodes.add(child);
        }
        return nodes;
    }

    private void addAsNgView(Project project, ViewSettings settings, VirtualFile file,
                             ArrayList<AbstractTreeNode> nodes, Set<String> parsedComponents) {

        String fileName = file.getName();
        boolean isComponentDirectory = fileName.contains(Constants.COMPONENT_DELIMITER);

        if (isComponentDirectory) {
            PsiFileNode node = createNgViewPsiNode(project, file, settings);

            String parsedComponent = fileName.split("\\.")[0];
            if (!parsedComponents.contains(parsedComponent)) {
                parsedComponents.add(parsedComponent);
                nodes.add(node);
            }
        }
    }

    private PsiFileNode createNgViewPsiNode(Project project, VirtualFile file, ViewSettings settings) {
        PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText(
                "." + Constants.EXTENSION,
                Language.findLanguageByID("TEXT"),
                "", true, true, false, file
        );

        return new PsiFileNode(project, psiFile, settings);
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }
}