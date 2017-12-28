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
        var h4 = document.createElement("h4");
        h4.innerText = title;
        var innerP = document.createElement("p");
        innerP.innerText = picCount + " photos";
        innerDiv.appendChild(h4);
        innerDiv.appendChild(innerP);
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
}

function loadSearchData(pageCount, size) {

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

// function loadMoreData() {
//     curPage++;
//     loadSearchData();
// }

function initData() {
    initSize = 20;
    loadSearchData(curPage, initSize);
    curPage += 2;
}