var ART_LIST = [];
var FIND_ART_START = 0;//用來記錄從哪個ART_LIST查起
var FIND_ART_LOCK = false;
var ALREADY_FIND_SET = false;
const DEL_AUTHOR = "這則留言已被本人刪除";
const DEL_WORD = "已經刪除的內容就像青春一樣回不去了";
const STATUS_TYPE = {"NORMAL" : "NORMAL", "DELETED" : "DELETED"};

/* --- 往下滑動找更多文章 --- */
function scrollDown(){
    if(FIND_ART_LOCK){
        console.log("FIND_ART_LOCK=true, skip find art");
        return;
    }
    if(FIND_ART_START >= ART_LIST.length){
        console.log("Already find all arts, skip find art");
        return;
    }

    var clientHeight = document.documentElement.clientHeight;
    var scrollTop = document.documentElement.scrollTop;
    var scrollHeight = document.documentElement.scrollHeight;
    if(clientHeight + scrollTop + 100 > scrollHeight){
        console.log("Loading posts");
        findPost();
    }
}

/* --- 往上滑動重新整理 --- */
function scrollUp(){
    var scrollTop = document.documentElement.scrollTop;
    if(ALREADY_FIND_SET && scrollTop > 30){
        console.log("Can load art set again");
        ALREADY_FIND_SET = false;
    }
    if(!ALREADY_FIND_SET && scrollTop < 20){
//        location = location;
    }
}

//$(window).off("scroll");
function init(){
    //回覆視窗控制
    $("body").click(function(e) {
        var box = $("#reply-box");
        var btn = $(".reply");
        if(!box.is(e.target) && !box.has(e.target).length > 0
            && !btn.is(e.target) && !btn.has(e.target).length > 0){
               closeReplyBox();
        }
    });

    //頁面滑動loading控制
    $(window).scroll(function(e) {
        scrollUp();
        scrollDown();
    });

    //上方填空空白
    var barHeight = $("#top-box")[0].offsetHeight;
    $("#header").css("height", barHeight + "px");

    findArtSet();
    findPost();
}

/* --- 查詢所有文章id --- */
function findArtSet(){
    ALREADY_FIND_SET = true;
    post("/post/findArtSet", {}, findArtSetAfter, findArtSetError);
}

function findArtSetAfter(returnList){
    ART_LIST = returnList;
    console.log("ArtSet size=" + ART_LIST.length);
}

function findArtSetError(){
    showConsoleBox("看起來泡麵打翻機台了, 請稍後再進來試試");
}

/* --- 查詢文章 --- */
function findPost(){
    FIND_ART_LOCK = true;
    var data = {
        "idList" : ART_LIST.slice(FIND_ART_START, FIND_ART_START + 10)
    }

    post("/post/findPost", data, findPostAfter, findPostError);
}

function findPostAfter(postDTO){
    FIND_ART_START += postDTO.length;
    for(let art of postDTO){
        $("#art-box").append(makeArt(art));
    }
    FIND_ART_LOCK = false;
}

function findPostError(){
    showConsoleBox("讀取文章異常");
    FIND_ART_LOCK = false;
}

/* --- 查詢留言 --- */
function findTopCont(e){
    var art = $(e).closest(".art")
    var data = {
        "id" : art.attr("id"),
        "no" : parseInt(art.attr("start-no"))
    }

    post("/post/findTopCont", data, findTopContAfter, findTopContError, art);
}

function findTopContAfter(contList, art){
    for(let cont of contList){
        art.children(".cont-box").append(makeCont(cont));
    }

    var contNum = parseInt(art.attr("cont-num"));
    var startNo = parseInt(art.attr("start-no")) + contList.length;
    art.attr("start-no", startNo);

    if(startNo > contNum){//一併更新contNum
        contNum = startNo;
        art.attr("cont-num", contNum)
    }

    //更新字句
    art.find(".open").text(getOpenStr(contNum, startNo));
    art.find(".reply").removeClass("disable");
}

function findTopContError(){
    showConsoleBox("讀取留言異常");
}

/* --- 新增文章 --- */
function createPost(){
    if($("#new-title").val().trim() == ""){
        showConsoleBox("文章標題不能為空白!!");
        return;
    }
    if($("#new-textarea").val().trim() == ""){
        showConsoleBox("文章內容不能為空白!!");
        return;
    }
    var data = {
        "title" : $("#new-title").val(),
        "word" : $("#new-textarea").val()
    }
    post("/post/createPost", data, createPostAfter, createPostError);
}

function createPostAfter(postDTO){
    //不做處理給user重整
    closeNewBox();
    $("#new-title").val("");
    $("#new-textarea").val("");
    showConsoleBox("文章發布成功!!");
}

function createPostError(postDTO){
    showConsoleBox("文章發布失敗, 請稍後再試");
}

/* --- 新增留言 --- */
function replyPost(){
    if($("#reply-textarea").val().trim() == ""){
        showConsoleBox("留言內容不能為空白!!");
        return;
    }
    var data = {
        "id" : $("#reply-textarea").attr("reply-id"),
        "word" : $("#reply-textarea").val()
    }
    replyArt = $("#" + $("#reply-textarea").attr("reply-id"));
    post("/post/replyPost", data, replyPostAfter, replyPostError, replyArt);
}

function replyPostAfter(postDTO, replyArt){
    closeReplyBox();
    $("#reply-textarea").val("");
    findTopCont(replyArt[0]);
}

function replyPostError(postDTO){
    showConsoleBox("留言失敗, 請稍後再試");
}

/* --- 刪除文章 --- */
function deletePost(e){
    var art = $(e).closest(".art")
    var data = {
        "id" : art.attr("id"),
        "no" : 0
    }
    post("/post/deletePost", data, deletePostAfter, deletePostError, art);
}

function deletePostAfter(res, art){
    art.find(".cont-box").html("");
    art.find(".title").text("已刪除文章");
    art.find(".word").text("");
    art.find(".likes").addClass("disable");
    art.find(".likes-icon").addClass("disable");
    art.find(".del").addClass("disable");
    art.find(".info").html("");
    art.find(".move").html("");
    showConsoleBox("刪除文章成功");
}

function deletePostError(res){
    showConsoleBox("文章刪除失敗, 刪除權限已過期");
}

/* --- 刪除留言 --- */
function deleteContent(e){
    var cont = $(e).closest(".cont");
    var data = {
        "id" : cont.closest(".art").attr("id"),
        "no" : parseInt(cont.attr("no"))
    }
    post("/post/deleteContent", data, deleteContentAfter, deleteContentError, cont);
}

function deleteContentAfter(res, cont){
    cont.find(".author").text(DEL_AUTHOR);
    cont.find(".word").text(DEL_WORD);
    cont.find(".likes").addClass("disable");
    cont.find(".likes-icon").addClass("disable");
    cont.find(".del").addClass("disable");
    showConsoleBox("刪除留言成功");
}

function deleteContentError(res){
    showConsoleBox("留言刪除失敗, 刪除權限已過期");
}

/* --- 按讚 --- */
function toggleLike(e){
    var cont = $(e).closest(".cont");
    if(cont.attr("is-user-like") == "true"){
        unlikeContent(cont);
    }else{
        likeContent(cont);
    }
}

function likeContent(cont){
    var data = {
        "id" : cont.closest(".art").attr("id"),
        "no" : cont.attr("no")
    }
    post("/post/likeContent", data, likeContentAfter, null, cont);
}

function likeContentAfter(postDTO, cont){
    if(postDTO.success){
        changeLike(true, cont);
    }else{
        console.log("Like content failed, already like it");
    }
}

function unlikeContent(cont){
    var data = {
        "id" : cont.closest(".art").attr("id"),
        "no" : cont.attr("no")
    }
    post("/post/unlikeContent", data, unlikeContentAfter, null, cont);
}

function unlikeContentAfter(postDTO, cont){
    if(postDTO.success){
        changeLike(false, cont);
    }else{
         console.log("Unlike content failed, already unlike it");
     }
}

function changeLike(isUserLike, cont){
    var likes = parseInt(cont.attr("likes"));
    likes = isUserLike ? likes + 1 : likes - 1;//先從頁面更新就好
    cont.attr("likes", likes);
    cont.attr("is-user-like", isUserLike);
    var likesEle = cont.find(".likes");
    if(likesEle.hasClass("likes-out")){
        likesEle.text(getLikeStr(isUserLike, likes));
    }else{
        likesEle.text(likes);
    }
}

/* --- 新增留言視窗 --- */
function openReplyBox(e){
    var now = new Date();
    var nowStr = `${now.getFullYear()}/${now.getMonth()}/${now.getDate()} ${now.getHours()}:${now.getMinutes()}`;
    var art = $(e).closest(".art")
    $("#reply-textarea").attr("reply-id", art.attr("id"));
    $("#reply-no").text(getNoStr(art.attr("cont-num")));
    $("#reply-time").text(nowStr);
    $("#reply-box").addClass("reply-open");
    $("#reply-box").removeClass("reply-close");
}

function closeReplyBox(){
    $("#reply-box").addClass("reply-close");
    $("#reply-box").removeClass("reply-open");
}

/* --- 新增文章視窗 --- */
function openNewBox(){
    $("#new-box").addClass("new-open");
    $("#new-box").removeClass("new-close");
}

function closeNewBox(){
    $("#new-box").addClass("new-close");
    $("#new-box").removeClass("new-open");
}

/* --- Console視窗 --- */
function showConsoleBox(str){
    $("#console-box").text(str);
    $("#console-box").addClass("console-open");
    $("#console-box").removeClass("console-close");
    setTimeout(closeConsoleBox, 3000);
}

function closeConsoleBox(){
    $("#console-box").addClass("console-close");
    $("#console-box").removeClass("console-open");
}

/* --- 文章與留言生成共用 --- */
function makeArt(postDTO){
    var a = postDTO;
    if(a.status == STATUS_TYPE.DELETED)
        return $("<div>", {id : a.id, class : "art disable", "status" : a.status});

    var c = postDTO.contList[0];
    var art = $("<div>", {id : a.id, class : "art", "cont-num" : a.contNum, "start-no" : 1});

    //作者與標題
    var bar = $("<div>", {class : "bar"}).appendTo(art);
    $("<img>", {class: "bar-head", src : ICON_USER}).appendTo(bar);
    $("<span>", {class : "author", text : c.author}).appendTo(bar);
    if(USER_ID == c.author) $("<div>", {class : "del", text : "刪除", onclick : "deletePost(this);"}).appendTo(bar);
    $("<p>", {class : "title", text : a.title}).appendTo(art);

    //文章內文
    var cont = $("<div>", {class : "cont", "no" : c.no,
        "is-user-like" : c.isUserLike, "likes" : c.likes}).appendTo(art);
    $("<pre>", {class : "word", text : c.word}).appendTo(cont);

    //訊息列
    var info = $("<p>" , {class : "info"}).appendTo(cont);
    $("<img>", {class : "likes-icon" , src : ICON_LIKE, onclick : "toggleLike(this);"}).appendTo(info);
    $("<div>", {class : "likes likes-out", text : getLikeStr(c.isUserLike, c.likes)}).appendTo(info);
    $("<span>", {class : "splitter", text : getSplitter()}).appendTo(info);
    $("<span>", {class : "time", text : getTimeFromStr(c.createDate)}).appendTo(info);

    //其他留言
    $("<div>", {class : "cont-box"}).appendTo(art);

    //操作列
    var move = $("<div>" , {class : "move"}).appendTo(art);
    $("<p>", {class : "open", text : getOpenStr(a.contNum, 1), onclick : "findTopCont(this);"}).appendTo(move);
    $("<p>", {class : "open-bot disable", text : "查看最新留言", onclick : "findBotCont(this);"}).appendTo(move);
    $("<p>", {class : (a.contNum > 1 ? "reply disable" : "reply"), text : "回覆", onclick : "openReplyBox(this);"}).appendTo(move);

    return art;
};

function makeCont(c){
    if(c.status == STATUS_TYPE.DELETED){
        var cont = $("<div>", {class : "cont", "art-id" : c.id, "no" : c.no});

        var barIn = $("<div>" , {class : "bar-in"}).appendTo(cont);
        $("<img>", {class: "bar-in-head", src : ICON_USER}).appendTo(barIn);
        $("<div>", {class : "author", text : DEL_AUTHOR}).appendTo(barIn);

        $("<pre>", {class : "word", text : DEL_WORD}).appendTo(cont);

        var info = $("<p>" , {class : "info"}).appendTo(cont);
        $("<span>", {class : "no", text : getNoStr(c.no)}).appendTo(info);
        $("<span>", {class : "splitter", text : getSplitter()}).appendTo(info);
        $("<span>", {class : "time", text : "已刪除"}).appendTo(info);
        return cont;
    }

    var cont = $("<div>", {class : "cont", "art-id" : c.id, "no" : c.no, "is-user-like" : c.isUserLike, "likes" : c.likes});

    //作者與讚
    var barIn = $("<div>" , {class : "bar-in"}).appendTo(cont);
    $("<img>", {class: "bar-in-head", src : ICON_USER}).appendTo(barIn);
    $("<div>", {class : "author", text : c.author}).appendTo(barIn);
    $("<img>", {class : "likes-icon", src : ICON_LIKE, onclick : "toggleLike(this);"}).appendTo(barIn);
    $("<div>", {class : "likes likes-in", text : c.likes}).appendTo(barIn);

    //留言
    $("<pre>", {class : "word", text : c.word}).appendTo(cont);

    //訊息列
    var info = $("<p>" , {class : "info"}).appendTo(cont);
    $("<span>", {class : "no", text : getNoStr(c.no)}).appendTo(info);
    $("<span>", {class : "splitter", text : getSplitter()}).appendTo(info);
    $("<span>", {class : "time", text : getTimeFromStr(c.createDate)}).appendTo(info);
    if(USER_ID == c.author) $("<span>", {class : "del", text : "刪除", onclick : "deleteContent(this);"}).appendTo(info);

    return cont;
};

function getNoStr(no){
    return `B${no}`;
}

function getSplitter(){
    return " ,  ";
}

function getLikeStr(isUserLike, likes){
    if(isUserLike){
        if(likes - 1 == 0){
            return "你說讚";
        }else{
            return `你與${likes - 1}人都說讚`
        }
    }
    return `${likes}人說讚`;
}

function getOpenStr(contNum, startNo){
    if(contNum == 1)//只有本文
        return "該文章尚無留言";

    var remain = contNum - startNo;

    if(remain == 0)//已全部展開
        return "";

    if(startNo == 1)//尚未展開
        return `查看全部${contNum - startNo}則留言`;

    return `查看剩餘${contNum - startNo}則留言`;
}

function getTimeFromStr(dateStr){
    if(dateStr == "") return "";

    var date = new Date(dateStr);
    var now = new Date();
    var gap = now - date;
    var s = 1000;
    var m = 60000;
    var h = 3600000;
    var day = 86400000;

    //超過一個月採另一種計算方式, 忽略時間部分計算
    var countMonth = (now.getYear() - date.getYear()) * 12
                    + now.getMonth() - date.getMonth()
                    + (now.getDate() < date.getDate() ? -1 : 0);//補回不足的月

    if(countMonth >= 12)
        return Math.floor(countMonth / 12) + "年前";

    if(countMonth > 0)
        return countMonth + "個月前";

    if(gap >= day)
        return Math.floor(gap / day) + "天前";

    if(gap >= h)
        return Math.floor(gap / h) + "小時前";

    if(gap >= m)
        return Math.floor(gap / m) + "分鐘前";

    if(gap >= s)
        return Math.floor(gap / s) + "秒前";

    if(gap >= 0)
        return "剛剛";

    return "";
}