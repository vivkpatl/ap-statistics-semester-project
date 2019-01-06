//File I/O imports as well as generic collections
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;


public class cleanser {

    //The location of the input file relative to the current file of code.
    final static String INPUT_FILE_LOCATION_RELATIVE = "canigetatry2/dataset_cleanse3.txt";

    //The location of the output file relative to the current file of code.
    final static String OUTPUT_FILE_LOCATION_RELATIVE = "NaN.txt";
    
    //The regex nightmare used to split the rows of the dataset into useful pieces of information.
    final static String FILE_PARSER_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    //The year from which the data we will use will be collected. Helps to avoid unnecessary lookups.
    final static int CUTOFF_YEAR = 1980;

    //The statistic that we really care about. Refers to the quantitative columns in the dataset.
    final static String STAT_OF_INTEREST = "PER";

    //The dataset contains multiple seasons for many players, often, we will just consider the last one.
    final static boolean IGNORE_MULTIPLES = true;

    //The raw data stored in an ArrayList for easy programmatic access.
    static ArrayList<String> _rawDataNotCleansed;

    //Reference to the input file.
    static Scanner _inputFileScanner;

    //Reference to the output file.
    static PrintStream _outputFilePrintStream;

    //The location of the output file relative to the current file of code.
    static ArrayList<PrintStream> BY_DECADE_FPATHS;



    //--------------------------------------------------------------
    //As the method name implies, this is the main method.
    //--------------------------------------------------------------
    public static void main(String[] args) throws FileNotFoundException {
        BY_DECADE_FPATHS = new ArrayList<PrintStream>();
        BY_DECADE_FPATHS.add(new PrintStream(new File("canigetatry2/final/dataset_1980s.csv")));
        BY_DECADE_FPATHS.add(new PrintStream(new File("canigetatry2/final/dataset_1990s.csv")));
        BY_DECADE_FPATHS.add(new PrintStream(new File("canigetatry2/final/dataset_2000s.csv")));
        BY_DECADE_FPATHS.add(new PrintStream(new File("canigetatry2/final/dataset_2010s.csv")));
        //Create the scanner with the input file location.
        _inputFileScanner = new Scanner(new File(INPUT_FILE_LOCATION_RELATIVE));
        
        //Create the PrintStream with the output file location
        _outputFilePrintStream = new PrintStream(new File(OUTPUT_FILE_LOCATION_RELATIVE));

        //Get the raw lines of the file.
        _rawDataNotCleansed = new ArrayList<String>();

        //Get all of the lines of the file.
        while (_inputFileScanner.hasNextLine()) {
            _rawDataNotCleansed.add(_inputFileScanner.nextLine());
        }

        //for (String s : _rawDataNotCleansed)
        //    _outputFilePrintStream.println(s);

        //Call the method of choice here after setting the constants above.
        //removeDataBeforeYear(CUTOFF_YEAR);
        //isolateLatestSeason();
        //cleanseForRelevantInfo();
        groupIntoDecades();

        //Avoid memory leaks.
        _inputFileScanner.close();
        _outputFilePrintStream.close();

        for (PrintStream pStream : BY_DECADE_FPATHS) {
            pStream.close();
        }
    }

    //--------------------------------------------------------------
    //Remove all of the rows that occur prior to the CUTOFF_YEAR
    //--------------------------------------------------------------
    public static void removeDataBeforeYear(int cutoffYear) {
        //Go through the raw data row by row, checking the year that the entry is for.
        //  If it is prior to 1979, don't print it to the PrintStream of the outputFile.
        for (int i = 0; i < _rawDataNotCleansed.size(); i++) {            
            try {
                //Check if the row is for a year after CUTOFF_YEAR and print it to the output file.
                //  The column with this info is column 2, but this is a program. Index 1 it is.
                if (Integer.parseInt(_rawDataNotCleansed.get(i).split(FILE_PARSER_REGEX)[1]) >= CUTOFF_YEAR) {
                    //The row of data is for a year after CUTOFF_YEAR. We can send it to the output file.
                    _outputFilePrintStream.println(_rawDataNotCleansed.get(i));
                }
            } catch (Exception e) {
                //Do nothing.
            }
        }
    }

    //--------------------------------------------------------------
    //Get the latest season for each player
    //--------------------------------------------------------------
    //Iterate through the collection with only 1 index tracking
    //  variable, and use a map to keep track of the occurences of
    //  a particular player's data (nearly every NBA/ABA player has
    //  more than 1 season)
    //--------------------------------------------------------------
    //Useful indices (2: Name)
    //--------------------------------------------------------------
    public static void isolateLatestSeason() {
        //We are using this monstrosity to deal with this problem.
        //  This is not space efficient but it will do.
        HashMap<String, ArrayList<String>> dearGodWhatIsThis = new HashMap<String, ArrayList<String>>();

        //Use a single index tracking variable
        for (int i = 0; i < _rawDataNotCleansed.size(); i++) {
            //The last name we just saw
            String lastObservedName = _rawDataNotCleansed.get(i).split(FILE_PARSER_REGEX)[2];

            //If the map does not contain this player's name in the keySet.
            //  In other words, if the data we are looking at has not previously been seen
            if (!dearGodWhatIsThis.keySet().contains(lastObservedName)) {
                dearGodWhatIsThis.put(lastObservedName, new ArrayList<String>());
            }

            //Now we keep iterating over data until the player no longer has data.
            //  In other words, when the lastObservedName does not match where we are currently at.
            while (lastObservedName.equals(_rawDataNotCleansed.get(i).split(FILE_PARSER_REGEX)[2]) && i < (_rawDataNotCleansed.size() - 1)) {
                //Add this row to the map's list for this name
                dearGodWhatIsThis.get(lastObservedName).add(_rawDataNotCleansed.get(i));

                //Move on to the next one, making sure we don't go to far.
                //  The edge case is taken care of in the while condition.
                i++;
            }
        }

        //Now, let's print the last season for each player to the output file
        for (ArrayList<String> playerEntries : dearGodWhatIsThis.values()) {
            if (playerEntries.size() > 0)
                _outputFilePrintStream.println(playerEntries.get(playerEntries.size() - 1));
        }
    }

    //--------------------------------------------------------------
    //Isolate a dataset that contains only age, PER, position, name.
    //--------------------------------------------------------------
    //In this method, we extract the relevant info from the raw data
    //  and store it in an output file, separated by commas.
    //--------------------------------------------------------------
    //Relevant indices (1: Year, 2: Name, 3: Position, 4: Age, Season PER: 9)
    //--------------------------------------------------------------
    public static void cleanseForRelevantInfo() {
        //Currently, the raw data has a lot of unnecessary info.
        //  Let's extract the meaningful portions of it.
        for (String unsplitRow : _rawDataNotCleansed) {
            //Get the individual components of each player's data.
            String[] splitRow = unsplitRow.split(FILE_PARSER_REGEX);
            
            //Make sure we use the primary position of the player
            String preferredPosition = splitRow[3].split("-")[0];

            //Combine the info we need and place the row in the output file.
            
            //Points scored predicted by minutes played
            //_outputFilePrintStream.println(splitRow[8] + "," + splitRow[splitRow.length - 1] + "," + preferredPosition + "," + splitRow[2]);
            _outputFilePrintStream.println(splitRow[1] + "," + splitRow[9] + "," + preferredPosition + "," + splitRow[2]);
        }
    }

    //--------------------------------------------------------------
    //Group the latest seasons into each decade since the 1980s
    //--------------------------------------------------------------
    public static void groupIntoDecades() {
        //Don't use the typical indices for this one, as the input for this has condensed rows
        for (String unsplitRow : _rawDataNotCleansed) {
            //Get the components
            String[] splitRow = unsplitRow.split(FILE_PARSER_REGEX);

            //Determine the year, and place it into the proper file
            int seasonYear = Integer.parseInt(splitRow[0]);

            if (seasonYear < 1990) { //1980s
                BY_DECADE_FPATHS.get(0).println(unsplitRow);
            } else if (1990 <= seasonYear && seasonYear <= 1999) { //1990s
                BY_DECADE_FPATHS.get(1).println(unsplitRow);
            } else if (2000 <= seasonYear && seasonYear <= 2009) { //2000s
                BY_DECADE_FPATHS.get(2).println(unsplitRow);
            } else { //2010s
                BY_DECADE_FPATHS.get(3).println(unsplitRow);
            }
        }
    }
}