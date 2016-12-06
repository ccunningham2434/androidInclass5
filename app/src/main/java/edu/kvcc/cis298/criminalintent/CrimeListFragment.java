package edu.kvcc.cis298.criminalintent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeListFragment extends Fragment {

    // >Tag for the logcat.
    private static final String TAG = "CrimeListFragment";

    //String key for saving whether to show the subtitle or not
    private static String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mCrimeRecyclerView;
    //Declare a new crimeAdapter. This comes from the inner class
    //that is written below in this file.
    private CrimeAdapter mAdapter;

    //Bool to know whether the subtitle is being shown or not.
    private boolean mSubtitleVisible;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Tell the fragment manager that this fragment has an
        //options menu that should be displayed. This will ensure
        //that the fragment manager makes a call to the
        //onCreateOptionsMenu method, which does the work of
        //inflating the menu and displaying it.
        setHasOptionsMenu(true);

        // >This will make a new instance of teh FetchCrimesTask private inner class. When the execute...
        // >...method get called on it, the FetchCrimesTask will start the doInBackground method on a...
        // >...seperate thread automatically for us.
        new FetchCrimesTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate the view from the layout file into a view variable
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        //Get a handle to the recycler view using findViewById
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        //Set the layout manger for the recyclerview. It needs to know how to layout
        //the individual views that make up the recyclerView. This linear layout manager
        //will tell the recyclerView to lay them out in a vertical fashion.
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Get the bool that represents whether to show the subtitle from the bundle
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        //Call the updateUI method which will setup the recyclerView with data.
        updateUI();

        //return the view
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        //Add a title variable to the viewHolder
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        //Variable to hold a single crime
        private Crime mCrime;

        //Constructor for the CrimeHolder.
        public CrimeHolder(View itemView) {
            //Call the parent constructor
            super(itemView);
            itemView.setOnClickListener(this);

            //Get the itemView that is passed into this constructor and assign it
            //to the class level variable.
            mTitleTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView
                    .findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.list_item_crime_solved_check_box);
        }

        //Write a method in here to take in an instance of a crime
        //and then assign the crime properties to the various
        //view widgets
        public  void bindCrime(Crime crime) {
            //Assign the passed in crime to the class level one
            //This may not be needed? Could use the local one.
            //Maybe we will need it later on.
            mCrime = crime;
            //Set the widget controls with the data from the crime.
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        //Setup local version of the list of crimes
        private List<Crime> mCrimes;

        //Constructor to set the list of crimes
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }


        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Get a reference to a layout inflator that can inflate our view
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //Use the inflator to inflate the default android list view.
            //We did not write this layout file. It is a standard android one.
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            //Return a new crimeHolder and pass in the view we just created.
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            //Get the crime out of the list of crimes that is declared
            //on the inner adapter class we wrote.
            Crime crime = mCrimes.get(position);
            //Set the data from the crime object
            //to the viewHolders various widgets.
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //This will update the UI when returning to this fragment
        //from the detail view one.
        updateUI();
    }

    //Override method that gets called when the menu is created
    //This method will be called by the fragment manager. The
    //fragment manager will know to call this method because
    //in the onCreate method we set a bool on the fragment
    //indicating that this fragment has a menu that must be
    //displayed. The fragment manager will check that bool and then
    //call this method when creating the fragment.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    //This method gets called everytime any item in the menu is selected
    //It is the job of the programmer to determine which item was selected
    //and then handle it appropriately
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //The method has the item that was selected come into the method
        //We can run a switch on the id of the menu item that was selected
        switch (item.getItemId()) {
            //if the id matches the add new crime item id, which is the id
            //from the layout file for the add crime item.
            case R.id.menu_item_new_crime:
                //make a new crime
                Crime crime = new Crime();
                //Get the singleton crime lab, and add the new crime
                CrimeLab.get(getActivity()).addCrime(crime);
                //Make a new intent to start up the crime pager activity
                //Note, that we query the crimepageractivity class to get
                //a properly formatted intent.
                Intent intent = CrimePagerActivity
                        .newIntent(getActivity(), crime.getId());
                //Start the activity with the new intent. No need to start
                //for a result because we do not need a result.
                startActivity(intent);
                //Return true to signify that no more processing is needed.
                return true;

            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Save the show subtitle bool to the bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    //****************************************
    //Private Methods
    //****************************************
    private void updateUI() {
        //This is using the static method on the CrimeLab class
        //to return the singleton. This will get us our one and only one
        //instance of the CrimeLab. There can be only one!!!
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Get the list of crimes from the singleton CrimeLab.
        //This will give us a local reference to the list of crimes
        //that we can send over to the CrimeAdapter.
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            //Create a new CrimeAdapter and send the crimes we just got
            //over to it. This way the adapter will have the list of crimes
            //it can use to make new viewholders and bind data to the viewholder
            mAdapter = new CrimeAdapter(crimes);

            //Set the adapter on the recyclerview.
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            //Notify the adapter that data may have changed and that
            //it should reload the data using the existing adapter.
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        //Get the crimelab
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        //Get all the crimes in the crime lab and get the size of that list
        int crimeCount = crimeLab.getCrimes().size();
        //Create a local string from the string resource that is
        //the subtitle format. The format may be incorrect depending on the
        //version of android you are developing on??? We have the format
        //set as a string denoted by %1$s, however it might need to be
        //%1$d. S is for string, d is for double? diget? It's for numbers!
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        //If we are hiding the subtitle, set the subtitle to null.
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        //Get a reference to the activity so that we can set the subtitle
        //of the action bar, which is also called the tool bar.
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    // >Make an inner class that extends from AsyncTask. The Generic types that are passed in are to...
    // >...<Params, Progress, Result>. They are types that can be used by the methods that must be overidden.
    // >We will have VOID for all of them at the moment.
    private class FetchCrimesTask extends AsyncTask<Void, Void, Void> {
        // >This is the method that will be executed on the separate thread.
        // >Once it complete teh onPostExecute method will be called automatically.
        @Override
        protected Void doInBackground(Void... params) {
            new CrimeFetcher().fetchCrimes();
            return null;
        }

        // >Method that will automatically get called when the code in doInBackground gets done executing.
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
