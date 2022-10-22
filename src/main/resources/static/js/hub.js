var ID_LIST = [];
var FIND_ID_START = 0;//用來記錄從哪個ID_LIST查起
var ALREADY_FIND_ID = false;
var FIND_ART_LOCK = false;
const DEL_AUTHOR = "這則留言已被本人刪除";
const DEL_WORD = "已經刪除的內容就像青春一樣回不去了";
const STATUS_TYPE = {"NORMAL" : "NORMAL", "DELETED" : "DELETED"};

/* --- 往下滑動找更多文章 --- */
function scrollDown(){
    if(FIND_ART_LOCK){
        console.log("FIND_ART_LOCK=true, skip find art");
        return;
    }
    if(FIND_ID_START >= ID_LIST.length){
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
    if(ALREADY_FIND_ID && scrollTop > 30){
        console.log("Can load id set again");
        ALREADY_FIND_ID = false;
    }
    if(!ALREADY_FIND_ID && scrollTop < 20){
//        location = location;
    }
}

function init(){
    //回覆視窗控制
    $("body").click(function(e) {
        var box = $(".reply-box");
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

    findIdSet();
    findPost();
}

/* --- 查詢所有文章id --- */
function findIdSet(){
    ALREADY_FIND_ID = true;
    post("/post/findIdSet", {}, findIdSetAfter, findIdSetError);
}

function findIdSetAfter(returnList){
    ID_LIST = returnList;
    console.log("ID_LIST size=" + ID_LIST.length);
}

function findIdSetError(xhr){
    showConsoleBox("看起來泡麵打翻機台了, 請稍後再進來試試");
}

/* --- 查詢文章 --- */
function findPost(){
    FIND_ART_LOCK = true;
    var data = {
        "idList" : ID_LIST.slice(FIND_ID_START, FIND_ID_START + 10)
    }

    post("/post/findPost", data, findPostAfter, findPostError);
}

function findPostAfter(postDTO){
    FIND_ID_START += postDTO.length;
    for(let art of postDTO){
        $("#art-box").append(makeArt(art));
    }
    FIND_ART_LOCK = false;
}

function findPostError(xhr){
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
    if(contNum > 1 && contNum == startNo)
        art.find(".open").attr("onclick", "");

    art.find(".open").text(getOpenStr(contNum, startNo));
    art.find(".reply").removeClass("disable");
}

function findTopContError(xhr){
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
    closeNewBox();
    $("#new-title").val("");
    $("#new-textarea").val("");
    showConsoleBox("文章發布成功!!");
    setInterval(function(){
            location = location;
        }, 1000);
}

function createPostError(xhr){
    showConsoleBox("文章發布失敗, 請稍後再試");
}

/* --- 新增留言 --- */
function replyPost(e){
    var art = $(e).closest(".art")
    if(art.find(".reply-textarea").val().trim() == ""){
        showConsoleBox("留言內容不能為空白!!");
        return;
    }
    var data = {
        "id" : art.attr("id"),
        "word" : art.find(".reply-textarea").val()
    }
    post("/post/replyPost", data, replyPostAfter, replyPostError, art);
}

function replyPostAfter(postDTO, art){
    closeReplyBox(art);
    art.find(".reply-textarea").val("");
    findTopCont(art[0]);
}

function replyPostError(xhr){
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

function deletePostError(xhr){
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

function deleteContentError(xhr){
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

/* --- 影像上傳 --- */
function uploadImg(e){
    var art = $(e).closest(".art");
    var imgBase64 = art.find(".upload-img").attr("src");
    if(imgBase64 == null || imgBase64 == "") return;

    var data = {
        "id" : art.attr("id"),
        "imgBase64" : imgBase64
    };
    post("/post/uploadImg", data, uploadImgAfter, uploadImgError, art);
}

function uploadImgAfter(PostDTO, art){
    var word = art.find(".reply-textarea").val();
    art.find(".reply-textarea").val(word + "\n" + PostDTO.imgUrl);
    $(e).closest(".art").find(".reply-img-view").empty();
    $(e).closest(".art").find(".reply-img-upload").addClass("disable");
}

function uploadImgError(xhr){
    showConsoleBox("上傳圖片失敗, 請稍後再試");
}

/* --- 影像壓鎖與預覽 --- */
function replyImg(e){
    if(e.files == null || e.files.length == 0 || e.files[0] == null){
        return;
    }

    var file = e.files[0];
    var fileName = /image\/\w+/g;
    if(!fileName.test(file.type)){//MIME iMAGE
        showConsoleBox("圖片格式錯誤");
        return;
    }

    var imgView = $(e).closest(".art").find(".reply-img-view");
    var imgUpload = $(e).closest(".art").find(".reply-img-upload");
    imgView.empty();
    imgUpload.addClass("disable");

    convertToBase64(file).then(base64 => {
            console.log(base64);
            buildImg(base64).then(newBase64 => {
                    $("<img>", {class : "upload-img", src : newBase64, name : "preview"}).appendTo(imgView);
                    imgUpload.removeClass("disable");
                }).catch(e => {
                    console.log(e);
                    showConsoleBox("圖片壓縮失敗:" + e);
                });
        }).catch(e => {
            console.log(e);
            showConsoleBox("圖片讀取失敗:" + e);
        });
}

function convertToBase64(file) {
    return new Promise((resolve, reject) => {
        let reader = new FileReader()
        reader.onload = () => resolve(reader.result);
        reader.onerror = () => reject(reader.error);
        reader.readAsDataURL(file);//轉換成Base64
    });
}

function buildImg(base64) {
    return new Promise((resolve, reject) => {
        var image = new Image();//先不設定寬度px
        image.src = base64;//img中src可以直接接Base64
        image.onload = () => resolve(compressImg(image, 0.8, 450));
    });
}

function compressImg(image, quality, maxWidth){
    var width = image.width;
    var height = image.height;
    if(width > maxWidth){
        var scale = maxWidth / width;
        width *= scale;
        height *= scale;
    }
    console.log("Resize img from (width/height) " + image.width + "/" + image.height
                                        + " => " + width + "/" + height);

    var canvas = document.createElement("canvas");
    var context = canvas.getContext("2d");
    canvas.width = width;
    canvas.height = height;
    context.drawImage(image, 0, 0, width, height);
    newImg = canvas.toDataURL("image/jpeg", quality);//壓縮比例, 1表示無損壓縮
    console.log("After compressed, image size=" + Math.round(0.75 * newImg.length / 1000) + "kb");//byte約為base64編碼的0.75
    return newImg;
};

function showPreview(src, fileName) {
    $("#previewDiv").append(image);
}

/* --- 新增留言視窗 --- */
function openReplyBox(e){
    var now = new Date();
    var nowStr = `${now.getFullYear()}/${String(now.getMonth()).padStart(2, "0")}/${String(now.getDate()).padStart(2, "0")}`
            + ` ${String(now.getHours()).padStart(2, "0")}:${String(now.getMinutes()).padStart(2, "0")}`;
    var art = $(e).closest(".art")
    art.find(".reply-no").text(getNoStr(art.attr("cont-num")));
    art.find(".reply-time").text(nowStr);
    art.find(".reply-box").removeClass("disable");
    art.find(".reply-textarea").focus();
}

function closeReplyBox(art){
    if(art != null){
        art.find(".reply-box").addClass("disable");
    }else{//如果沒定義, 就關閉全部
        $(".reply-box").addClass("disable");
    }
}

/* --- 新增文章視窗 --- */
function openNewBox(){
    $("#new-box").addClass("big-box-open");
    $("#new-box").removeClass("big-box-close");
    $("#new-title").focus();
}

function closeNewBox(){
    $("#new-box").addClass("big-box-close");
    $("#new-box").removeClass("big-box-open");
}

/* --- 設定視窗 --- */
function openSettingBox(){
    $("#setting-box").addClass("big-box-open");
    $("#setting-box").removeClass("big-box-close");
}

function closeSettingBox(){
    $("#setting-box").addClass("big-box-close");
    $("#setting-box").removeClass("big-box-open");
}

function logout(){
    $("#logout").submit();
}

function login(){
    location = "login";
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

    //作者
    var bar = $("<div>", {class : "bar"}).appendTo(art);
    $("<img>", {class: "bar-head", src : ICON_USER}).appendTo(bar);
    $("<span>", {class : "author", text : c.author}).appendTo(bar);
    if(USER_ID == c.author) $("<div>", {class : "del", text : "刪除", onclick : "deletePost(this);"}).appendTo(bar);

    //文章內文
    var cont = $("<div>", {class : "cont", "no" : c.no,
        "is-user-like" : c.isUserLike, "likes" : c.likes}).appendTo(art);
    $("<p>", {class : "title", text : a.title}).appendTo(cont);
    var word = $("<div>", {class : "word"}).appendTo(cont);
    getWordHtml(word, c.word);

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
    $("<p>", {class : (a.contNum > 1 ? "reply disable" : "reply"), text : "新增留言", onclick : "openReplyBox(this);"}).appendTo(move);

    //回覆視窗
    var replyBox = $("<div>", {class : "reply-box disable"}).appendTo(art);
    var replyBar = $("<div>", {class : "bar"}).appendTo(replyBox);
    $("<img>", {class : "bar-head", src : ICON_USER}).appendTo(replyBar);
    $("<span>", {class : "author", text : "匿名"}).appendTo(replyBar);

    var replyInfo = $("<p>" , {class : "reply-info"}).appendTo(replyBox);
    $("<span>", {class : "reply-no"}).appendTo(replyInfo);
    $("<span>", {class : "splitter", text : getSplitter()}).appendTo(replyInfo);
    $("<span>", {class : "reply-time"}).appendTo(replyInfo);

    $("<textarea>", {class : "reply-textarea", placeholder : "留言..."}).appendTo(replyBox);
    var replyMove = $("<div>" , {class : "move"}).appendTo(replyBox);
    var replyImgLabel = $("<label>", {class : "reply-img"}).appendTo(replyMove);
    $("<img>", {src : ICON_USER}).appendTo(replyImgLabel);
    $("<input>", {type : "file", accept : "image/*", onchange : "replyImg(this);"}).appendTo(replyImgLabel);
    $("<div>", {class : "reply-summit", text : "送出", onclick : "replyPost(this);"}).appendTo(replyMove);
    $("<div>", {class : "reply-img-view"}).appendTo(replyBox);
    $("<p>", {class : "reply-img-upload disable", text : "確定圖片", onclick : "uploadImg(this);"}).appendTo(replyBox);

    return art;
};

function makeCont(c){
    if(c.status == STATUS_TYPE.DELETED){
        var cont = $("<div>", {class : "cont", "no" : c.no});

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
    var word = $("<div>", {class : "word"}).appendTo(cont);
    getWordHtml(word, c.word);

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
    return ", ";
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

    if(remain == 0)//已展開全部留言
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
    var week = 604800000;

//超過一個月採另一種計算方式, 忽略時間部分計算
//    var countMonth = (now.getYear() - date.getYear()) * 12
//                    + now.getMonth() - date.getMonth()
//                    + (now.getDate() < date.getDate() ? -1 : 0);//補回不足的月
//
//    if(countMonth >= 12)
//        return Math.floor(countMonth / 12) + "年前";
//
//    if(countMonth > 0)
//        return countMonth + "個月前";

    if(gap >= week)//超過一周直接顯示日期
        return (now.getYear() > date.getYear() ? date.getYear() + "年 " : "")
                + date.getMonth() + "月 " + date.getDate() + "日";

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

function getWordHtml(e, word){
    var urlExp = /https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)/gi;
    var bxExp = /b\d+(?=$| )/gi;
    var imgExp = /(http(s?):)([/|.|\w|\s|-])*\.(?:jpg|gif|png)/gi;

    var map = new Map();
    var lines = word.split("\n");
    for(let line of lines){
        var mark = 0xf490;//私人區間, 不太可能出現, 分隔符剛好不會重疊

        line = line.replace(imgExp, function (imgUrl){
                var key = String.fromCharCode(mark);
                var span = $("<span>", {class : "word-img", text : imgUrl});
                $("<img>", {src : imgUrl, alt : imgUrl}).appendTo(span);
                map.set(mark, span);
                mark++;
                return key;
            }).replace(urlExp, function (url){
                var key = String.fromCharCode(mark);
                map.set(mark, $("<a>", {href : url, text : url, target : "_blank"}));
                mark++;
                return key;
            }).replace(bxExp, function (bx){
                var key = String.fromCharCode(mark);
                var no = bx.substr(1);
                map.set(mark, $("<span>", {class : "bx", text : bx, onclick : `goToBx(this, ${no});`}));
                mark++;
                return key;
            });

        var last = 0;
        for(let i = 0; i < line.length; i++){
            if(map.has(line.charCodeAt(i))){
                $("<span>", {text : line.substring(last, i)}).appendTo(e);
                last = i + 1;//跳過這個替換元

                map.get(line.charCodeAt(i)).appendTo(e);
            }
        }

        if(last < line.length){
            $("<span>", {text : line.substring(last, line.length)}).appendTo(e);
        }

        $("<br>").appendTo(e);
    }

    return e;
}

/* --- 連結位置移動 --- */
function goToBx(e, no){
    var contEle = $(e).closest(".art").find(`[no=${no}]`)[0]
    if(contEle == null) return;

    goTo(contEle);
}

function goTo(element = "body"){
	var tobBoxHeight = $("#top-box")[0].offsetHeight;//取得tobBox高度

	//給上方留空格
    $("html, body").animate({
		scrollTop: $(element).offset().top - tobBoxHeight
	}, 500);
}