//File I/O imports as well as generic collections
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.ArrayList;


public class cleanser {

    //The location of the input file relative to the current file of code.
    final static String INPUT_FILE_LOCATION_RELATIVE = "dataset_uncleansed.txt";

    //The location of the output file relative to the current file of code.
    final static String OUTPUT_FILE_LOCATION_RELATIVE = "dataset_CLEANSED_BY_YEAR.txt";
    
    //The year from which the data we will use will be collected. Helps to avoid unnecessary lookups.
    final static int CUTOFF_YEAR = 1979;

    //The statistic that we really care about. Refers to the quantitative columns in the dataset.
    final static String STAT_OF_INTEREST = "PER";

    //The dataset contains multiple seasons for many players, often, we will just consider the last one.
    final static boolean IGNORE_MULTIPLES = true;

    //Reference to the input file.
    static Scanner _inputFileScanner;

    //Reference to the output file.
    static PrintStream _outputFilePrintStream;

    public static void main(String[] args) throws FileNotFoundException {
        //Create the scanner with the input file location.
        _inputFileScanner = new Scanner(new File(INPUT_FILE_LOCATION_RELATIVE));
        
        //Create the PrintStream with the output file location
        _outputFilePrintStream = new PrintStream(new File(OUTPUT_FILE_LOCATION_RELATIVE));

        //Get the raw lines of the file.
        ArrayList<String> rawDataNotCleansed = new ArrayList<String>();

        //Get all of the lines of the file.
        while (_inputFileScanner.hasNextLine()) {
            rawDataNotCleansed.add(_inputFileScanner.nextLine());
        }

        //Call the method of choice here after setting the constants above.
        removeDataBeforeYear(CUTOFF_YEAR, _outputFilePrintStream, rawDataNotCleansed);

        //Avoid memory leaks.
        _inputFileScanner.close();
        _outputFilePrintStream.close();
    }

    //Remove all of the rows that occur prior to the CUTOFF_YEAR
    public static void removeDataBeforeYear(int cutoffYear, PrintStream outputFile, ArrayList<String> rawData) {
        //Go through the raw data row by row, checking the year that the entry is for.
        //  If it is prior to 1979, don't print it to the PrintStream of the outputFile.
        for (int i = 0; i < rawData.size(); i++) {            
            try {
                //Check if the row is for a year after CUTOFF_YEAR and print it to the output file.
                //  The column with this info is column 2, but this is a program. Index 1 it is.
                if (Integer.parseInt(rawData.get(i).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")[1]) > CUTOFF_YEAR) {
                    //The row of data is for a year after CUTOFF_YEAR. We can send it to the output file.
                    outputFile.println(rawData.get(i));
                }
            } catch (Exception e) {
                //Do nothing.
            }
        }
    }


}