package com.betzalelkenig.notes;

import datamodel.Note;
import datamodel.NoteData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private List<Note> notes;
    @FXML
    private ListView<Note> noteListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;

    public void initialize(){
/*        Note item1 = new Note("Java", "JavaFX and other proj",
                LocalDate.of(2020, Month.MAY,1));

        Note item2 = new Note("Linux", "tutorial and shell in cw",
                LocalDate.of(2020, Month.MAY,20));
        Note item3 = new Note("crypto", "wiki & cw & math",
                LocalDate.of(2020, Month.APRIL,1));

        notes = new ArrayList<Note>();
        notes.add(item1);
        notes.add(item2);
        notes.add(item3);

        NoteData.getInstance().setNotes(notes);*/

        noteListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {
            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note note, Note t1) {
                if(t1 != null){
                    Note item = noteListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d, MMMM, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        noteListView.getItems().setAll(NoteData.getInstance().getNotes());
        noteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteListView.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleClickListView(){
        Note item = noteListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        deadlineLabel.setText(item.getDeadline().toString());
//        System.out.println("The seelected item " + item);
//        StringBuilder sb = new StringBuilder(item.getDetails());
//        sb.append("\n\n\n\n");
//        sb.append("Date: ");
//        sb.append(item.getDeadline().toString());
//        itemDetailsTextArea.setText(sb.toString());
    }

}
