function getUrlParam(name) {
    //构造一个含有目标参数的正则表达式对象
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    //匹配目标参数
    var r = window.location.search.substr(1).match(reg);
    //返回参数值
    if (r != null) {
        return unescape(r[2]); 
    }
    return null; 
}


// function tfc() {
//      if($(".dropdown-content").css("display") == "block") {
//          $(".dropdown-content").css("display", "none");
//      } else {
//          $(".dropdown-content").css("display", "block");
//      }
// }

function applyTimeFilter() {
    var start = $("#start-datepicker").val();
    var end = $("#end-datepicker").val();
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
    for(var i = 0;i < data.length;i++) {
        avatar = data[i]["avatarUrl"];
        desc = data[i]["description"];
        title = data[i]["title"];
        url = data[i]["url"]
        var result = document.createElement("figure");
        result.className = "item";
        var a = document.createElement("a");
        a.setAttribute("href", url);
        var img = document.createElement("img");
        img.setAttribute("src", avatar+imgSuffix);
        var caption = document.createElement("figcaption");
        caption.innerText = title;
        a.appendChild(img);
        a.appendChild(caption);
        result.appendChild(a);
        // console.log(url);

        div.appendChild(result);

    }
    if(data.length > 0) { 
        $("#results").append(div);
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
    // console.log(curPage);
    loadSearchData();
}

