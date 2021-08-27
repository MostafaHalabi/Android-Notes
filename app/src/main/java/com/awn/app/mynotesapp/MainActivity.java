package com.awn.app.mynotesapp;

import android.Manifest;
import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.awn.app.mynotesapp.adapter.NoteAdapter;
import com.awn.app.mynotesapp.db.NoteHelper;
import com.awn.app.mynotesapp.entity.Note;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.awn.app.mynotesapp.FormAddUpdateActivity.REQUEST_UPDATE;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener{
    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;

    private LinkedList<Note> list;
    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String [] storagePermissions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Notes");

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        rvNotes = (RecyclerView)findViewById(R.id.rv_notes);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(this);

        noteHelper = new NoteHelper(this);
        noteHelper.open();

        list = new LinkedList<>();

        adapter = new NoteAdapter(this);
        adapter.setListNotes(list);
        rvNotes.setAdapter(adapter);

        new LoadNoteAsync().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add){
            Intent intent = new Intent(MainActivity.this, FormAddUpdateActivity.class);
            startActivityForResult(intent, FormAddUpdateActivity.REQUEST_ADD);
        }
    }

    private class LoadNoteAsync extends AsyncTask<Void, Void, ArrayList<Note>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (list.size() > 0){
                list.clear();
            }
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return noteHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            progressBar.setVisibility(View.GONE);

            list.addAll(notes);
            adapter.setListNotes(list);
            adapter.notifyDataSetChanged();

            if (list.size() == 0){
                showSnackbarMessage("No data at this time");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FormAddUpdateActivity.REQUEST_ADD){
            if (resultCode == FormAddUpdateActivity.RESULT_ADD){
                new LoadNoteAsync().execute();
                showSnackbarMessage("One item added successfully");
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        }
        else if (requestCode == REQUEST_UPDATE) {

            if (resultCode == FormAddUpdateActivity.RESULT_UPDATE) {
                new LoadNoteAsync().execute();
                showSnackbarMessage("One item was successfully modified");
                // int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                // rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);
            }

            else if (resultCode == FormAddUpdateActivity.RESULT_DELETE) {
                int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                list.remove(position);
                adapter.setListNotes(list);
                adapter.notifyDataSetChanged();
                showSnackbarMessage("One item has been successfully deleted");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null){
            noteHelper.close();
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_import:
                if(checkStoragePermission()){
                    //permission allowed
                    importCSV();
                }else{
                    //permission not allowed
                    requestStoragePermissionImport();
                }
                break;

            case R.id.action_export:
                if(checkStoragePermission()){
                    //permission allowed
                    exportCSV();
                }else{
                    //permission not allowed
                    requestStoragePermissionExport();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkStoragePermission(){
        /*
        Checking if storage permission is enabled or not and returning true/false
        */
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissionImport(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE_EXPORT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         /*
       handling permission result
        */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case STORAGE_REQUEST_CODE_EXPORT:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    exportCSV();
                }
                else{
                    //permission denied
                    Toast.makeText(getApplicationContext(),"Storage permission required...", Toast.LENGTH_SHORT).show();
                }

            }
            break;

            case STORAGE_REQUEST_CODE_IMPORT:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    importCSV();
                }
                else{
                    //permission denied
                    Toast.makeText(getApplicationContext(),"Storage permission required...", Toast.LENGTH_SHORT).show();
                }

            }
            break;
        }
    }

    private void importCSV() {

            String filePathAndName = Environment.getExternalStorageDirectory()+"/SQLiteBackup/"+"SQLITE_export.csv";

            File csvFile = new File (filePathAndName);

            //check if file exists or not
            if(csvFile.exists()){

                try{
                    CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsolutePath()));

                    String[] nextLine;
                    while((nextLine = csvReader.readNext()) != null){
                        String id= nextLine[0];
                        String date = nextLine[1];
                        String title = nextLine[2];
                        String description = nextLine[3];

                        Note newNote = new Note();
                        newNote.setId(Integer.parseInt(id));
                        newNote.setDate(date);
                        newNote.setTitle(title);
                        newNote.setDescription(description);
                        noteHelper.insert(newNote);

                        Toast.makeText(getApplicationContext(),"Notes Imported Successfully",Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }else{
                Toast.makeText(getApplicationContext(),"no file was found..",Toast.LENGTH_SHORT).show();
            }

    }

    private void exportCSV() {

        //path for csv file
        File folder = new File(Environment.getExternalStorageDirectory()+"/"+"SQLiteBackup"); //SQLiteExport is the folder name

        boolean isFolderCreated = false;
        if(!folder.exists()){
            isFolderCreated = folder.mkdir();//create folder if it doesn't exist
        }

        //file name
        String csvFileName = "SQLITE_export.csv";

        //complete path and name
        String filePathAndName = folder.toString()+"/"+csvFileName;

        //get records
        ArrayList<Note> noteList = new ArrayList<>();
        noteList.clear();
        noteList = noteHelper.query();

        try{
            //write csv file
            FileWriter fw = new FileWriter(filePathAndName);
            for(int i =0;i<noteList.size();i++) {
                fw.append("" + noteList.get(i).getId()); //id
                fw.append(",");
                fw.append("" + String.valueOf(noteList.get(i).getDate())); //date
                fw.append(",");
                fw.append("" + noteList.get(i).getTitle()); //title
                fw.append(",");
                fw.append("" + noteList.get(i).getDescription()); //description
                fw.append("\r\n");
            }
            fw.flush();
            fw.close();

            Toast.makeText(getApplicationContext(),"Exported to: "+filePathAndName,Toast.LENGTH_LONG).show();
        }catch (Exception e){

            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}