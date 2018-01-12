function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]); 
    }
    return null; 
}

function applyTimeFilter() {
    var start = $("#start-datepicker").val();
    var end = $("#end-datepicker").val();

    if(start.length == 0) {
        start = "1970-01-01";
    }
    if(end.length == 0) {
        end = $.datepicker.formatDate("yy-mm-dd", new Date());
    }

    var url = "/search?query=" + $("#query_box").val() 
                + "&start=" + start 
                + "&end=" + end;
    location.href = url;
}

function resetTimeFilter() {
    var dates = $(".datepicker");
    dates.each(function() {
        $.datepicker._clearDate(this);
    });
}

function fill(data, pageCount) {

    if(data.length == 0 && notFound == true) {
        $("#loading").remove();
        return;
    } else if(data.length == 0 && pageCount < 1) {
        errorInfo = document.createElement("div");
        errorInfo.setAttribute("id", "error_info");
        
        logoDiv = document.createElement("div");
        logoDiv.setAttribute("id", "error_logo_div");
        logoImg = document.createElement("img");
        logoImg.setAttribute("src", "http://p0u4yewt0.bkt.clouddn.com/icon48.png");
        logoDiv.appendChild(logoImg);

        msgDiv = document.createElement("div");
        msgDiv.setAttribute("id", "error_msg_div");
        span = document.createElement("span");
        span.innerText = "Sorry, we can't find any related information...";
        span.setAttribute("style", "padding-left:10px");
        msgDiv.appendChild(span);
        $("#results").css("max-width", "100%");
        $("#results").css("width", "100%");
        $("#results").append(logoDiv);
        $("#results").append(msgDiv);
        $("#results").css("background-color", "rgb(248, 240, 226)");
        $("#results").css("text-align", "center");
        var phs = $("#results").siblings("#ph");
        phs.each(function() {
            this.remove();
        });
        $("#error_logo_div").css("margin-top", $(window).innerHeight()/3 + "px");
        $("#error_msg_div").css("margin-top", $(window).innerHeight()/3 + "px");

        notFound = true;
        $("#loading").remove();
        return;
    } else if(data.length == 0 && pageCount >= 2) {
        alert("Sorry, there is no more data...");
    }


    var imgSuffix = "?imageMogr2/auto-orient/thumbnail/250x250!/blur/1x0/quality/100|imageslim";
    var div = document.createElement("div");
    div.setAttribute("class", "mansory");

    for(var i = 0, count = 1;i < data.length;i++, count++) {
        avatar = data[i]["avatarUrl"];
        desc = data[i]["description"];
        title = data[i]["title"];
        url = data[i]["url"];
        picCount = data[i]["picCount"];
        var figure = document.createElement("figure");
        figure.className = "item";
        var a = document.createElement("a");
        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");
        var img = document.createElement("img");
        img.setAttribute("src", avatar+imgSuffix);
        var caption = document.createElement("figcaption");
        
        var innerDiv = document.createElement("div");
        var span1 = document.createElement("span");
        span1.innerHTML = title;
        var p2 = document.createElement("p");
        p2.innerText = picCount + " images";
        innerDiv.appendChild(span1);
        innerDiv.appendChild(p2);
        caption.appendChild(innerDiv);

        a.appendChild(img);
        a.appendChild(caption);
        figure.appendChild(a);
        div.appendChild(figure);

        if(count % 5 == 0) {
            $("#results").append(div);
            div = document.createElement("div");
            div.setAttribute("class", "mansory");
        }
    }
    $(".lm-btn").css("display", "initial");
    $(".float-btn").css("display", "initial");
    $("#loading").remove();
    $(".lm-btn").css("background-image", "");
    $(".lm-btn").text("Load More");
}

function loadSearchData(pageCount, size) {

    $(".lm-btn").css("background", "rgb(21, 21, 36) url(http://p0u4yewt0.bkt.clouddn.com/button_load.gif?imageMogr2/auto-orient/thumbnail/30x30!/blur/1x0/quality/100|imageslim) center center no-repeat")
    $(".lm-btn").text("");

    var startAt = getUrlParam("start");
    var endAt = getUrlParam("end");

    var params = {
        "query": $("#query_box").val(),
        "page": pageCount,
        "startAt": startAt,
        "endAt": endAt,
        "size": size
    };
    $.ajax({
        url: "/loadSearchResult",
        type: "POST",
        dataType: "json",
        contentType : "application/json; charset=utf-8",
        data: JSON.stringify(params),
        success: function(data, status) {
            fill(data, pageCount);
        }
    });
}

function loadRecommend() {

    var params = {
        "query": $("#query_box").val()
    };
    $.ajax({
        url: "/recommend",
        type: "POST",
        dataType: "json",
        contentType : "application/json; charset=utf-8",
        data: JSON.stringify(params),
        success: function(data, status) {
            console.log(data);
            fillRecommend(data);
        }
    });
}

function fillRecommend(data) {
    var divRecommend = document.getElementById("recommend");
    for(var i = 0;i < data.length;i++) {
        var a = document.createElement("a");
        a.innerText = data[i];
        a.setAttribute("href", "/search?query=" + data[i]);
        divRecommend.appendChild(a);
    }
}

// function loadMoreData() {
//     curPage++;
//     loadSearchData();
// }

function initData() {

    // set loading gif
    loadingImg = document.createElement("img");
    loadingImg.setAttribute("id", "loading");
    loadingImg.setAttribute("src", "http://p0u4yewt0.bkt.clouddn.com/load1.gif");
    var results = document.getElementById("results");
    results.appendChild(loadingImg);

    initSize = 20;
    loadSearchData(curPage, initSize);
    curPage += 2;
}

function goTop() {
    BackTop=function(btnClass){
        var btn=document.getElementsByClassName(btnClass)[0];
        var d=document.documentElement;
        window.onscroll=set;
        btn.onclick=function (){
            btn.style.display="none";
            window.onscroll=null;
            this.timer=setInterval(function(){
                d.scrollTop-=Math.ceil(d.scrollTop*0.1);
                if(d.scrollTop==0) clearInterval(btn.timer,window.onscroll=set);
            },25);
        };
        function set(){btn.style.display=d.scrollTop?'block':"none"}
    };
    BackTop('float-btn');
}