package com.betzalelkenig.notes;

import datamodel.Note;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private List<Note> notes;
    @FXML
    private ListView<Note> noteListView;
    @FXML
    private TextArea itemDetailsTextArea;
    public void initialize(){
        Note item1 = new Note("Java", "JavaFX and other proj",
                LocalDate.of(2020, Month.MAY,1));

        Note item2 = new Note("Linux", "toturial and shell in cw",
                LocalDate.of(2020, Month.MAY,20));
        Note item3 = new Note("crypto", "wiki & cw & math",
                LocalDate.of(2020, Month.APRIL,1));

        notes = new ArrayList<Note>();
        notes.add(item1);
        notes.add(item2);
        notes.add(item3);

        noteListView.getItems().setAll(notes);
        noteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    public void handleClickListView(){
        Note item = noteListView.getSelectionModel().getSelectedItem();
//        System.out.println("The seelected item " + item);
        itemDetailsTextArea.setText(item.getDetails());
    }

}
