package datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class NoteData {
    private static NoteData instance = new NoteData();
    private static String filename = "NoteItem.txt";

    private ObservableList<Note> notes;
    private DateTimeFormatter formatter;

    public static NoteData getInstance(){
        return instance;
    }

    private NoteData(){
        formatter = DateTimeFormatter.ofPattern("dd, MM, yyyy");
    }

    public ObservableList<Note> getNotes() {
        return notes;
    }

    public void addNoteItem(Note item){
        notes.add(item);
    }


    public void loadNotes() throws IOException{
        notes = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try {
            while ((input = br.readLine()) != null){
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                Note note = new Note(shortDescription, details, date);
                notes.add(note);
            }
        }finally {
            if(br != null){
                br.close();
            }
        }
    }

    public void storeNotes() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<Note> iter = notes.iterator();
            while (iter.hasNext()){
                Note note = iter.next();
                bw.write(String.format("%s\t%s\t%s\t",
                        note.getShortDescription(),
                        note.getDetails(),
                        note.getDeadline().format(formatter)));
                bw.newLine();
            }
        }finally {
            if (bw != null) bw.close();
        }
    }

    public void deleteNote(Note item){
        notes.remove(item);
    }



}
