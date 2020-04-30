package com.betzalelkenig.notes;

import datamodel.Note;
import datamodel.NoteData;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    public Note processResult() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadLineValue = deadlinePicker.getValue();

        Note newItem = new Note(shortDescription, details, deadLineValue);
        NoteData.getInstance().addNoteItem(newItem);
        return newItem;
    }


}
