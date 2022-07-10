import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CsvFile {
    ArrayList<String[]> raw_data;

    CsvFile(ArrayList<String[]> raw_data) {
        assert raw_data.size() >= 1;
        String[] headers = raw_data.get(0);
        List<String> headers_list = Arrays.asList(headers);

        // ensure headers contains "Balance" column
        assert headers_list.contains("Balance");
        this.raw_data = raw_data;
    }

    public int get_balance_column_index() {
        String[] headers = raw_data.get(0);
        for (int k=0; k<headers.length; k++) {
            if (Objects.equals(headers[k], "Balance")) {
                return k;
            }
        }
        return -1;
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
