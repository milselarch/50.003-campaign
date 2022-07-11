# 50.003-campaign
Software testing campaign repo for Lim Thian Yew (1003158)

## Build and test instructions:  
Run the following in the project root folder
```console
javac -cp ./src src/*.java
java src/RecordChecker.java
```

Alternatively, you can build and run the project using IntelliJ IDEA instead (in fact that is the method I using).
You will be prompted for the filename of the two csv source files, and the unique combination of columns (comma seperated) that you wish to compare them by:  

```console
Hello campaign world
Enter first csv file path: sample_file_1.csv
Enter second csv file path: sample_file_3.csv        
Enter unique combination (comma separated): "Customer ID#", "Account No.", "Currency", "Type"
```

Example output:
```console
COMBINATION ["Customer ID#", "Account No.", "Currency", "Type"]
HEADERS
["Customer ID#", "Account No.", "Currency", "Type", "Balance"]
HEADERS
["Customer ID#", "Account No.", "Currency", "Type", "Balance"]
Successfully wrote mismatches to mismatches/mismatches-220711-214731.csv
```
