package com.example.sherrylim.androidlabs;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

public class MessageFragment extends Fragment {

    private boolean isTablet;
    private TextView messageView;
    private TextView idView;
    private Button delete;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View gui = inflater.inflate(R.layout.activity_message_fragment, container, false);
        messageView =(TextView) gui.findViewById(R.id.messageView);
        idView =(TextView) gui.findViewById(R.id.mid);
        delete = (Button) gui.findViewById(R.id.deleteButton);

        bundle = getArguments();

        String message = bundle.getString("Message");
        final long id = bundle.getLong("ID");
        final long id_inChat= bundle.getLong("IDInChat");

        messageView.setText(message);
        idView.setText(String.valueOf(id));

        delete.setOnClickListener((view)->{
            if(isTablet){
                ChatWindow cw = (ChatWindow)getActivity();
                cw.deleteForTablet(id, id_inChat);
                getFragmentManager().beginTransaction().remove(MessageFragment.this).commit();
            }else{
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DeleteTD", id);
                resultIntent.putExtra("IDInChat", id_inChat);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }
                }

                );

        return gui;//lab7 done
    }

    public void setIsTablet(boolean isTablet){
        this.isTablet=isTablet;
    }
}
