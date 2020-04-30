package com.betzalelkenig.notes;

import datamodel.Note;
import datamodel.NoteData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Controller {

    private List<Note> notes;
    @FXML
    private ListView<Note> noteListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Note note = noteListView.getSelectionModel().getSelectedItem();
                deleteItem(note);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        noteListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Note>() {


            @Override
            public void changed(ObservableValue<? extends Note> observableValue, Note note, Note t1) {
                if (t1 != null) {
                    Note item = noteListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d, MMMM, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        noteListView.setItems(NoteData.getInstance().getNotes());
        noteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteListView.getSelectionModel().selectFirst();
        //red if the date pass
        noteListView.setCellFactory(new Callback<ListView<Note>, ListCell<Note>>() {
            @Override
            public ListCell<Note> call(ListView<Note> noteListView) {
                ListCell<Note> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(Note note, boolean b) {
                        super.updateItem(note, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(note.getShortDescription());
                            if (note.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (note.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.ORANGE);
                            } else setTextFill(Color.GREEN);
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            }else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Item");
        dialog.setHeaderText("Use this dialog to create a new Note");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("noteitemdialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            Note newItem = controller.processResult();
//            noteListView.getItems().setAll(NoteData.getInstance().getNotes());
            noteListView.getSelectionModel().select(newItem);
        }


    }

    @FXML
    public void handleClickListView() {
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

    public void deleteItem(Note item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Note");
        alert.setHeaderText("Delete note: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            NoteData.getInstance().deleteNote(item);
        }
    }
}
