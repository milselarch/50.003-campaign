This is a report for my week 13 testing for koh aik hong's (1005139) implementation of the software campaign  

```java
@Test
public void test_file_name() {
    // check that isCsv works with test filename
    boolean is_csv = DataRecon.isCSV("test.csv");
    assertTrue(is_csv);
}
```
Test passes

```java
@Test
public void bad_file_name() {
    // check that isCsv returns false with test filename
    // that does not end with .csv
    try {
        DataRecon.isCSV("test");
    } catch (Exception e) {
        assertTrue(e instanceof IndexOutOfBoundsException);
        return;
    }

    fail("isCsv should thrown an error");
}
```
Test passes

```java
@Test
public void chain_file_name() {
    // check that filename with two dots is still considered a csv
    boolean is_csv = DataRecon.isCSV("test.example.csv");
    assertTrue(is_csv);
}
```
Test fails because the code in isCsv only checks the extension after the first . (.example) rather than the last one

```java
@Test
public void chain_file_name_v2() {
    // check that filename with two dots is not considered a csv
    boolean is_csv = DataRecon.isCSV("test.csv.py");
    assertFalse(is_csv);
}
```
Test fails because the code in isCsv only checks the extension after the first . (.csv) rather than the last one (.py)

```java
@Test
public void fuzz_file_name() {
    // fuzz that isCsv returns true for random string inputs
    // that end in .csv
    Random generator = new Random();

    for (int k=0; k<100; k++) {
        int length = 5 + generator.nextInt(5);
        String rand = RandomString.generate(length);
        String filename = rand + ".csv";
        boolean is_csv = DataRecon.isCSV(filename);
        assertTrue(is_csv);
    }
}
```
Test passes

```java
@Test
public void test_recon() {
    // test that running the program with the sample files
    // and a column index works without error
    InputStream stdin = System.in;
    String[] inputs = (new String[] {
        "sample_file_1.csv",
        "sample_file_2.csv",
        "0"
    });

    String input_data = String.join("\n", inputs);

    try {
        System.setIn(new ByteArrayInputStream(input_data.getBytes()));
        DataRecon.main(new String[]{});
    } catch (Exception e) {
        System.out.println("TEST EXAMPLE FAILED");
        e.printStackTrace();
        fail("TEST EXAMPLE FAILED");
    } finally {
        System.setIn(stdin);
    }
}
```
Test passes

```java
@Test
public void test_recon_neg() {
    // test that running the program with the sample files
    // and a negative column index works without error
    InputStream stdin = System.in;
    String[] inputs = (new String[] {
        "sample_file_1.csv",
        "sample_file_2.csv",
        "-1"
    });

    String input_data = String.join("\n", inputs);

    try {
        System.setIn(new ByteArrayInputStream(input_data.getBytes()));
        DataRecon.main(new String[]{});
    } catch (Exception e) {
        System.out.println("TEST EXAMPLE FAILED");
        e.printStackTrace();
        fail("TEST EXAMPLE FAILED");
    } finally {
        System.setIn(stdin);
    }
}
```
Test fails as the implementation does not check for negative column indexes

```java
@Test
public void test_recon_space() {
    // test that running the program with the sample files
    // and a column index where the raw input has a leading
    // space works without error
    InputStream stdin = System.in;
    String[] inputs = (new String[] {
        "sample_file_1.csv",
        "sample_file_2.csv",
        " 1"
    });

    String input_data = String.join("\n", inputs);

    try {
        System.setIn(new ByteArrayInputStream(input_data.getBytes()));
        DataRecon.main(new String[]{});
    } catch (Exception e) {
        System.out.println("TEST EXAMPLE FAILED");
        e.printStackTrace();
        fail("TEST EXAMPLE FAILED");
    } finally {
        System.setIn(stdin);
    }
}
```
Test fails as space around column index is not trimmed before being parsed to integer

```java
@Test
public void test_recon_commas() {
    // test that running the program with the sample files
    // and comma delimited column indexes with a space in between
    // where the raw input has a leading space works without error
    InputStream stdin = System.in;
    String[] inputs = (new String[] {
        "sample_file_1.csv",
        "sample_file_2.csv",
        "1, 2"
    });

    String input_data = String.join("\n", inputs);

    try {
        System.setIn(new ByteArrayInputStream(input_data.getBytes()));
        DataRecon.main(new String[]{});
    } catch (Exception e) {
        System.out.println("TEST EXAMPLE FAILED");
        e.printStackTrace();
        fail("TEST EXAMPLE FAILED");
    } finally {
        System.setIn(stdin);
    }
}
```
Test fails as space around column indexes is not trimmed before being parsed to integer

