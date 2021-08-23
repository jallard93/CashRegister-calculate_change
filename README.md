# CashRegister-calculate_change
## AWS Lambda Function for creating change

To run tests:

    maven test

To package for deployment:

    maven package

To update lambda function code:

    aws lambda update-function-code --function-name CashRegister-calculate_change --zip-file fileb://target/CashRegister-calculate_change-1.0-SNAPSHOT.jar


