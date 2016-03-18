#! /bin/sh


report_cp() {
mkdir "$CIRCLE_TEST_REPORTS/junit/"

find . -type f -regex ".*/build/test-results/.*xml" -exec cp {} $CIRCLE_TEST_REPORTS/junit/ \;
cp -r ./build/reports "$CIRCLE_ARTIFACTS"
}

# テスト実行
./gradlew test

if [ $? -ne 0 ]; then
    echo "UnitTest failed..."
    report_cp
    exit 1
else
    report_cp
fi
