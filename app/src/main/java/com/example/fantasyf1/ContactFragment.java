/**
 * @file:           ContactFragment.java
 * @author:         CJ Larsen
 * @description:    fragment containing a ListView of the user's contacts. gets the user's contacts
 *                  from their phone and allows them to choice a contact to message
 */

package com.example.fantasyf1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactFragment extends Fragment {

    private Activity containerActivity = null;
    private View inflatedView = null;

    private ListView contactsListView;
    private ArrayAdapter<String> contactsAdapter = null;
    private ArrayList<String> contacts = new ArrayList<String>();

    public ContactFragment() {}

    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_contact, container, false);
        return inflatedView;
    }

    /**
     * once view is created, create contact list for user
     * @param savedInstance
     */
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getContacts();
    }

    /**
     * ready to roll -> actually update the ListView with contacts
     */
    @Override
    public void onResume() {
        super.onResume();
        setupContactsAdapter();
    }

    /**
     * accesses the user's contacts, create ArrayList of contact Strings
     */
    public void getContacts() {
        int limit = 1000;
        Cursor cursor = containerActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cursor.moveToNext() && limit > 0) {
            /** Create content string */
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            @SuppressLint("Range") String given = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contacts.add(given + " :: " + id);
        }
        cursor.close();
    }

    /**
     * once ArrayList of contacts is complete, populate the ListView with contacts
     */
    private void setupContactsAdapter() {
        contactsListView = (ListView) containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new ArrayAdapter<String>(containerActivity, R.layout.row_contact, R.id.contact_row_text_view, contacts);
        contactsListView.setAdapter(contactsAdapter);
    }
}