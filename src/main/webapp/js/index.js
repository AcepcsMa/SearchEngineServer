function ac() {
    // params = {
    //     "suggest": {
    //         "album-suggest": {
    //             "prefix": $("#query_box").val(),
    //             "completion": {
    //                 "field": "suggest"
    //             }
    //         }
    //     }
    // };

    // $.ajax(
    //     {
    //         type : "POST",
    //         dataType : "json",
    //         url : "http://localhost:9200/albums/album/_search",
    //         contentType : "application/json; charset=utf-8",
    //         data : JSON.stringify(params),
    //         success : function(data, status) {
    //             suggestions = new Array();
    //             for(var i = 0;i < data["suggest"]["album-suggest"][0]["options"].length;i++) {
    //                 suggestions.push(data["suggest"]["album-suggest"][0]["options"][i]["_source"]["title"]);
    //             }
    //             $( "#query_box" ).autocomplete({
    //                 source: suggestions
    //               });
    //         }
    //     }
    // );

//    $.get("/ac",
//        {"query":$("#query_box").val()},
//        function(data) {
//            $( "#query_box" ).autocomplete({
//                source: data
//                });
//        });
}