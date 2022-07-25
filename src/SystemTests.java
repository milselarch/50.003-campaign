import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SystemTests {
    /*
    system tests on RecordChecker.generate_diffs
    (the entry function to the program to take in filenames,
    unique combination, and produce the csv file containing mismatches)
    */
    @Test
    public void test_non_unique_combination() {
        /*
        check that the test fails with a BadCombination Exception
        if the unique combination entered is not actually unique
        */
        final int no_unique_columns = 5;
        ArrayList<String> columns = RandomString.generate_multi(
            no_unique_columns
        );
        ArrayList<String> non_unique_columns = RandomString.sample(
            columns, no_unique_columns + 1
        );
        String non_unique_combination = String.join(
            ",", non_unique_columns
        );

        try {
            RecordChecker.generate_diffs(
                "sample_file_1.csv",
                "sample_file_2.csv",
                non_unique_combination
            );
        } catch (Exception e) {
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.DUP_COL_ERR, err_msg);
            return;
        }

        fail("NO EXCEPTION THROWN FOR NON-UNIQUE COMBINATION");
    }

    @Test
    public void combination_mismatch()
        throws BadFileFormat, IOException
    {
        /*
        check that the test fails with a BadCombination Exception
        if the unique combination entered is unique, and the number
        of columns in the unique combination actually matches the
        number of columns in the files, but the columns supplied in
        the unique combination don't match the columns in the files
        */
        String filename1 = "sample_file_1.csv";
        String filename2 = "sample_file_3.csv";
        CsvFile csv_file1 = RecordChecker.read_csv(filename1);
        CsvFile csv_file2 = RecordChecker.read_csv(filename2);
        String[] headers1 = csv_file1.get_headers();
        String[] headers2 = csv_file2.get_headers();
        assertArrayEquals(headers1, headers2);

        ArrayList<String> fake_columns = RandomString.generate_multi_exc(
            headers1.length, headers1[0].length(), Arrays.asList(headers1)
        );
        String mismatch_combination = String.join(
            ",", fake_columns
        );

        try {
            RecordChecker.generate_diffs(
                "sample_file_1.csv",
                "sample_file_3.csv",
                mismatch_combination
            );
        } catch (Exception e) {
            // System.out.println(e);
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.COMB_NOT_FOUND, err_msg);
            return;
        }

        fail("NO EXCEPTION THROWN FOR MISMATCHING COMBINATION");
    }

    @Test
    public void combination_mismatch_v2()
        throws BadFileFormat, IOException
    {
        /*
        check that the test fails with a BadCombination Exception
        if the unique combination entered is unique, and the number
        of columns in the unique combination actually matches the
        number of columns in the files, but one of the columns supplied
        in the unique combination don't match the columns in the files.
        this is different from the previous test in that only one of
        the columns don't match the columns in the csv files
        */
        String filename1 = "sample_file_1.csv";
        String filename2 = "sample_file_3.csv";
        CsvFile csv_file1 = RecordChecker.read_csv(filename1);
        CsvFile csv_file2 = RecordChecker.read_csv(filename2);
        String[] headers1 = csv_file1.get_headers();
        String[] headers2 = csv_file2.get_headers();
        assertArrayEquals(headers1, headers2);

        Random generator = new Random();
        String[] combination_columns = headers1.clone();
        int index1 = generator.nextInt(combination_columns.length);
        int index2 = generator.nextInt(combination_columns.length);
        // random generate a new string that's not in csv file headers
        String fake_column = RandomString.unique_random(
            headers1[index1].length(), Arrays.asList(headers1)
        );

        // randomly set one of the columns to be put into the
        // unique combination to be a random string that doesn't
        // exist in the columns of the two csv files
        combination_columns[index2] = fake_column;
        String mismatch_combination = String.join(
            ",", Arrays.asList(combination_columns)
        );

        try {
            RecordChecker.generate_diffs(
                "sample_file_1.csv",
                "sample_file_3.csv",
                mismatch_combination
            );
        } catch (Exception e) {
            // System.out.println(e);
            assertTrue(e instanceof BadCombination);
            String err_msg = e.getMessage();
            assertEquals(RecordChecker.COMB_NOT_FOUND, err_msg);
            return;
        }

        fail("NO EXCEPTION THROWN FOR MISMATCHING COMBINATION");
    }

    @Test
    public void files_mismatch() throws IOException {
        /*
        check that the test fails with a BadFileFormat Exception
        if the unique combination entered matches one of the files,
        (we try setting unique_combination on each of the two files' columns),
        and the number of columns in both files is the same
        but the header columns between the two files don't match
        each other

        file column values for both files are randomly generated lmao
        */

        String filename1 = "test_file_1.csv";
        String filename2 = "test_file_2.csv";
        int num_columns = 5;

        ArrayList<String[]> file_data_1 = new ArrayList<>();
        ArrayList<String> fake_columns_1 = RandomString.generate_multi_exc(
            num_columns, 5
        );
        ArrayList<String[]> file_data_2 = new ArrayList<>();
        ArrayList<String> fake_columns_2 = RandomString.generate_multi_exc(
            num_columns, 5, fake_columns_1
        );

        assertNotEquals(fake_columns_1, fake_columns_2);
        String[] headers1 = new String[fake_columns_1.size()];
        String[] headers2 = new String[fake_columns_2.size()];
        fake_columns_1.toArray(headers1);
        fake_columns_2.toArray(headers2);

        file_data_1.add(headers1);
        file_data_1.add(headers1);
        file_data_2.add(headers2);
        file_data_2.add(headers2);

        // System.out.println("UNIQUE_COMBINATION");
        // System.out.println(unique_combination);

        CsvFile csv_file_1 = new CsvFile(file_data_1);
        CsvFile csv_file_2 = new CsvFile(file_data_2);
        csv_file_1.export_csv(filename1);
        csv_file_2.export_csv(filename2);

        for (int k=0; k<2; k++) {
            String[] columns = (k == 0 ? headers1 : headers2);
            String unique_combination = String.join(
                ",", columns
            );

            try {
                RecordChecker.generate_diffs(
                    filename1, filename2, unique_combination
                );
            } catch (Exception e) {
                // System.out.println(e);
                // System.out.println("HEADERS");
                // System.out.println(Arrays.toString(headers1));
                // System.out.println(Arrays.toString(headers2));
                assertTrue(e instanceof FilesMismatch);
                String err_msg = e.getMessage();
                assertEquals(RecordChecker.COL_MISMATCH, err_msg);
                continue;
            }

            fail("NO EXCEPTION THROWN FOR MISMATCHING CSV HEADER COLUMNS");
        }
    }

    @Test
    public void files_column_length_mismatch() throws IOException {
        /*
        test for: RecordChecker.generate_diffs
        check that the test fails with a BadFileFormat Exception
        if the unique combination entered matches one of the files,
        (we try setting unique_combination on each of the two files' columns),
        and all the columns that appear in one file appear in the other file,
        but one file has more columns than the other

        column values are randomly generated lmao
        */

        String filename1 = "test_file_1.csv";
        String filename2 = "test_file_2.csv";
        int num_columns = 5;

        ArrayList<String[]> file_data_1 = new ArrayList<>();
        ArrayList<String> fake_columns_1 = RandomString.generate_multi_exc(
            num_columns, 5
        );
        ArrayList<String[]> file_data_2 = new ArrayList<>();
        ArrayList<String> fake_columns_2 = new ArrayList<>(
            fake_columns_1.subList(1, fake_columns_1.size())
        );

        assertNotEquals(fake_columns_1, fake_columns_2);
        String[] headers1 = new String[fake_columns_1.size()];
        String[] headers2 = new String[fake_columns_2.size()];
        fake_columns_1.toArray(headers1);
        fake_columns_2.toArray(headers2);

        file_data_1.add(headers1);
        file_data_1.add(headers1);
        file_data_2.add(headers2);
        file_data_2.add(headers2);

        // System.out.println("UNIQUE_COMBINATION");
        // System.out.println(unique_combination);

        CsvFile csv_file_1 = new CsvFile(file_data_1);
        CsvFile csv_file_2 = new CsvFile(file_data_2);
        csv_file_1.export_csv(filename1);
        csv_file_2.export_csv(filename2);

        for (int k=0; k<2; k++) {
            String[] columns = (k == 0 ? headers1 : headers2);
            String unique_combination = String.join(
                ",", columns
            );

            try {
                RecordChecker.generate_diffs(
                    filename1, filename2, unique_combination
                );
            } catch (Exception e) {
                // System.out.println(e);
                // System.out.println("HEADERS");
                // System.out.println(Arrays.toString(headers1));
                // System.out.println(Arrays.toString(headers2));
                assertTrue(e instanceof FilesMismatch);
                String err_msg = e.getMessage();
                assertEquals(RecordChecker.COL_MISMATCH, err_msg);
                continue;
            }

            fail("NO EXCEPTION THROWN FOR MISMATCHING CSV HEADER COLUMNS");
        }
    }

    @Test
    public void valid_mismatch_test() throws
        IOException, FilesMismatch, BadFileFormat, BadCombination
    {
        /*
        If we have two files with the same header column values
        (I randomly generated the columns, though column length is
        constant across all columns)
        AND the unique combination matches the file header columns
        AND each file has a row each that have the same values except
        at a single column at rand_index (randomly selected)
        AND the unique combination is all the columns except for column i
        THEN we should expect a mismatch csv file to be produced that
        contains both of those rows
        */

        String filename1 = "test_file_1.csv";
        String filename2 = "test_file_2.csv";
        Random generator = new Random();

        final int column_length = 5 + generator.nextInt(5);
        final int no_unique_columns = 5 + generator.nextInt(5);
        ArrayList<String> columns = RandomString.generate_multi_exc(
            no_unique_columns, column_length
        );

        String[] columns_arr = new String[no_unique_columns];
        columns.toArray(columns_arr);

        String[] row1 = new String[no_unique_columns];
        String[] row2 = new String[no_unique_columns];
        columns.toArray(row1);
        columns.toArray(row2);

        String rand_value1 = RandomString.unique_random(
            column_length + 1, columns
        );
        String rand_value2 = RandomString.unique_random(
            column_length + 1, List.of(new String[]{rand_value1})
        );

        int rand_index = (new Random()).nextInt(no_unique_columns);
        row1[rand_index] = rand_value1;
        row2[rand_index] = rand_value2;

        assert(!rand_value1.equals(rand_value2));
        ArrayList<String[]> file_data_1 = new ArrayList<>();
        ArrayList<String[]> file_data_2 = new ArrayList<>();
        file_data_1.add(columns_arr);
        file_data_1.add(row1);
        file_data_2.add(columns_arr);
        file_data_2.add(row2);

        CsvFile csv_file_1 = new CsvFile(file_data_1);
        CsvFile csv_file_2 = new CsvFile(file_data_2);
        csv_file_1.export_csv(filename1);
        csv_file_2.export_csv(filename2);

        List<String> group_columns = columns.subList(0, columns.size());
        group_columns.remove(rand_index);
        String unique_combination = String.join(
            ",", group_columns
        );

        System.out.print("UNIQUE_COMBO: ");
        System.out.println(unique_combination);
        System.out.println(columns);

        String export_filename = RecordChecker.generate_diffs(
            filename1, filename2, unique_combination
        );

        System.out.println("EXPORTED TO");
        System.out.println(export_filename);
        CsvFile mismatches = RecordChecker.read_csv(
            export_filename, columns_arr
        );

        assertEquals(2, mismatches.num_rows());
        assertArrayEquals(mismatches.get_headers(), columns_arr);
        assertArrayEquals(mismatches.get_row(0), row2);
        assertArrayEquals(mismatches.get_row(1), row1);
    }
}
