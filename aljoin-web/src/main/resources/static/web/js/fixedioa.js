/**
 * 固定表单流程审批工具类
 * @date: 2017/11/07
 * @author: wangj
 */
var fixed = {
    /**
     * 填写意见
     * @param selector 按钮
     * @param taskId   任务ID
     * @param _csrf_   token
     * @param message  意见
     */
    addComment : function (selector,taskId,_csrf,message) {
        pform.on('submit('+selector+')', function(data) {
            var param = {
                "taskId":taskId,
                _csrf:_csrf,
                "message":message
            };
            tool.post("/act/actFixedForm/addComment",param ,postCallBackPubInfo, false);
            return false;
        });
    },
    /**
     * 获得上一个节点的信息
     * @param taskId  任务ID
     * @param _csrf   token
     */
    getPreTaskInfo : function (taskId,_csrf) {
        var pm = {
            taskId:taskId,
            _csrf:_csrf
        };
        tool.post("/act/actFixedForm/getPreTaskInfo",pm,function(retMsg){
            $(".agree").hide();
            $(".noagree").show();
            $(".yes").removeClass("blue_border");
            $(".no").addClass("blue_border");
            if(retMsg.object != null){
                if(retMsg.object.length > 0){
                    var data = retMsg.object[0];
                    $("#pre-nodeName").text(data.nextNodeName);
                }else{
                    $(".noagree").attr("disabled",true);
                }
            }
        },false);
    },
    /**
	 * 回退到上一节点
     * @param selector 按钮
     * @param taskId   任务ID
     * @param _csrf    token
     */
    jumpTask : function (selector,url,taskId,_csrf) {
        pform.on('submit('+selector+')', function(data) {
            var param = {
                "taskId":taskId,
                _csrf:_csrf
            };
            tool.post(url,param ,postCallBackPubInfo, false);
            return false;
        });
    },
    /**
     * 检查下个节点指定是 个人（多个人）、部门、岗位
     * @param url
     * @param taskId
     * @param _csrf
     */
    checkNextTaskInfo : function (url,taskId,_csrf) {
        var pm = {
            taskId:taskId,
            _csrf:_csrf
        };
        tool.post(url,pm,function(retMsg){
            var data = retMsg.object;
            if(null != data){
                if(data.isOrgn == false){
                    $("#audit-orgn").hide();
                    $("#audit-member").show();
                    var userList = data.user;
                    if(userList != 'undefined'){
                        var str = '';
                        var count = 0;
                        var strId = ''
                        var strName = '';
                        $(userList).each(function(index){
                            var data = userList[index];
                            str += '<a class="seitem" value="'+data.id+'">'+data.fullName+'</a>';
                            strId += data.id+";";
                            strName += data.fullName +";";
                            count ++;
                        });
                        $("#audit-member-list").empty();
                        $("#audit-member-list").append(str);

                        $("#testarea").text(strName);
                        $("#testarea").attr("userId",strId);
                    }
                }else{
                    $("#audit-orgn").show();
                    $("#audit-member").hide();
                    var deptList = data.dept;
                    if(deptList != 'undefined'){
                        var strId = '';
                        var strName = '';
                        $(deptList).each(function(index){
                            var data = deptList[index];
                            strId += data.userId+";";
                            strName += data.fullName+";";
                        });
                        $("#n_receiverNameid").val(strId);
                        $("#n_receiverName").text(strName);
                    }
                }
            }
        },false);
    },
    /**
     * 获取日志信息
     * @param url
     * @param taskId
     * @param _csrf
     */
    getAllTaskLogInfo : function (url,taskId,_csrf) {
        var pm = {
            taskId:taskId,
            _csrf:_csrf
        };
        tool.post(url,pm,function(data){
            var loglist={loglist:data};
            $('#paging2-data').html(template("paging2-script", loglist));
            var index = layer.open({
                title : '日志',
                maxmin : false,
                type : 1,
                content : $('#win-daily-object')
            });
            layer.full(index);
        },false);
    }
}

/**
 * 回调函数
 * @param retMsg
 */
function postCallBackPubInfo(retMsg){
    if (retMsg.code == 0) {
        layer.alert(retMsg.message, {
            title : "操作提示",
            icon : 1
        }, function(index) {
            parent.layer.closeAll();
            parent.loadData();
        });
    } else {
        tool.error(retMsg.message);
    }
}
