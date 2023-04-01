function calUV(){
    var start = document.getElementById("uvstart").value;
    var end = document.getElementById("uvend").value;
    if (end == "" || start == ""){
        alert("日期未选中！");
        return;
    }
    if (end < start) {
        alert("结束日期不得早于开始日期");
        return;
    }
    $.post(
        CONTEXT_PATH + "/data/uv",
        {"start": start, "end": end},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                //window.location.reload(); // 刷新页面
                $(uvres).text(data.uvResult);
            } else {
                alert(data.msg);
            }
        }
        );
}

function calDAU(){
    var start = document.getElementById("daustart").value;
    var end = document.getElementById("dauend").value;
    if (end == "" || start == ""){
        alert("日期未选中！");
        return;
    }
    if (end < start) {
        alert("结束日期不得早于开始日期");
        return;
    }
    $.post(
        CONTEXT_PATH + "/data/dau",
        {"start": start, "end": end},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                //window.location.reload(); // 刷新页面
                $(daures).text(data.dauResult);
            } else {
                alert(data.msg);
            }
        });
}