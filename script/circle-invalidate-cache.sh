#! /bin/sh

mkdir ${BUILD_CACHEDIR}
MAVEN_CHECKFILE=${BUILD_CACHEDIR}/es_maven_check.txt

if [ -e "${MAVEN_CHECKFILE}" ]; then
 # ファイルが存在するので、一致チェックを行う
 echo "has ${MAVEN_CHECKFILE}"
 CACHEFILE_MSG=`cat ${MAVEN_CHECKFILE}`
 REPOSITORY_MSG=`curl https://raw.githubusercontent.com/eaglesakura/maven/gh-pages/.cache_ctrl`
 if [ ${CACHEFILE_MSG} = ${REPOSITORY_MSG} ]; then
    # mavenリポジトリに変更がないため、何もしない
     echo "Cache eq"
     exit 0
 else
    # mavenリポジトリが変更になったため、ローカルを削除
     echo "Cache not eq"
    rm -rf "/home/ubuntu/.m2"
    rm -rf "/home/ubuntu/.ivy2"
    rm -rf "/home/ubuntu/.gradle"
    exit 0
 fi
else
 # ファイルが存在しないので、キャッシュをDLしてabort
 echo "not ${MAVEN_CHECKFILE}"
 wget -O ${MAVEN_CHECKFILE} "https://raw.githubusercontent.com/eaglesakura/maven/gh-pages/.cache_ctrl"
 exit 0
fi