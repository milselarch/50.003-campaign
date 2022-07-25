import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class UnitTests {
    @Test
    public void empty_combination() {
        /*
        unittest for: RecordChecker.parse_combination
        ensure when we parse an empty string as our unique combination
        the parser raises an Exception
        */
        try {
            RecordChecker.parse_combination("");
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String error_message = e.getMessage();
            // System.out.println("EMPTY COMBO");
            // System.out.println(error_message);
            assertEquals(error_message, RecordChecker.BLANK_COLUMN);
            return;
        }

        fail("EMPTY COMBINATION DIDN'T FAIL");
    }

    @Test
    public void empty_combination_v2() {
        /*
        unittest for: RecordChecker.parse_combination
        ensure when we parse a string made of whitespace
        as our unique combination, the parser raises an Exception
        */
        Random generator = new Random();
        int length = 5 + generator.nextInt(5);
        RandomString rand_string_builder = new RandomString(
            length, new Random(), "\s\t\n\r"
        );

        String raw_input = rand_string_builder.next_string();

        try {
            RecordChecker.parse_combination(raw_input);
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String error_message = e.getMessage();
            System.out.println("EMPOTY COMBO");
            System.out.println(error_message);
            assertEquals(error_message, RecordChecker.BLANK_COLUMN);
            return;
        }

        fail("EMPTY COMBINATION DIDN'T FAIL");
    }

    @Test
    public void valid_combination() throws BadCombination {
        /*
        unittest for: RecordChecker.parse_combination
        ensure a string containing comma delimited columns
        where each column is unique successfully returns an array
        of the columns
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        ArrayList<String> columns = RandomString.generate_multi_exc(
            num_columns, column_length
        );

        String unique_combination = String.join(",", columns);
        String[] parsed_combination = RecordChecker.parse_combination(
            unique_combination
        );

        String[] columns_arr = new String[num_columns];
        columns.toArray(columns_arr);
        assertArrayEquals(columns_arr, parsed_combination);
    }

    @Test
    public void empty_column() {
        /*
        unittest for: RecordChecker.parse_combination
        ensure a string containing comma delimited columns
        where each column is unique successfully returns an array
        EXCEPT for one column that's just blank fails to parse
        without errors
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        ArrayList<String> columns = RandomString.generate_multi_exc(
            num_columns, column_length
        );

        int index = generator.nextInt(columns.size());
        columns.set(index, "");

        String unique_combination = String.join(",", columns);
        // unique_combination = "asd, sdasd, fdaasd,";
        // String[] split = unique_combination.split(",");
        // System.out.println(split);
        System.out.println("UNIQUE_COMBINATION");
        System.out.println(unique_combination);

        try {
            RecordChecker.parse_combination(unique_combination);
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.BLANK_COLUMN, err_msg);
            return;
        }

        fail("EMPTY COLUMN NOT FAILED IN PARSE_COMBINATION");
    }

    @Test
    public void non_unique_combination() {
        /*
        unittest for: RecordChecker.parse_combination
        ensure a string containing comma delimited columns
        where one of the columns is duplicated fails to be
        parsed in parse_combination due to duplicate columns
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        ArrayList<String> columns = RandomString.generate_multi_exc(
            num_columns, column_length
        );
        ArrayList<String> non_unique_columns = RandomString.sample(
            columns, num_columns + 1
        );
        String unique_combination = String.join(
            ",", non_unique_columns
        );

        try {
            RecordChecker.parse_combination(unique_combination);
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.DUP_COL_ERR, err_msg);
            return;
        }

        fail("NON UNIQUE COMBINATION SHOULD'VE FAILED PARSING");
    }

    @Test
    public void non_unique_combination_v2() {
        /*
        unittest for: RecordChecker.parse_combination
        ensure a string containing comma delimited columns
        where at least one of the columns is duplicated fails to be
        parsed in parse_combination due to duplicate columns
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        int excess_columns = 1 + generator.nextInt(5);

        ArrayList<String> columns = RandomString.generate_multi_exc(
            num_columns, column_length
        );
        ArrayList<String> non_unique_columns = RandomString.sample(
            columns, num_columns + excess_columns
        );
        String unique_combination = String.join(
            ",", non_unique_columns
        );

        try {
            RecordChecker.parse_combination(unique_combination);
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.DUP_COL_ERR, err_msg);
            return;
        }

        fail("NON UNIQUE COMBINATION SHOULD'VE FAILED PARSING");
    }

    public String[] make_columns(int num_columns, int column_length) {
        ArrayList<String> columns = RandomString.generate_multi_exc(
            num_columns, column_length
        );
        String[] columns_arr = new String[num_columns];
        columns.toArray(columns_arr);
        return columns_arr;
    }

    @Test
    public void same_file_mismatches() throws FilesMismatch, BadCombination {
        /*
        unittest for: RecordChecker.get_mismatch_rows
        Check that if we check a csv file populated with random
        rows against itself we can successfully run get_mismatch_rows
        and get 0 mismatch rows
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        int num_rows = 5 + generator.nextInt(11);

        String[] headers = make_columns(num_columns, column_length);
        List<String> headers_list = Arrays.asList(headers);
        Collections.shuffle(headers_list);

        List<String> combination = headers_list.subList(0, num_columns/2);
        String[] combination_arr = new String[combination.size()];
        combination.toArray(combination_arr);
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(headers);

        System.out.print("COMBINATION: ");
        System.out.println(combination);
        System.out.print("HEADERS: ");
        System.out.println(headers_list);

        for (int k=0; k<num_rows; k++) {
            String[] columns_arr = make_columns(num_columns, column_length);
            rows.add(columns_arr);
        }

        CsvFile csv_file = new CsvFile(rows);
        ArrayList<String[]> mismatches = RecordChecker.get_mismatch_rows(
            csv_file, csv_file, rows.get(0)
        );

        assertEquals(mismatches.size(), 0);
    }

    @Test
    public void same_file_mismatches_v2() throws FilesMismatch, BadCombination {
        /*
        unittest for: RecordChecker.get_mismatch_rows
        Check that if we check a csv file populated with random
        rows against another file with the same header columns and
        the same rows (but shuffled), we can successfully run
        get_mismatch_rows and get 0 mismatch rows
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        int num_rows = 5 + generator.nextInt(11);

        String[] headers = make_columns(num_columns, column_length);
        List<String> headers_list = Arrays.asList(headers);
        Collections.shuffle(headers_list);

        List<String> combination = headers_list.subList(0, num_columns/2);
        String[] combination_arr = new String[combination.size()];
        combination.toArray(combination_arr);
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(headers);

        for (int k=0; k<num_rows; k++) {
            String[] columns_arr = make_columns(num_columns, column_length);
            rows.add(columns_arr);
        }

        CsvFile csv_file1 = new CsvFile(rows);
        ArrayList<String[]> rows2 = csv_file1.copy_rows();
        Collections.shuffle(rows2);

        rows2.add(0, headers);
        CsvFile csv_file2 = new CsvFile(rows);
        ArrayList<String[]> mismatches = RecordChecker.get_mismatch_rows(
            csv_file1, csv_file2, headers
        );

        assertEquals(mismatches.size(), 0);
    }

    @Test
    public void same_file_mismatches_v3() throws FilesMismatch, BadCombination {
        /*
        unittest for: RecordChecker.get_mismatch_rows
        Check that if we check a csv file populated with random
        rows against another file with the same header column values
        (but the column order is shuffled) and the same rows
        (but row positions are shuffled), we can successfully run
        get_mismatch_rows and get 0 mismatch rows
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        int num_rows = 5 + generator.nextInt(11);

        String[] headers = make_columns(num_columns, column_length);
        List<String> headers_list = Arrays.asList(headers);
        Collections.shuffle(headers_list);

        List<String> combination = headers_list.subList(0, num_columns/2);
        String[] combination_arr = new String[combination.size()];
        combination.toArray(combination_arr);
        ArrayList<String[]> rows = new ArrayList<>();
        rows.add(headers);

        for (int k=0; k<num_rows; k++) {
            String[] columns_arr = make_columns(num_columns, column_length);
            rows.add(columns_arr);
        }

        CsvFile csv_file1 = new CsvFile(rows);
        ArrayList<String[]> rows2 = csv_file1.copy_rows();
        Collections.shuffle(rows2);

        rows2.add(0, headers);
        CsvFile csv_file2 = new CsvFile(rows);
        ArrayList<String[]> mismatches = RecordChecker.get_mismatch_rows(
            csv_file1, csv_file2, combination_arr
        );

        assertEquals(mismatches.size(), 0);
    }

    @Test
    public void test_unique_arr() {
        /*
        unittest for RecordChecker.is_unique_arr
        check that is_unique_arr returns true for
        an array of unique string values
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);
        String[] columns = make_columns(num_columns, column_length);
        boolean is_unique = RecordChecker.is_unique_arr(columns);
        assertTrue(is_unique);
    }

    @Test
    public void test_unique_empty() {
        /*
        unittest for RecordChecker.is_unique_arr
        check that is_unique_arr returns true for
        an empty array
        */
        String[] columns = new String[0];
        boolean is_unique = RecordChecker.is_unique_arr(columns);
        assertTrue(is_unique);
    }

    @Test
    public void test_unique_null() {
        /*
        unittest for RecordChecker.is_unique_arr
        check that is_unique_arr returns false when given null input
        */
        String[] columns = null;
        boolean is_unique = RecordChecker.is_unique_arr(columns);
        assertFalse(is_unique);
    }

    @Test
    public void test_non_unique_arr() {
        /*
        unittest for RecordChecker.is_unique_arr
        check that is_unique_arr returns false with an
        array of strings containing duplicate columns
        */
        Random generator = new Random();
        int num_columns = 5 + generator.nextInt(5);
        int column_length = 5 + generator.nextInt(5);

        ArrayList<String> columns = RandomString.generate_multi(
            num_columns, column_length
        );

        int num_samples = num_columns + 1;
        ArrayList<String> non_unique_columns = RandomString.sample(
            columns, num_samples
        );

        String[] columns_arr = new String[num_samples];
        non_unique_columns.toArray(columns_arr);
        boolean is_unique = RecordChecker.is_unique_arr(columns_arr);
        assertFalse(is_unique);
    }

    @Test
    public void read_sample_csv() throws BadFileFormat, IOException {
        /*
        unittest for RecordChecker.read_csv
        Verify that sample_file_1.csv and sample_file_3.csv
        both have 1000 rows and 5 columns after being read, and that
        its headers row is as follows:
        "Customer ID#","Account No.","Currency","Type","Balance"
        (quotation marks are in the raw data itself)
        */
        CsvFile csv_file1 = RecordChecker.read_csv(
            "sample_file_1.csv"
        );

        assertEquals(csv_file1.num_rows(), 1000);
        assertEquals(csv_file1.num_columns(), 5);
        assertArrayEquals(csv_file1.get_headers(), new String[] {
            "\"Customer ID#\"", "\"Account No.\"", "\"Currency\"",
            "\"Type\"", "\"Balance\""
        });

        CsvFile csv_file3 = RecordChecker.read_csv(
            "sample_file_3.csv"
        );

        assertEquals(csv_file3.num_rows(), 1000);
        assertEquals(csv_file3.num_columns(), 5);
        assertArrayEquals(csv_file3.get_headers(), new String[] {
            "\"Customer ID#\"", "\"Account No.\"", "\"Currency\"",
            "\"Type\"", "\"Balance\""
        });
    }

    @Test
    public void read_empty() throws BadFileFormat, IOException {
        /*
        unittest for RecordChecker.read_csv
        make sure read_csv fails for an empty file
        */
        try {
            RecordChecker.read_csv("empty_file.csv");
        } catch (Exception e) {
            assertTrue(e instanceof BadFileFormat);
            String err_message = e.getMessage();
            assertEquals(err_message, RecordChecker.NO_HEADERS);
            return;
        }

        fail("EMPTY FILE READ SHOULD'VE THROWN ERROR");
    }

    @Test
    public void read_dir() throws BadFileFormat, IOException {
        /*
        unittest for read_csv
        make sure when we try to make it read a directory
        (mismatches dir) it fails with a FileNotFoundException
        */
        try {
            RecordChecker.read_csv("mismatches");
        } catch (Exception e) {
            System.out.println(e);
            assertTrue(e instanceof IOException);
            assertTrue(e instanceof FileNotFoundException);
            return;
        }

        fail("EMPTY FILE READ SHOULD'VE THROWN ERROR");
    }
}
