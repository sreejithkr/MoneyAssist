package com.skr.expensetrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.skr.AppController;
import com.skr.datahelper.ArchiveDBHelper;
import com.skr.datahelper.DBHelper;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UtilitiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UtilitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UtilitiesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Boolean proceedFlag = false;
    private String onProceedString = "";

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UtilitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UtilitiesFragment newInstance(String param1, String param2) {
        UtilitiesFragment fragment = new UtilitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UtilitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mListener != null) {
            mListener.onCreatedFragment();
        }

        View rootView = inflater.inflate(R.layout.fragment_utilities, container, false);

        rootView.findViewById(R.id.archive_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog();
            }
        });
        rootView.findViewById(R.id.load_from_Archive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onLoadFromArchiveClicked();
                }

              //  ArchiveDBHelper.reloadTheDataBAseFromArchive("");
            }
        });

        rootView.findViewById(R.id.export_to_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLoadExportToExcelClicked();
                }
               // ExportToExcelActivity
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final File dbpath = getActivity().getApplication().getDatabasePath(DBHelper.DATABASE_NAME);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_archive_confirmation, null);
        final TextView error_message_dialog_archive = (TextView)view.findViewById(R.id.error_message_dialog_archive);
        error_message_dialog_archive.setTextColor(getResources().getColor(R.color.text_normal));
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.archive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onProceedString = "";
                        proceedFlag = false;
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // sign in the user ...
                EditText editText = (EditText) view.findViewById(R.id.editTextArchiveName);
                String textValue = editText.getText().toString();

                proceedFlag = textValue.equalsIgnoreCase(onProceedString);
                if (textValue.length() > 15) {
                    error_message_dialog_archive.setTextColor(getResources().getColor(R.color.error_red));

                    error_message_dialog_archive.setText(getString(R.string.validation_archive_name_length));
                    error_message_dialog_archive.setVisibility(View.VISIBLE);

                    return;
                }
                if (!AppController.isAlphaNumeric(textValue)){
                    error_message_dialog_archive.setTextColor(getResources().getColor(R.color.error_red));

                    error_message_dialog_archive.setText(getString(R.string.validation_archive_name));
                    error_message_dialog_archive.setVisibility(View.VISIBLE);

                    return;
                }
                if(ArchiveDBHelper.ifFileAlreadyExist(textValue) && !proceedFlag){
                    error_message_dialog_archive.setTextColor(getResources().getColor(R.color.error_red));
                    error_message_dialog_archive.setText(getString(R.string.validation_archive_name_already_exist));
                    error_message_dialog_archive.setVisibility(View.VISIBLE);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(getString(R.string.proceed));
                    proceedFlag = true;
                    onProceedString = textValue;
                    return;
                }
                onProceedString = "";
                proceedFlag = false;
                ArchiveDBHelper.exportDB(textValue, dbpath, getActivity());
                dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onCreatedFragment();
        public void onArchiveButtonClicked();
        public void onLoadFromArchiveClicked();
        public void onLoadExportToExcelClicked();
    }

}
