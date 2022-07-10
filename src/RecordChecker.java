import java.util.*;
import java.io.*;

public final class RecordChecker {
    private RecordChecker() {}

    public static void main(String[] args) {
        System.out.println("Hello campaign world");
        RecordChecker.print_file("sample_file_1.csv");
    }

    public static void print_file(String filename) {
        /*
        parse the csv file and print out every row of the
        parsed csv file
        */
        RecordChecker checker = new RecordChecker();
        CsvFile csv_data = null;

        try {
            csv_data = checker.read_csv(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert csv_data != null;
        csv_data.print_file();
    }

    public static CsvFile read_csv(
        String filename
    ) throws IOException, BadFileFormat {
        /*
        Given a filename (or file path), this method returns
        an ArrayList of String Arrays, where each string array
        is a row of csv values, and each element in the string
        array is a single column value of the column, row 0 should
        represent a list of headers
        */
        String split_by = ",";

        //parsing a CSV file into BufferedReader class constructor
        FileReader file_obj = new FileReader(filename);
        BufferedReader br = new BufferedReader(file_obj);
        ArrayList<String[]> csv_data = new ArrayList<>();
        String[] headers = null;

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            if (csv_data.size() == 0) {
                headers = line.split(split_by);
                csv_data.add(headers);
                continue;
            }

            String[] columns = line.split(split_by);
            if (columns.length != headers.length) {
                // this row has more columns than were in the headers row
                throw new BadFileFormat("COLUMNS MISMATCH");
            }
            csv_data.add(columns);
        }

        if (headers == null) {
            throw new BadFileFormat("NO HEADERS FOUND");
        } else if (headers.length <= 1) {
            // We need to have more than the Balance column at the least
            throw new BadFileFormat("INSUFFICIENT COLUMNS");
        }

        // Convert String Array to List
        List<String> headers_list = Arrays.asList(headers);
        if (headers_list.contains("Balance")) {
            // fail parsing if the balance column is missing
            throw new BadFileFormat("NO BALANCE COLUMN");
        }
        Arrays.sort(headers);
        // check for duplicate columns in data
        for (int k=1; k<headers.length; k++) {
            if (Objects.equals(headers[k-1], headers[k])) {
                throw new BadFileFormat("DUPLICATE COLUMNS");
            }
        }

        return new CsvFile(csv_data);
    }
}
