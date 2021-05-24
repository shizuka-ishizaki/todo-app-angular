// DOM読み込みが完了してから処理
document.addEventListener("DOMContentLoaded",function(){
  // HTMLCollectionを配列に変換しつつ削除アイコンを取得
  Array.from(
    document.getElementsByClassName("delete")
    // それぞれのアイコンに削除フォーム実行のonclickイベントを設定
  ).forEach(action => {
    // eventを取得して、クリックされた要素(target)の親要素であるformをsubmitする
    action.addEventListener("click", (e) => {
      var result = window.confirm("削除するカテゴリを設定しているToDoも削除されます。\n本当に削除してもいいですか？");
      // var result = window.confirm("本当に削除してもいいですか？");
      if( result ) {
        e.currentTarget.parentNode.submit();
      }　else {
         window.alert('キャンセルされました');
      }
    });
  });
});