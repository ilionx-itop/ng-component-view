package com.gerhardboer.fileditor.state;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@State(
        name = "NgComponentViewState",
        storages = {
                @Storage(StoragePathMacros.WORKSPACE_FILE)
        }
)
public class NgComponentViewState implements PersistentStateComponent<NgComponentViewState> {

    private Map<String, NgEditorOpenFileState> fileStates = new HashMap<>();

    @Nullable
    @Override
    public NgComponentViewState getState() {
        return this;
    }

    @Override
    public void loadState(NgComponentViewState ngComponentViewState) {
        XmlSerializerUtil.copyBean(ngComponentViewState, this);
    }

    @Nullable
    public static NgComponentViewState getInstance(Project project) {
        return ServiceManager.getService(project, NgComponentViewState.class);
    }

    public NgEditorOpenFileState getFileState(String fileName) {
        NgEditorOpenFileState currentState = this.fileStates.get(fileName);
        if (currentState == null) {
            currentState = new NgEditorOpenFileState();
            this.fileStates.put(fileName, currentState);
        }

        return currentState;
    }

    public class NgEditorOpenFileState {
        public boolean ts = true;
        public boolean html = true;
        public boolean css = true;

        public void set(final String name, boolean newState) {
            if (name.endsWith(".ts"))
                this.ts = newState;

            if (name.endsWith(".html"))
                this.html = newState;

            if (name.endsWith(".css"))
                this.css = newState;
        }

        public boolean get(final String name) {
            if (name.endsWith(".ts")) {
                return this.ts;
            }

            if (name.endsWith(".html")) {
                return this.html;
            }

            if (name.endsWith(".css")) {
                return this.css;
            }

            return true;
        }


    }
}

