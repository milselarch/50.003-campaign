import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class UnitTest {
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
        check that the test fails with a BadFileFormat Exception
        if the unique combination entered matches one of the files,
        (we try setting unique_combination on each of the two files' columns),
        and most of the columns are the same in both files
        but the number of columns in both files is different
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
}
