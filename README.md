# Margarine Knife

## 概要

 * Butter KnifeライクなView Bindライブラリ
 * 本家 Butter KnifeがAPTに方向転換を行ったため、純粋なRuntime Bind Libraryとして開発する
 * ライブラリプロジェクトでも利用できる

## Butter Knifeからの移行

 * 基本的には引数やクラス名を合わせてあるので、importとクラス名書き換えでそのまま動作する

### Annotation

```
// ButterKnife 7.0.1
@Bind(R.id.View_Id)
ViewGroup mViewGroup;

@BindString(R.string.String_Res)
String stringRes;

@Click(R.id.View_Id)
void  clickView(View view) {
  // do
}
```

```
// MargarineKnife
@Bind(R.id.View_Id)
ViewGroup mViewGroup;

@BindString(R.string.String_Res)
String stringRes;

@Click(R.id.View_Id)
void  clickView(View view) {
  // do
}

```

### Annotation(Lib Project)

 * MargarineKnifeはリソース名を指定できるため、ライブラリプロジェクトでも利用可能

```
// ButterKnife 7.0.1 ERROR!
@Bind("View_Id")
ViewGroup mViewGroup;
```

```
// MargarineKnife OK!
@BindRes(resName = "View_Id")
ViewGroup mViewGroup;


@Click(resName = "View_Id")
void  clickView(View view) {
  // do
}
```

### View Bind

```
// ButterKnife 7.0.1
ButterKnife.bind(this);
```

```
// MargarineKnife
MargarineKnife.bind(this);
```

### View Unbind

 * MargarineKnifeでは、unbind呼び出しは行わない設計となっている

```
// ButterKnife 7.0.1
ButterKnife.unbind(this);
```

```
// MargarineKnife (Nothing)
```

## LICENSE

プロジェクトの都合に応じて、下記のどちらかを選択してください。

* アプリ等の成果物で権利情報を表示可能な場合
	* 権利情報の表示を行う（行える）場合、MIT Licenseを使用してください。
	* [MIT License](LICENSE-MIT.txt)
* 何らかの理由で権利情報を表示不可能な場合
	* 何らかの事情によりライセンス表記を行えない場合、下記のライセンスで使用可能です。
	* ライブラリ内で依存している別なライブラリについては、必ずそのライブラリのライセンスに従ってください。
	* [NYSL(English)](LICENSE-NYSL-eng.txt)
	* [NYSL(日本語)](LICENSE-NYSL-jpn.txt)
