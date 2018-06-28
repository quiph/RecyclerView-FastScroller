package com.qtalk.sample.fragments


import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.qtalk.sample.R
import com.qtalk.sample.adapters.ContactsAdapter
import kotlinx.android.synthetic.main.fragment_contacts.view.*

class ContactsFragment : Fragment() {

    private var mNameList: ArrayList<String> = ArrayList()
    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(view)
        {
            with(contacts_recycler_view)
            {
                layoutManager = LinearLayoutManager(activity)
                adapter = ContactsAdapter(activity,mNameList)
                addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity?.checkSelfPermission(android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),PERMISSIONS_REQUEST_READ_CONTACTS)
                }
                else {
                    if(mNameList.size == 0)
                        retrieveContacts()
                }
            }

        }

    }
    private fun retrieveContacts() {

        activity?.contentResolver?.let {
            it.query(ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts.DISPLAY_NAME),null,null,"${ContactsContract.Contacts.DISPLAY_NAME} ASC" ).use{
                while(it.moveToNext()){
                    mNameList.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)))
                }
            }
        }
        view?.contacts_recycler_view?.adapter?.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==PERMISSIONS_REQUEST_READ_CONTACTS&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            retrieveContacts()
        else
            Toast.makeText(activity,"Permission Denied",Toast.LENGTH_SHORT).show()
    }


}
