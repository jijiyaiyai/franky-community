$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        processData: false, //拒绝将表单内容转化为json
        contentType: false, //禁止jQuery设置文件类型，防止文件传输出错
        data: new FormData($("#uploadForm")[0]), //取[0]: dom对象
        success: function(data) {
            if(data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 0) {
                            window.location.reload(); // 刷新页面
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传头像失败!");
            }
        }
    });
    return false;
}