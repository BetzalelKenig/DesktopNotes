package com.betzalelkenig.notes;

import datamodel.Note;
import datamodel.NoteData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    @FXML
    private Button newButton;
    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<Note> filteredList;

    private Predicate<Note> wantAllItems;
    private Predicate<Note> wantTodaysItems;

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

        wantAllItems = new Predicate<Note>() {
            @Override
            public boolean test(Note note) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<Note>() {
            @Override
            public boolean test(Note note) {
                return (note.getDeadline().equals(LocalDate.now()));
            }
        };
        filteredList = new FilteredList<Note>(NoteData.getInstance().getNotes(), wantAllItems);

        SortedList<Note> sortedList = new SortedList<Note>(filteredList,
                new Comparator<Note>() {
                    @Override
                    public int compare(Note o1, Note o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

//        noteListView.setItems(NoteData.getInstance().getNotes());
        noteListView.setItems(sortedList);
        noteListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        noteListView.getSelectionModel().selectFirst();
        //colors according to the date
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
                            } else {
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
    public void handleKeyPressed(KeyEvent keyEvent) {
        Note selectedItem = noteListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
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

    @FXML
    public void handleFilterButton() {
        Note selectedItem = noteListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if (filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadlineLabel.setText("");
            } else if (filteredList.contains(selectedItem)) {
                noteListView.getSelectionModel().select(selectedItem);
            } else {
                noteListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
            noteListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit(){
        Platform.exit();
    }
}
