package com.example.customviewstuff.activities.soccer;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

public class BindingCommand {
    public ObservableField<String> helpText;
    public ObservableBoolean showMainPanel, showButtons;

    public BindingCommand() {
        helpText = new ObservableField<>("确保联机设备在同一网络下，开热点设备作为主机");
        showMainPanel = new ObservableBoolean(true);
        showButtons = new ObservableBoolean(true);
    }

    public void showMainPanel(boolean show) {
        showMainPanel.set(show);
    }

    public void showButtons(boolean show) {
        showButtons.set(show);
    }

    public void setHelpText(String text) {
        helpText.set(text);
    }
}
