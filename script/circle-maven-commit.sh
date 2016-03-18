#! /bin/sh

# gitユーザ設定
git config --global user.email 'eagle.sakura+deploy@gmail.com'
git config --global user.name 'eaglesakura-deploy'

if [ -e ./maven ]; then
    echo "maven directory found. commit -> maven $CIRCLE_PROJECT_REPONAME $CIRCLE_BRANCH"
else
    echo "maven directory notfound..."
    exit 1
fi

cd maven
git add .
git commit -am "[Circle-CI] add $CIRCLE_PROJECT_REPONAME $CIRCLE_BRANCH.$CIRCLE_BUILD_NUM"
git push origin gh-pages
