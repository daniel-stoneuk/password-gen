package com.evo.passwordgenerator.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evo.passwordgenerator.R;
import com.evo.passwordgenerator.data.PasswordContract;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class GenerateFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private Button clipbutton;
    private Button clearbutton;
    private Button savebutton;
    private TextView generatedPassword;
    private String passwToSave;
    private DiscreteSeekBar charNumber;

    public int type = 0;
    public static final String ARG_TYPE = "argType";

    public GenerateFragment() {
        // Required empty public constructor
    }

    public static GenerateFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);

        GenerateFragment fragment = new GenerateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base2, container, false);

        generatedPassword = (EditText) view.findViewById(R.id.generatedPassword);
        generatedPassword.setMovementMethod(new ScrollingMovementMethod());
        clipbutton = (Button) view.findViewById(R.id.clipboard_button);
        clearbutton = (Button) view.findViewById(R.id.clear_button);
        savebutton = (Button) view.findViewById(R.id.save_button);
        charNumber = (DiscreteSeekBar) view.findViewById(R.id.charNumber);
        context = getActivity().getApplicationContext();

        generatedPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        charNumber.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                switch (type) {
                    case 0: {
                        Pass_Alpha pword = new Pass_Alpha(value);
                        passwToSave = pword.randomChar(context);
                        break;
                    }
                    case 1: {
                        Pass_Num pword = new Pass_Num(value);
                        passwToSave = pword.randomChar(context);
                        break;
                    }
                    case 2: {
                        Pass_AlphaNumSym pword = new Pass_AlphaNumSym(value);
                        passwToSave = pword.randomChar(context);
                        break;
                    }
                }
                generatedPassword.setText(passwToSave);
                savebutton.setEnabled(true);
                savebutton.setText(R.string.save_text);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        clipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(generatedPassword.getText().length() > 0){
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", generatedPassword.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    // TODO: show snackbar
                } else {
                    // TODO: show nothing to show snackbar
                }
            }
        });

        clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (charNumber.getProgress() > 0 || generatedPassword.getText().length() > 0) {
                    charNumber.setProgress(1);
                    generatedPassword.setText("");
                    hide();
                    savebutton.setEnabled(true);
                    savebutton.setText(R.string.save_text);
                }
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passwToSave.isEmpty()) {

                    ContentValues values = new ContentValues();

                    values.put(PasswordContract.PasswordEntry.COLUMN_PASSWORD, passwToSave);
                    values.put(PasswordContract.PasswordEntry.COLUMN_TIME, System.currentTimeMillis());

                    getActivity().getContentResolver().insert(PasswordContract.PasswordEntry.CONTENT_URI, values);

                    savebutton.setEnabled(false);
                    savebutton.setText(R.string.saved_text);
                }
            }
        });

        return view;

    }

    private void hide() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        generatedPassword.clearFocus();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
