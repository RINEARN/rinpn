# ステップ 4 - スクリプトで関数/変数を追加する (ライブラリ スクリプト の使用)

&raquo; [English](Step4.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

スクリプトは、単体で実行するためだけでなく、 計算式や別スクリプト内で使用するための関数や変数を、定義して提供するためにも役立ちます。 そのような目的のスクリプトの事を、 RINPn および Vnano では **ライブラリ スクリプト** と呼びます。

## ライブラリ スクリプトの使用方法

例えば、RINPn の「 lib 」フォルダ内にある「 ExampleLibrary.vnano 」は、 ライブラリスクリプトのサンプルで、標準で読み込まれるようになっています。 その記述内容は以下の通りです：

    (lib フォルダ内の ExampleLibrary.vnano をテキストエディタで開いた内容)

    float libvar = 2.0;

    float libfun(float x) {
        float result = libvar * x + 1;
        return result;
    }

※ なお、Vnano における float 型の精度は、double 型と同様の 64 bit なため、どちらを使っても同じです。 一応は float が標準の型名で、double は他言語との間の移植性や可読性に配慮して使える別名、という扱いとなっています。

上記の「 ExampleLibrary.vnano 」内で定義されている変数「 libvar 」や関数「 libfun(x) 」は、 例えば [ステップ 1](Step1_Japanese.md) や [ステップ 2](Step2_Japanese.md) で扱った、電卓に入力する計算式の中で、 以下の例のように使用できます：

    INPUT:
    1 + libvar

    OUTPUT:
    3

    INPUT:
    libfun(1.23)

    OUTPUT:
    3.46

同様に、[ステップ 3](Step3_Japanese.md) でのスクリプト内でも使用できます。

## 新しいライブラリスクリプトの追加方法（読み込み登録）

必要に応じて、別の新しいライブラリ スクリプトのファイルを作成し、その中で変数や関数を定義する事もできます。

新しいライブラリ スクリプト ファイルを作成した際は、 そのファイルを RINPn の「 lib 」フォルダ内（またはその子孫階層のフォルダ内）に置き、 そのファイルパスを、「 lib 」フォルダ直下にあるテキストファイル「 VnanoLibraryList.txt 」内に記載（追記）してください。 そうすると、そのライブラリ スクリプトが読み込まれるようになります。

---

## 商標等に関する表記

* Oracle と Java は、Oracle Corporation 及びその子会社、関連会社の米国及びその他の国における登録商標です。文中の社名、商品名等は各社の商標または登録商標である場合があります。

* Microsoft Windows は、米国 Microsoft Corporation の米国およびその他の国における登録商標です。

* Linux は、Linus Torvalds 氏の米国およびその他の国における商標または登録商標です。

* ChatGPT は、米国 OpenAI OpCo, LLC による米国またはその他の国における商標または登録商標です。

* その他、文中に使用されている商標は、その商標を保持する各社の各国における商標または登録商標です。

