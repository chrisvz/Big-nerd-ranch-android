package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import static android.provider.ContactsContract.*;
import static android.provider.ContactsContract.CommonDataKinds;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

    private String contactID;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setSubject(getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();
                startActivity(intent);

            }
        });



        final Intent pickContact = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);

        }


        mCallSuspectButton= (Button) v.findViewById(R.id.call_suspect_btn);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v){

               if(mCrime.getPhone() != null) {
                   Intent callContact = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCrime.getPhone()));
                   startActivity(callContact);
               }
            };
        });
        return v;
    }

    private String getCrimeReport() {
        String solvedString = null;

        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat , mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,mCrime.getTitle(),dateString,solvedString,suspect);
        return report;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }

        else if (requestCode == REQUEST_CONTACT && data != null) {

            Uri contactUri = data.getData();
            queryContact(contactUri);


            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            queryPhone(uri);

        }
    }





    private void queryContact(Uri uri) {
        String[] queryForFields= new String[] {
                Contacts.DISPLAY_NAME,  Contacts._ID ,
        };

        Cursor cursor = query(uri, queryForFields, null, null, null);

        try {
            if(cursor.getCount() == 0) {
                return ;
            }
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            contactID = cursor.getString(1);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);
        }
        finally {
            cursor.close();
        }
    }

    private void queryPhone(Uri uri) {

        String[] column  = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
        };

        String field = CommonDataKinds.Phone.CONTACT_ID;

        Cursor phoneCursor = query(uri, column, field + "= ?", new String[]{contactID}, null);

        try {
            if(phoneCursor.getCount() == 0) {
                return ;
            }
            phoneCursor.moveToFirst();
            mCrime.setPhone(phoneCursor.getString(0));
        }finally {
            phoneCursor.close();

        }

    }

    private Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = getActivity().getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder

        );
        return cursor;
    }


    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
}
