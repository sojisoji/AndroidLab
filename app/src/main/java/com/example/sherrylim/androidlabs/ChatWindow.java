package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ChatWindow extends Activity {
    protected static final String ACTIVITY_NAME ="ChatWindow";
    protected SQLiteDatabase db;
    protected boolean FrameExists;
    protected Cursor cursor;

    String tableName = ChatDatabaseHelper.TABLE_NAME;
    String keyID = ChatDatabaseHelper.KEY_ID;
    String keyMsg = ChatDatabaseHelper.KEY_MESSAGE;
    ArrayList<String> messages = new ArrayList<>();
    ChatAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        FrameExists = (findViewById(R.id.frame)!=null);

        ListView chat = findViewById(R.id.chat);
        EditText textInput = findViewById(R.id.chatText);
        Button send = findViewById(R.id.send_button);
        messageAdapter =new ChatAdapter( this );
        chat.setAdapter (messageAdapter);
        ChatDatabaseHelper myOpener = new ChatDatabaseHelper(this);
        db = myOpener.getWritableDatabase();

        cursor =db.rawQuery("SELECT * FROM "+ChatDatabaseHelper.TABLE_NAME+";", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast() ) {
            messages.add(cursor.getString( cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
            Log.i(ACTIVITY_NAME, "Cursor's  column count =" + cursor.getColumnCount() );
            for(int k =0; k<cursor.getColumnCount(); k++){
            Log.i(ACTIVITY_NAME, "Column name" + cursor.getColumnName(k));
            }
        }

        send.setOnClickListener(e ->{
                messages.add(textInput.getText().toString());
                ContentValues cv = new ContentValues();
                cv.put(ChatDatabaseHelper.KEY_MESSAGE,textInput.getText().toString()  );
                db.insert(ChatDatabaseHelper.TABLE_NAME, "Null replacement value", cv);
            cursor =db.rawQuery("SELECT * FROM "+ChatDatabaseHelper.TABLE_NAME+";", null);

            cursor.moveToFirst();

            messageAdapter.notifyDataSetChanged(); //this restarts the process of getCount() & getView()
                textInput.setText("");
            }
        );

        chat.setOnItemClickListener((adapterView, view, position, id) ->{
                String msg =messageAdapter.getItem(position);
                long ID = id;
                Long id_inChat = messageAdapter.getId(position);

                MessageFragment myFragment = new MessageFragment();
                Bundle infoToPass = new Bundle();
                infoToPass.putString("Message", msg);
                infoToPass.putLong("ID", ID);
                infoToPass.putLong("IDInChat", id_inChat);



                //if on tablet:
                if(FrameExists)
                {

                    myFragment.setArguments( infoToPass );
                    myFragment.setIsTablet(true);
                    getFragmentManager().beginTransaction().replace(R.id.frame,myFragment).commit();
                }
                else //this is a phone:
                {
                    myFragment.setIsTablet(false);
                    Intent next = new Intent(ChatWindow.this, MessageDetails.class);
                    next.putExtra("ChatItem", infoToPass);
                    startActivityForResult(next, 1, infoToPass);
                }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            long id = extras.getLong("DeleteID");
            long id_inChat = extras.getLong("IDInChat");
           // String query = "DELETE FROM Messages WHERE "+ keyID + " = " +id + ";";
            db.delete(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_ID + " = ?", new String[] {Long.toString(id)}) ;//.execSQL(query);
            messages.remove((int)id_inChat);
            cursor =db.rawQuery("SELECT * FROM "+ChatDatabaseHelper.TABLE_NAME+";", null);
            cursor.moveToFirst();
            messageAdapter.notifyDataSetChanged();
        }
    }

    public void deleteForTablet(long idInDatabase, long idInChat){
        long id = idInDatabase;
        long id_inChat = idInChat;
        db.delete(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_ID + " = ?", new String[] {Long.toString(id)}) ;
        messages.remove((int)id_inChat);
        cursor =db.rawQuery("SELECT * FROM "+ChatDatabaseHelper.TABLE_NAME+";", null);
        cursor.moveToFirst();
        messageAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();

    }

    private class ChatAdapter extends ArrayAdapter<String>{
         ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount(){
            return messages.size();
        }

        //*****step 6
        public long getItemId(int position){
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex("ID"));
        }

        public String getItem(int position){
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null ;

            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = result.findViewById(R.id.message_text);
            message.setText(   getItem(position)  ); // get the string at position
            return result;

        }

        public long getId(int position){
            return position;
        }

    }

    public class ChatDatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "Messages.db";
        public static final int VERSION_NUM = 5;
        public static final String TABLE_NAME = "Messages";
        public static final String KEY_ID = "ID";
        public static final String KEY_MESSAGE= "Message";

        public ChatDatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }
        public void onCreate(SQLiteDatabase db)
        {
            Log.i("ChatDatabaseHelper", "Calling onCreate");
            db.execSQL("CREATE TABLE " + TABLE_NAME + "( "+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MESSAGE + " text);" );
        }
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) // newVer > oldVer
        {
            Log.i("ChatDatabaseHelper", "Calling onUpgrade, oldVersion= " + oldVer + ", newVersion= " + newVer);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME ); //delete any existing data
            onCreate(db);  //make a new database
        }

        public void onDowngrade(SQLiteDatabase db, int oldVer, int newVer) // newVer > oldVer
        {
            Log.i("ChatDatabaseHelper", "Calling onDowngrade, oldVersion= " + oldVer + ", newVersion= " + newVer);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME ); //delete any existing data
            onCreate(db);  //make a new database
        }


    }
}
