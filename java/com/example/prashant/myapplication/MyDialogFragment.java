package com.example.prashant.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Prashant on 05-Apr-2015.
 */
    public class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Data already exists. Over-write?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           /* Toast toast = Toast.makeText(getActivity(), "Over-written!", Toast.LENGTH_SHORT);
                            toast.show();*/
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           /* Toast toast = Toast.makeText(getActivity(), "Reverting!", Toast.LENGTH_SHORT);
                            toast.show();*/
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
