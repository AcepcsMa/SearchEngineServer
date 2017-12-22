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
    $("#start-datepicker").attr("value", "");
    $("#end-datepicker").attr("value", "");
}

function fill(data) {

    var imgSuffix = "?imageView2/1/w/250/h/250/format/jpg/q/75|imageslim";
    var div = document.createElement("div");
    div.setAttribute("class", "mansory");

    // for(var i = 0, count = 1;i < data.length;i++, count++) {
    //     avatar = data[i]["avatarUrl"];
    //     desc = data[i]["description"];
    //     title = data[i]["title"];
    //     url = data[i]["url"]
    //     var figure = document.createElement("figure");
    //     figure.className = "item";
    //     var a = document.createElement("a");
    //     a.setAttribute("href", url);
    //     a.setAttribute("target", "_blank");
    //     var img = document.createElement("img");
    //     img.setAttribute("src", avatar+imgSuffix);
    //     var caption = document.createElement("figcaption");
    //     caption.innerText = title;
    //     a.appendChild(img);
    //     a.appendChild(caption);
    //     figure.appendChild(a);

    //     div.appendChild(figure);
    //     if(count % 5 == 0) {
    //         $("#results").append(div);
    //         div = document.createElement("div");
    //         div.setAttribute("class", "mansory");
    //     }
    // }

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
        // caption.innerText = title;
        
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
}

function loadSearchData(pageCount) {

    var startAt = getUrlParam("start");
    var endAt = getUrlParam("end");

    var params = {
        "query": $("#query_box").val(),
        "page": pageCount,
        "startAt": startAt,
        "endAt": endAt
    };
    $.ajax({
        url: "/loadSearchResult",
        type: "POST",
        dataType: "json",
        contentType : "application/json; charset=utf-8",
        data: JSON.stringify(params),
        success: function(data, status) {
            fill(data);
        }
    });
}

function loadMoreData() {
    curPage++;
    loadSearchData();
}

// function onScroll() {
//     console.log("hh");
//     var scrollTop = $(window).scrollTop();
//     var scrollHeight = $(document).height();
//     var windowHeight = $(window).height();
//     if (scrollTop + windowHeight == scrollHeight) {
//         curPage++;
//         loadSearchData(curPage);
//     }
// }