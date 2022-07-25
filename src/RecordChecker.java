import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

public final class RecordChecker {
    static final String DUP_COL_ERR = "DUPLICATE COLUMN NAMES";
    static final String COL_MISMATCH = "COLUMN HEADERS MISMATCH";
    static final String COMB_NOT_FOUND = "COMBINATION NOT FOUND IN CSV";

    private RecordChecker() {}

    public static void main(String[] args) {
        System.out.println("Hello campaign world");
        // RecordChecker.print_file("files/sample_file_1.csv");
        RecordChecker.run_interactive();
    }

    public static void run_interactive() {
        /*
        sample_file_1.csv
        sample_file_3.csv
        "Customer ID#", "Account No.", "Currency", "Type"

        Enter first csv file path: sample_file_1.csv
        Enter second csv file path: sample_file_3.csv
        Enter unique combination (comma separated):
        "Customer ID#", "Account No.", "Currency", "Type"
        "Customer ID#", "Account No.", "Type"
        */
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter first csv file path: ");
        String filename1 = scanner.nextLine();
        System.out.print("Enter second csv file path: ");
        String filename2 = scanner.nextLine();

        final String prompt = "Enter unique combination (comma separated): ";
        System.out.print(prompt);
        String combi_input = scanner.nextLine();

        try {
            String export_path = RecordChecker.generate_diffs(
                filename1, filename2, combi_input
            );
            System.out.print("Successfully wrote mismatches to ");
            System.out.println(export_path);
        } catch (Exception e) {
            System.out.println("ERROR ENCOUNTERED");
            e.printStackTrace();
        }
    }

    public static String generate_diffs(
        String filename1, String filename2, String raw_combination
    ) throws BadCombination, BadFileFormat,
        IOException, FilesMismatch
    {
        /*
        returns fil

        Things checked for:
            1. everything handled by read_csv
            2. * if the columns differ between files
            (might be too severe / strict)

        TODO:
            1. intelligently reorder columns in both csv files
            in case they were ordered different from each other
            2. should balance be treated as a number than a string?
            3. all the values are wrapped in quotes like "12.32"
            should values without quotes be treated like they're equal?
            i.e. "12.32" == 12.32?
        */
        String[] combination = raw_combination.split(",");
        // remove whitespace around each combination's values
        String[] trim_combination = new String[combination.length];
        for (int k=0; k<combination.length; k++) {
            trim_combination[k] = combination[k].trim();
        }

        System.out.print("COMBINATION ");
        System.out.println(Arrays.toString(trim_combination));
        if (!RecordChecker.is_unique_arr(trim_combination)) {
            throw new BadCombination(DUP_COL_ERR);
        }

        CsvFile csv_file1 = read_csv(filename1);
        CsvFile csv_file2 = read_csv(filename2);
        String[] headers1 = csv_file1.get_headers();
        String[] headers2 = csv_file2.get_headers();

        System.out.println("EDGE HEADERS");
        System.out.println(Arrays.toString(headers1));
        System.out.println(Arrays.toString(headers2));

        if (!Arrays.equals(headers1, headers2)) {
            throw new FilesMismatch(COL_MISMATCH);
        } else if (!csv_file1.has_columns(trim_combination)) {
            throw new BadCombination(COMB_NOT_FOUND);
        }

        ArrayList<String[]> all_mismatch_rows = new ArrayList<>();

        for (int k=0; k<csv_file1.num_rows(); k++) {
            String[] current_row = csv_file1.get_row(k);
            String[] compare_values = csv_file1.exclude_row_columns(
                k, trim_combination
            );
            String[] column_values = csv_file1.select_row_columns(
                k, trim_combination
            );

            ArrayList<String[]> mismatches = csv_file2.get_mismatch_rows(
                compare_values, trim_combination, column_values
            );

            if (mismatches.size() == 0) { continue; }
            mismatches.add(current_row);
            all_mismatch_rows.addAll(mismatches);
        }

        return RecordChecker.export_mismatches(
            all_mismatch_rows, headers1
        );
    }

    public static String export_mismatches(
        ArrayList<String[]> all_mismatch_rows, String[] headers
    ) throws IOException {
        String pattern = "yyMMdd-HHmmss";
        Date date_now = new java.util.Date();
        String stamp = new SimpleDateFormat(pattern).format(date_now);
        String dirname = "mismatches";

        File directory = new File(dirname);
        if (!directory.exists()) {
            System.out.println("created directory " + dirname);
            boolean dir_created = directory.mkdir();
            if (!dir_created) {
                throw new IOException(
                    "FAILED TO MAKE OUTPUT DIRECTORY"
                );
            }
        }

        String filename = "mismatches-" + stamp + ".csv";
        String export_path = dirname + "/" + filename;

        try {
            FileWriter writer = new FileWriter(export_path);
            // columns are not written in example output
            // String header_line = String.join(",", headers);
            // writer.write(header_line);
            // writer.write("\n");

            for (String[] row : all_mismatch_rows) {
                String line = String.join(",", row);
                writer.write(line);
                writer.write("\n");
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("File write error occurred.");
            e.printStackTrace();
        }

        return export_path;
    }

    public static boolean is_unique_arr(String[] arr) {
        // return true if all the elements in the
        // array are unique, return false otherwise
        String[] copied_arr = new String[arr.length];
        System.arraycopy(arr, 0, copied_arr, 0, arr.length);
        Arrays.sort(copied_arr);

        // check for duplicate columns in data
        for (int k=1; k<copied_arr.length; k++) {
            if (copied_arr[k-1].equals(copied_arr[k])) {
                return false;
            }
        }

        return true;
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
            return;
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

        things checked for:
            1. columns mismatch within file
            2. file not having any rows
            3. file not having at least 2 rows (including headers)
            4. header columns not being unique
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
                throw new BadFileFormat(COL_MISMATCH);
            }
            csv_data.add(columns);
        }

        if (headers == null) {
            throw new BadFileFormat("NO HEADERS FOUND");
        } else if (headers.length <= 1) {
            // We need to have more than one column at the least
            throw new BadFileFormat("INSUFFICIENT COLUMNS");
        }

        System.out.println("HEADERS");
        System.out.println(Arrays.toString(headers));

        // Convert String Array to List
        List<String> headers_list = Arrays.asList(headers);
        if (!RecordChecker.is_unique_arr(headers)) {
            throw new BadFileFormat(DUP_COL_ERR);
        }

        return new CsvFile(csv_data);
    }
}
