function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}


// 置顶
function setTop(topBtn, type) {
    if (type == 0)type = 1;
    else type = 0;
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val(), "type":type},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(topBtn).text(data.type==1?'已置顶':'置顶');
            } else {
                alert(data.msg);
            }
        }
    );
}

// 加精
function setWonderful(wonderfulBtn, status) {
    if (status == 0)status = 1;
    else status = 0;
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val(), "status":status},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(wonderfulBtn).text(data.status==1?'已加精':'加精');
            } else {
                alert(data.msg);
            }
        }
    );
}

$(function(){
    $("#topBtn").click(setTop);
});

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    );
}