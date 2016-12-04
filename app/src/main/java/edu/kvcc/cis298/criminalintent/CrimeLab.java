package edu.kvcc.cis298.criminalintent;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by dbarnes on 10/19/2016.
 */
public class CrimeLab {
    //Static variable to hold the instance of the CrimeLab
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    //This context will be the hosting activity. It will get
    //assigned in the constructor.
    private Context mContext;

    //This is a static get method to get the single instance of the class.
    public static CrimeLab get(Context context) {
        //If we don't have an instance, we create a new one.
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        //Regardless of whether we created it, or
        //it already existed, we need to return it
        return sCrimeLab;
    }

    //This is the constructor. It is private.
    //It can't be used from outside classes.
    private CrimeLab(Context context) {
        //Create a new array list of crimes
        mCrimes = new ArrayList<>();
        //Set the class level context to the one passed in.
        mContext = context;

        //Load the crime list
        loadCrimeList();
    }

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        //This is a foreach loop. Syntax is different than C#
        for (Crime crime : mCrimes) {
            //If the current crime we are looking at has a id that
            //matches the passed in one, we return the crime
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        //Didn't find a match, return null.
        return null;
    }

    private void loadCrimeList() {

        //Define a Scanner which will be used to read in the file
        Scanner scanner = null;

        try {
            //instantiate a new scanner
            //Use some method chaining to get access to the file.
            //Use the context to get at resources, and once we have the resources
            //call openRawResource passing in the location of the file.
            //The location of the file will be R.raw."The name of the file"
            //Remember that files here must be all lowercase.
            scanner = new Scanner(mContext.getResources().openRawResource(R.raw.crimes));

            //while the scanner has another line to read
            while(scanner.hasNextLine()) {

                //Get the next line and split it into parts
                String line = scanner.nextLine();
                String parts[] = line.split(",");

                //Assign each part to a local variable
                String stringUUID = parts[0];
                String title = parts[1];
                String stringDate = parts[2];
                String stringSolved = parts[3];

                //Setup some vars for doing parsing
                UUID uuid = UUID.fromString(stringUUID);

                //Create a new DateFormat to use in parsing the date
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                //Use the new format to parts the string date.
                Date date = format.parse(stringDate);

                //Set a bool as to whether it is solved or not
                //This is a ternary operator. It is a shorthand if else
                //The part between the ? and : happens when true.
                //The part after the : happens when false.
                boolean solved = (stringSolved.equals("1")) ? true : false;

                //Add the Crime to the list
                mCrimes.add(new Crime(uuid, title, date, solved));
            }

        } catch (Exception e) {
            //On exception, just log out the exception to string
            Log.e("Read CSV", e.toString());
        } finally {
            //Check to make sure that the scanner actually got instantiated
            //and if so, close it.
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}
