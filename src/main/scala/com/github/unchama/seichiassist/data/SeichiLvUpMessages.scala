package com.github.unchama.seichiassist.data

object SeichiLvUpMessages {
  private val messages = Map(
    1 -> "整地鯖では整地をするとレベルが上がり、様々な恩恵が受けられます。\n初めての方は整地ワールドで掘ってレベルを上げてみましょう！\n木の棒を右クリックしてメニューを開き右上のビーコンボタンをクリック！",
    2 -> "おめでとうございます！最初のレベルアップです！\nレベル8に到達すると整地するだけで経験値がもらえるようになります。",
    3 -> "やけに早く掘れる気がする…？それは仕様です＾＾\n整地鯖では接続人数や採掘量に応じて採掘速度にブーストが掛かります。",
    4 -> "整地鯖では死んでもロストしません。気軽に冒険、整地してくださいね。\nそういえば当鯖の「ルール」もう確認しましたか？\n手遅れになる前に確認しておきましょうネ。木の棒メニューからどうぞ。",
    5 -> "Dynmapでどこに誰がいるのか、何があるのかなどを見ることができます。\nURLはこちら→https://www.seichi.network/map",
    6 -> "各ワールドへの移動方法は覚えていますか？\n木の棒メニューのビーコンボタンをクリックするとすぐ行けますよ！",
    7 -> "あと300ブロックほど整地するとガチャ券を獲得できます！\nガチャ券を右クリックすると豪華景品が獲得できるかも？",
    8 -> "おめでとう！新しいパッシブスキル[マナ獲得]を解除しました。\n特定のブロック破壊で確率でマナを獲得できるようになりました！",
    9 -> "整地鯖をもっと良くするために、是非JPMCSに投票をお願いします。\n1日1回投票出来ます。投票すると様々な特典を受け取れます！\nURLはこちら→https://minecraft.jp/servers/54d3529e4ddda180780041a7/vote",
    10 -> "初めてのアクティブスキル[デュアルブレイク]を習得できます！\n習得するには木の棒メニューのスキルブックをクリック！\nレベル12に到達するとMineStack機能が使えるようになります。\nブロック破壊スキルでは溶岩も消去することができますが溶岩1個につきブロック10個分の耐久値を消費します。",
    11 -> "そろそろ家建てません…？自分の家はメインワールドで建てることができます。\n他人の保護の無い所に保護を設定してから建てましょう。\n保護方法は公式wikiに詳しい説明があります。木の棒メニューからどうぞ。\n自分のチェストは設定した保護内に置けば自動でロックされます。",
    12 -> "MineStack機能を解除しました！木の棒メニューから呼び出せます。\n対象ブロックを無限にスタック出来、いつでも取り出せます。\nレベル16に到達すると四次元ポケットが使えるようになります。",
    13 -> "建築の時など、採掘速度のブースト効果が邪魔な時は\n木の棒メニューから効果のONとOFFを変更できます。",
    14 -> "「ガチャ券がイチイチ地面に落っこちるのがうざい！」\nって人に朗報です。木の棒メニューからガチャ券の取得方法を変更できます。",
    15 -> "家を建てたら木の棒メニューからホームポイントを設定しておきましょう。\nどこからでもクリック1つで自宅へ瞬間移動できますよ！",
    18 -> "整地の際に獲得できる経験値量が増えました。\n経験値量は10レベルごとに増加し、スキルがより長く使えるようになります！",
    20 -> "新しいアクティブスキル[トリアルブレイク]を習得できます！\n習得するには木の棒メニューのスキルブックをクリック！",
    25 -> "どこでもエンダーチェスト機能を解除しました！\n木の棒メニューから呼び出せます。",
    28 -> "整地の際に獲得できる経験値量が増えました。",
    30 -> "新しいアクティブスキル[エクスプロージョン]を習得できます！\n習得するには木の棒メニューのスキルブックをクリック！",
    38 -> "整地の際に獲得できる経験値量が増えました。",
    40 -> "新しいアクティブスキルを習得できます！\n習得するには木の棒メニューのスキルブックをクリック！\nツリーが分かれていますので、お好きなスキルをドウゾ。",
    46 -> "四次元ポケットの容量が増加しました！",
    48 -> "整地の際に獲得できる経験値量が増えました。",
    56 -> "四次元ポケットの容量が増加しました！",
    58 -> "整地の際に獲得できる経験値量が増えました。",
    66 -> "四次元ポケットの容量が増加しました！",
    68 -> "整地の際に獲得できる経験値量が増えました。",
    78 -> "整地の際に獲得できる経験値量が増えました。",
    88 -> "整地の際に獲得できる経験値量が増えました。",
    98 -> "整地の際に獲得できる経験値量が増えました。"
  )

  def get(level: Int): Option[String] = messages.get(level)
}