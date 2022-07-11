import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CsvFile {
    ArrayList<String[]> raw_data;
    public static final String BALANCE_COLUMN = "\"Balance\"";

    CsvFile(ArrayList<String[]> raw_data) {
        assert raw_data.size() >= 1;
        String[] headers = raw_data.get(0);
        List<String> headers_list = Arrays.asList(headers);

        // ensure headers contains "Balance" column
        assert headers_list.contains(BALANCE_COLUMN);
        this.raw_data = raw_data;
    }

    public int get_balance_column_index() {
        return this.get_column_index(BALANCE_COLUMN);
    }

    public int get_column_index(String column_name) {
        String[] headers = raw_data.get(0);
        // System.out.print("I-HEADERS ");
        // System.out.println(Arrays.toString(headers));
        // System.out.println(column_name);

        for (int k=0; k<headers.length; k++) {
            /*
            System.out.print(k);
            System.out.print(" -- ");
            System.out.print(headers[k]);
            System.out.print(" -- |");
            System.out.print(column_name);
            System.out.print("| -- ");
            System.out.println(headers[k].charAt(0));
            */

            if (headers[k].equals(column_name)) {
                return k;
            }
        }
        return -1;
    }

    public boolean has_columns(String[] columns) {
        int[] column_indexes = this.get_column_indexes(columns);
        for (int column_index : column_indexes) {
            if (column_index == -1) { return false; }
        }
        return true;
    }

    public int[] get_column_indexes(String[] columns) {
        int[] column_indexes = new int[columns.length];
        for (int k=0; k<columns.length; k++) {
            String column = columns[k];
            int column_index = this.get_column_index(column);
            column_indexes[k] = column_index;
        }

        return column_indexes;
    }

    public String[] select_row_columns(int index, String[] columns) {
        // get columns values corresponding to the columns passed in
        // for the row at index
        int[] column_indexes = this.get_column_indexes(columns);
        String[] column_values = new String[columns.length];
        String[] row = this.get_row(index);

        for (int k=0; k<column_values.length; k++) {
            column_values[k] = row[column_indexes[k]];
        }

        assert column_indexes.length == column_values.length;
        return column_values;
    }

    public ArrayList<Pair<String[], String>> search_matches(
        String[] columns, String[] column_values
    ) {
        assert columns.length == column_values.length;
        ArrayList<Pair<String[], String>> matches = new ArrayList<>();

        for (int k=0; k<this.num_rows(); k++) {
            String[] row_column_values = this.select_row_columns(k, columns);
            String[] row = this.get_row(k);

            String balance = this.get_row_balance(k);
            if (Arrays.equals(column_values, row_column_values)) {
                matches.add(new Pair<>(row, balance));
            }
        }

        return matches;
    }

    public ArrayList<String[]> get_mismatch_rows(
        String balance, String[] columns, String[] column_values
    ) {
        ArrayList<Pair<String[], String>> matches = this.search_matches(
            columns, column_values
        );

        ArrayList<String[]> rows = new ArrayList<>();
        for (Pair<String[], String> match : matches) {
            String[] row = match.first;
            String row_balance = match.second;

            if (!Objects.equals(row_balance, balance)) {
                rows.add(row);
            }
        }

        return rows;
    }

    public void print_file() {
        /*
        print out every row of the parsed csv file
        */
        for (int k=0; k<this.raw_data.size(); k++) {
            String[] column = this.raw_data.get(k);
            System.out.print(k);
            System.out.print(" ");
            System.out.println(Arrays.toString(column));
        }
    }

    public String get_row_balance(int index) {
        String[] row = this.get_row(index);
        int balance_index = this.get_balance_column_index();
        return row[balance_index];
    }

    public String[] non_balance_row(int index) {
        // return row at index with the Balance column removed
        ArrayList<String> filtered_row_data = new ArrayList<String>();
        int balance_column_index = this.get_balance_column_index();
        String[] row = this.get_row(index);

        for (int k=0; k<row.length; k++) {
            if (k == balance_column_index) { continue; }
            filtered_row_data.add(row[k]);
        }

        return filtered_row_data.toArray(new String[0]);
    }

    public String[] get_headers() {
        return this.raw_data.get(0);
    }

    public int num_rows() {
        return this.raw_data.size() - 1;
    }

    public int num_columns() {
        String[] headers = this.get_headers();
        return headers.length;
    }

    public String[] get_row(int index) {
        assert index >= 0;
        return this.raw_data.get(index + 1);
    }
}
