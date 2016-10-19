package edu.kvcc.cis298.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    //Declare a new crimeAdapter. This comes from the inner class
    //that is written below in this file.
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Inflate the view from the layout file into a view variable
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        //Get a handle to the recycler view using findViewById
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        //Set the layout manger for the recyclerview. It needs to know how to layout
        //the indiviual views that make up the recyclerView. This linear layout manager
        //will tell the recyclerView to lay them out in a vertical fashion.
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Call the updateUI method which will setup the recyclerView with data.
        updateUI();

        //return the view
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {

        //Add a title variable to the viewHolder
        public TextView mTitleTextView;

        //Constructor for the CrimeHolder.
        public CrimeHolder(View itemView) {
            //Call the parent constructor
            super(itemView);
            //Get the itemView that is passed into this constructor and assign it
            //to the class level variable.
            mTitleTextView = (TextView) itemView;
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
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            //Return a new crimeHolder and pass in the view we just created.
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            //Get the crime out of the list of crimes that is declared
            //on the inner adapter class we wrote.
            Crime crime = mCrimes.get(position);
            //Set the text on the viewHolders textview widget.
            holder.mTitleTextView.setText(crime.getTitle());
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    private void updateUI() {
        //This is using the static method on the CrimeLab class
        //to return the singleton. This will get us our one and only one
        //instance of the CrimeLab. There can be only one!!!
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        //Get the list of crimes from the singleton CrimeLab.
        //This will give us a local reference to the list of crimes
        //that we can send over to the CrimeAdapter.
        List<Crime> crimes = crimeLab.getCrimes();

        //Create a new CrimeAdapter and send the crimes we just got
        //over to it. This way the adapter will have the list of crimes
        //it can use to make new viewholders and bind data to the viewholder
        mAdapter = new CrimeAdapter(crimes);

        //Set the adapter on the recyclerview.
        mCrimeRecyclerView.setAdapter(mAdapter);
    }
}