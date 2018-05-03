/**
 * 收文阅件流程审批工具类
 * @date: 2017/11/15
 * @author: wangj
 */
var readReceipt = {
    /**
     * 检查当前任务是否被签收
     * @param processInstanceId  流程实例ID
     * @param _csrf   token
     */
    checkClaim: function () {
        var processInstanceId = $("#processInstanceId").val();
        var pm = {
            processInstanceId: processInstanceId,
            _csrf: $("#_csrf").val()
        };
        tool.post("/ioa/ioaReceiveFile/checkIsClaim", pm, function (retMsg) {
            if (retMsg.code == 0) {
                $("#revoke-inread").hide();
            } else {
                $("#revoke-inread").show();
            }
        }, false);
    },
    /**
     * 签收任务
     * @param taskId 任务ID
     * @param _csrf  token
     */
    claimTask: function (_csrf) {
        layer.confirm('确定签收吗 ?', {
            icon: 3,
            title: '提示'
        }, function (index) {
            var data = {
                ids: taskId,
                _csrf: _csrf
            }
            tool.post("/ioa/ioaWaitWork/claimTask", data, function (retMsg) {
                if (retMsg.code == 0) {
                    //tool.success(retMsg.message);
                    layer.msg(retMsg.message,{
	    			  icon: 1,
	    				  time: 1500 //2秒关闭（如果不配置，默认是3秒）
	    				});
                    $(".sign").hide();
                    $(".btns").show();
                } else {
                    tool.error(retMsg.message);
                }
            }, false)
        });
    },
    /**
     * 查看流程图
     */
    showImg: function () {
    	var processInstanceId = $("#processInstanceId").val()
        var index = layer.open({
            title: '流程图',
            maxmin: false,
            type: 1,
            content: '<img src="/act/actFixedForm/showImgfromproInsId?processInstanceId=' + processInstanceId + '" />'
        });
        pform.render();
        layer.full(index);
    },
    /**
     * 撤回
     * @param selector 按钮
     * @param _csrf_   token
     */
    revoke2Task: function (selector) {
    	pform.on('submit(' + selector + ')', function (data) {
	    	var processInstanceId = $("#processInstanceId").val()
	        layer.confirm('确定撤回吗 ?', {
	            icon: 3,
	            title: '提示'
	        }, function (index) {
	            var data = {
	            	processInstanceId: processInstanceId,
	                _csrf: _csrf
	            }
//	            tool.post("/ioa/ioaReceiveFile/revoke2Task", data, postCallBackPubInfo, false)
	            tool.post("/act/actFixedForm/jump2Task2", data, postCallBackPubInfo, false)
	        });
    	})
    },
    /**
     * 审批 完成任务
     * @param selector 按钮
     * @param url       完成任务的url(后台controller)
     * @param processInstanceId   流程实例ID
     * @param _csrf_   token
     * @param bizId    业务表主键ID
     */
    completeTask: function () {
        pform.on('submit(form-add-submit)', function (data) {
            var message = $("#draw_sug").val();
            var processInstanceId = $("#processInstanceId").val();
            var _csrf = $("#_csrf").val();
            var receiveFileId = $("#receiveFileId-director").val() == "" ? $("#receiveFileId-toread").val() : $("#receiveFileId-director").val();
            var readUserIds = $("#n_receiverNameid").val();
            var isEnd = $("#isEnd").val();
            var param = {
                "processInstanceId": processInstanceId,
                "_csrf": _csrf,
                "receiveFileId": receiveFileId,
                "readUserIds": readUserIds,
                "isEnd": isEnd,
                "message": message
            };
            if(isEnd != "1"){
            	if(readUserIds ==""){
            		layer.msg("请选择下一级审批处理人");
                	return false;
                }
            }
            if($("#draw_sug").val()==""){
            	layer.confirm('意见为空，确定提交？', {
            		  icon : 3,
        			  btn: ['确定','取消'] //按钮
        			}, function(){
        				tool.post("/ioa/ioaReceiveFile/completeTask", param, postCallBackPubInfo, false);
        			}, function(){
        			  
        			});
            }else{
            	layer.confirm('确定提交？', {
            		  icon : 3,
          			  btn: ['确定','取消'] //按钮
          			}, function(){
          				tool.post("/ioa/ioaReceiveFile/completeTask", param, postCallBackPubInfo, false);
          			}, function(){
          			  
          			});
            }
            return false;
        });
    },
    /**
     * 获得上一个节点的信息
     * @param taskId  任务ID
     * @param _csrf   token
     */
    getPreTaskInfo: function () {
        var processInstanceId = $("#processInstanceId").val();
        var _csrf = $("#_csrf").val();
        var pm = {
            processInstanceId: processInstanceId,
            _csrf: _csrf
        };
        tool.post("/ioa/ioaReceiveFile/getPreTaskInfo", pm, function (retMsg) {
            $(".agree").hide();
            $(".noagree").show();
            $(".yes").removeClass("blue_border");
            $(".no").addClass("blue_border");
            if (retMsg.object != null) {
                if (retMsg.object.length > 0) {
                    var data = retMsg.object[0];
                    $("#pre-nodeName").text(data.nextNodeName);
                } else {
                    $(".noagree").attr("disabled", true);
                }
            }
        }, false);
    },
    /**
     * 回退到上一节点
     * @param selector 按钮
     * @param taskId   任务ID
     * @param _csrf    token
     */
    jump2Task2: function (selector) {
        pform.on('submit(' + selector + ')', function (data) {
        	var message = $("#draw_sug").val();
        	if(message == ""){
        		layer.msg("请输入回退意见");
        		return false;
        	}
        	var processInstanceId = $("#processInstanceId").val();
            var param = {
                "processInstanceId": processInstanceId,
                bizId: bizId,
                _csrf: $("#_csrf").val(),
                "message": message
            };
            tool.post("/ioa/ioaReceiveFile/jump2Task2", param, postCallBackPubInfo, false);
            return false;
        });
    },
    /**
     * 检查下个节点指定是 个人（多个人）、部门、岗位
     * @param url
     * @param taskId
     * @param _csrf
     */
    checkNextTaskInfo: function () {
        var processInstanceId = $("#processInstanceId").val();
        var _csrf = $("#_csrf").val();
        var tr = $("#objList").find("tr");
        var isCheckAllUser = $("#all:checked").length;
        $("#isCheckAllUser").val(isCheckAllUser);
        var receiveUserIds = '';
        $(tr).each(function(index){
            var td = $(this).find("td").eq(1);
            var readUserIds = td.find("#readUserIds"+index).val();
            if(readUserIds != ''){
                receiveUserIds += readUserIds;
            }
        });
        var pm = {
            processInstanceId: processInstanceId,
            _csrf: _csrf,
            receiveUserIds : receiveUserIds
        };
        tool.post("/ioa/ioaReceiveFile/checkNextTaskInfo", pm, function (retMsg) {
            var data = retMsg.object;
            if (null != data) {
                if (data.isNotBack) {
                    $("#no_agree_btn").hide();
                }
                if (data.isOrgn == false) {
                    $("#audit-orgn").hide();
                    $("#audit-member").show();
                    var userList = data.user;
                    if (userList != 'undefined') {
                        var str = '';
                        var count = 0;
                        var strId = ''
                        var strName = '';
                        $(userList).each(function (index) {
                            var data = userList[index];
                            str += '<a class="seitem" value="' + data.id + '">' + data.fullName + '</a>';
                            strId += data.id + ";";
                            strName += '<span style="padding-left: 5px;">'+data.fullName+'</span>';
                            count++;
                        });
                        $("#audit-member-list").empty();
                        $("#audit-member-list").append(str);
                        var isShowReadUserName = $("#isShowReadUserName").val();
                        if(isShowReadUserName != ''){
                            $("#testarea").html(strName);
                            $("#n_receiverNameid").val(strId);
                        }
                        $("#testarea").attr("userId", strId);
                    }
                } else {
                    $("#audit-orgn").show();
                    $("#audit-member").hide();
                    var deptList = data.dept;
                    if (deptList != 'undefined') {
                        var strId = '';
                        var strName = '';
                        $(deptList).each(function (index) {
                            var data = deptList[index];
                            strId += data.deptId + ";";
                            strName += data.fullName + ";";
                        });
                        $("#n_receiverName_").val(strName);
                        $("#deptId").val(strId);
                    }
                    var userList = data.user
                    if (userList != 'undefined') {
                        var uId = '';
                        $(userList).each(function (index) {
                            var data = userList[index];
                            uId += data.id + ";";
                        });
                        $("#deptUserId").val(uId);
                    }
                }
            }
        }, false);
    },
    /**
     * 获取下个节点信息
     * @param taskId
     * @param _csrf
     */
    getNextTaskInfo: function () {
        var processInstanceId = $("#processInstanceId").val();
        var _csrf = $("#_csrf").val();
        var pm = {
            processInstanceId: processInstanceId,
            _csrf: _csrf
        };
        tool.post("/ioa/ioaReceiveFile/getNextTaskInfo", pm, function (retMsg) {
            var nextNodeName = "";
            if (null != retMsg && null != retMsg.object) {
                if (retMsg.object.length == 0) {
                    nextNodeName = "结束";
                    $("#audit-orgn").hide();
                    $("#audit-member").hide();
                    $("#isEnd").val("1");
                    $("#no_agree_btn").hide();//传阅环节不允许回退
                } else {
                    var data = retMsg.object[0];
                    nextNodeName = data.nextNodeName;
                }
                $("#nextNode").empty();
                //$("#nextNode").append('<option value="">请选择</option>');
                $("#nextNode").append('<option value="\'+nextNodeName+\'" selected="selected" readonly="readonly">' + nextNodeName + '</option>');
                if (retMsg.code == 0) {
                    var index = layer.open({
                        title: '审批处理',
                        maxmin: false,
                        type: 1,
                        shade: false,
                        area: ['500px', '400px'],
                        shadeClose: true,
                        content: $('#win-yes-object')
                    });
                    pform.render();
                }
            }
        }, false);
    },
    /**
     * 获取日志信息
     * @param url
     * @param taskId
     * @param _csrf
     */
    getAllTaskLogInfo: function () {
        var processInstanceId = $("#processInstanceId").val();
        var _csrf = $("#_csrf").val();
        var pm = {
            processInstanceId: processInstanceId,
            _csrf: _csrf
        };
//        tool.post("/ioa/ioaReceiveFile/getAllTaskInfo", pm, function (data) {
        tool.post("/act/actFixedForm/getLog", pm, function (data) {
            var loglist = {loglist: data};
            $('#paging3-data').html(template("paging3-script", loglist));
            var index = layer.open({
                title: '日志',
                maxmin: false,
                type: 1,
                content: $('#win-daily-object')
            });
            layer.full(index);
        }, false);
    },
    
    /**
     * 待阅文件详情
     * @param bizId
     */
    readDetail: function (bizId) {
        var retMsg = tool.getById('/ioa/ioaReceiveFile/getById', bizId);
        var object = retMsg.ioaReceiveFileVO;
        var isAssignee = retMsg.isAssignee;
        if (null != object) {
            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-read').html(template("artt-read", filelist));
            }

            for (var key in object) {
                $("#read-object-form").find("#" + key + "-read").val(
                    object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-read").val(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-read").val(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-read").val(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
                if (key == 'fileType') {
                    $("#fileType-read").val(object[key]);
                }
                if (key == 'fromUnit') {
                    $("#fromUnit-read").val(object[key]);
                }
                if (key == 'urgentLevel') {
                    $("#urgentLevel-read").val(object[key]);
                }
            }

            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-read2').html(template("artt-read2", filelist));
            }
            if (object.claim == true) {
                $("#revoke-inread").hide();
            } else {
                $("#revoke-inread").show();
            }
            for (var key in object) {
                $("#read-object-form2").find("#" + key + "-read").val(
                    object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-read2").val(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-read2").val(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-read2").val(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
                if (key == 'fileType') {
                    $("#fileType-read2").val(object[key]);
                }
                if (key == 'fromUnit') {
                    $("#fromUnit-read2").val(object[key]);
                }
                if (key == 'urgentLevel') {
                    $("#urgentLevel-read2").val(object[key]);
                }
            }
        }
        var index = layer.open({
            title: '待阅文件详情',
            maxmin: false,
            type: 1,
            shadeClose: true,
            content: $('#win-readdetail-object')
        });
        pform.render();
        layer.full(index);
    },
    /**
     * 待阅文件详情
     */
    toReadDetail: function (bizId) {
        var object = tool.getById("/ioa/ioaReceiveFile/directorDetail", bizId);
        if (null != object) {
        	//文件附件
        	if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-toread').html(template("artt-toread", filelist));
            }
        	
        	//传阅对象
            if (null != object.objectDOList) {
                var obj = object.objectDOList;
                for(var i = 0;i < obj.length;i++){
                    if(obj[i].isCheck == "1"){
                        obj.pop(obj[i]);
                        $("#all-detail").attr("checked",true);
                    }
                }
                var objlist = {objlist: obj};
                $('#objList-toread').html(template("obj-script-toread", objlist));
            }

            for (var key in object) {
                $("#toRead-object-form").find("#" + key + "-toread").text(
                    object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-toread").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-toread").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-toread").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
                if (key == 'id') {
                    $("#id-toread").val(object[key]);
                    $("#receiveFileId-toread").val(object[key]);
                }
            }
        }
        var index = layer.open({
            title: '传阅文件详情',
            maxmin: false,
            type: 1,
            shadeClose: true,
            content: $('#win-toreaddetail-object')
        });
        layer.full(index);
        pform.render();
    }/*,
    inReadDetail : function (bizId) {
        var object = tool.getById('/ioa/ioaReceiveFile/getById', bizId);
        if(null != object){
            if(null != object.attachmentList){
                var fileBack=object.attachmentList;
                var filelist={filelist:fileBack};
                $('#demoList-inread').html(template("artt-inread", filelist));
            }

            for ( var key in object) {
                $("#inRead-object-form").find("#" + key+"-inread").text(
                    object[key]);
                if(key == 'orgnlFileTime'){
                    var orgnlFileTime = object[key].substr(0,10);
                    $("#orgnlFileTime-inread").text(orgnlFileTime);
                }
                if(key == 'receiveFileTime'){
                    var receiveFileTime = object[key].substr(0,10);
                    $("#receiveFileTime-inread").text(receiveFileTime);
                }
                if(key == 'handleLimitTime'){
                    var handleLimitTime = object[key].substr(0,10);
                    $("#handleLimitTime-inread").text(handleLimitTime);
                }
                if(key == 'processInstanceId'){
                    $("#processInstanceId").val(object[key]);
                }
                if(key == 'id'){
                    $("#id-inread").val(object[key]);
                }
            }
        }
        var index = layer.open({
            title : '在阅文件详情',
            maxmin : false,
            type : 1,
            shadeClose: true,
            content : $('#win-inreaddetail-object')
        });
        layer.full(index);
    }*/,
    directorAuditDetail: function (bizId) {
        var object = tool.getById("/ioa/ioaReceiveFile/directorDetail", bizId);
        $("#isShowReadUserName").val("1");
        if (null != object) {
            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-director').html(template("artt-director", filelist));
            }

            if (null != object.objectDOList) {
                var obj = object.objectDOList;
                for(var i = 0;i < obj.length;i++){
                    if(obj[i].isCheck == "1"){
                        obj.pop(obj[i]);
                        $("#all").attr("checked",true);
                    }
                }
                var objlist = {objlist: obj};
                $('#objList').html(template("obj-script", objlist));
            }

            for (var key in object) {
                $("#director-object-form").find("#" + key + "-director").text(
                    object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-director").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-director").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-director").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                    $("#processInstanceId-director").val(object[key]);
                }
                if (key == 'id') {
                    $("#receiveFileId-director").val(object[key]);
                }
            }
        }
        var index = layer.open({
            title: '池主任审批详情',
            maxmin: false,
            type: 1,
            shadeClose: true,
            content: $('#win-director-object')
        });
        layer.full(index);
        pform.render();
    },
    registerDetail: function (bizId) {
        var retMsg = tool.getById('/ioa/ioaReceiveFile/getById', bizId);
        var object = retMsg.ioaReceiveFileVO
        $("#id-inread").val(bizId);
        //默认签收
        if (null != object) {
            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-register').html(template("artt-register", filelist));
                $('#demoList-inread').html(template("artt-inread", filelist));
            }
            for (var key in object) {
                $("#register-object-form").find("#" + key + "-register").text(
                    object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-register").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-register").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-register").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }

                $("#inRead-object-form-object-form").find("#" + key + "-inread").text(object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-inread").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-inread").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-inread").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
                if (key == 'id-register') {
                    $("#id-register").val(object[key]);
                }
            }
            var isAssignee = retMsg.isAssignee;//文件是否当前用户签收
            if(!isAssignee){
            	$("#check-sub").hide()
            }
            var index = layer.open({
                title: '收文登记详情',
                maxmin: false,
                type: 1,
                shadeClose: true,
                content: $('#win-register-object')
            });
            layer.full(index);
        }
    },
    
    inReadDetail: function (bizId) {
        var object = tool.getById('/ioa/ioaReceiveFile/readDetail', bizId);
        $("#id-inread").val(bizId);
        //附件为空移除附件结构
        if(object.resResourceList == null){
        	$("#demoList-inread").parents(".table-wrap").parent(".office-opinion").remove();
        }
        //过了办公室意见节点显示“办公室拟办意见”
       if(object.readUserIds != '' && object.readUserIds != null){
    	   var minText = object.officeOpinion;
    	   if(minText == null ){
    		   minText = "";
    	   }
    	    $("#office-textareaHtml").children("div").empty();
        	$(".office-opinion.readUserIds").show();
        	var textareaHtml = "<textarea rows='5' id='officeOpinion-toread' disabled=''>"+minText+"</textarea>";
        	$("#office-textareaHtml").children("div").append(textareaHtml);
        	$("#objList-inread").parents(".office-opinion").show();
        }
        if (null != object) {
            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-inread').html(template("artt-inread", filelist));
            }
            for (var key in object) {
                $("#inRead-object-form").find("#" + key + "-inread").text(object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-inread").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-inread").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-inread").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
            }

            if (object.claim == true) {
                $("#revoke-inread").hide();
            } else {
                $("#revoke-inread").show();
            }
            //池主任审批后要有传阅对象
            if (null != object.objectDOList) {
            	$(".inread_object").show();
            	$("#read-situation").show();
                var obj = object.objectDOList;
                for(var i = 0;i < obj.length;i++){
                    if(obj[i].isCheck == "1"){
                        obj.pop(obj[i]);
                        $("#all-detail-inread").attr("checked",true);
                    }
                }
                var objlist = {objlist: obj};
                $('#objList-inread').html(template("obj-script-inread", objlist));
            }else{
            	$(".inread_object").hide();
            	$("#read-situation").hide();
            }
            var index = layer.open({
                title: '在阅文件详情',
                maxmin: false,
                type: 1,
                shadeClose: true,
                content: $('#win-inreaddetail-object'),
  			    cancel: function(){ 
  			    	location.reload();
 			  }	
            });
            layer.full(index);
        }
    },
    
    inReadHisDetail: function (bizId) {
        var object = tool.getById('/ioa/ioaReceiveFile/readHisDetail', bizId);
        //附件为空移除附件结构
        if(object.resResourceList == null){
        	$("#demoList-inread").parents(".table-wrap").parent(".office-opinion").remove();
        }
        $("#id-inread").val(bizId);
        if (null != object) {
            if (null != object.resResourceList) {
                var fileBack = object.resResourceList;
                var filelist = {filelist: fileBack};
                $('#demoList-inread').html(template("artt-inread", filelist));
            }
            for (var key in object) {
                $("#inRead-object-form").find("#" + key + "-inread").text(object[key]);
                if (key == 'orgnlFileTime') {
                    var orgnlFileTime = object[key].substr(0, 10);
                    $("#orgnlFileTime-inread").text(orgnlFileTime);
                }
                if (key == 'receiveFileTime') {
                    var receiveFileTime = object[key].substr(0, 10);
                    $("#receiveFileTime-inread").text(receiveFileTime);
                }
                if (key == 'handleLimitTime') {
                    var handleLimitTime = object[key].substr(0, 10);
                    $("#handleLimitTime-inread").text(handleLimitTime);
                }
                if (key == 'processInstanceId') {
                    $("#processInstanceId").val(object[key]);
                }
            }

            if (object.claim == true) {
                $("#revoke-inread").hide();
            } else {
                $("#revoke-inread").show();
            }
            //综合查询页面  办公室意见显示
            if(object.readUserIds != ''){
            	$("#officeOpinion-toread").text(object.officeOpinion);
            }
            //池主任审批后要有传阅对象
            if (null != object.objectDOList) {
            	$(".inread_object").show();
            	$("#read-situation").show();
                var obj = object.objectDOList;
                for(var i = 0;i < obj.length;i++){
                    if(obj[i].isCheck == "1"){
                        obj.pop(obj[i]);
                        $("#all-detail-inread").attr("checked",true);
                    }
                }
                var objlist = {objlist: obj};
                $('#objList-inread').html(template("obj-script-inread", objlist));
            }else{
            	$(".inread_object").hide();
            	$("#read-situation").hide();
            }
            var index = layer.open({
                title: '收文阅件详情',
                maxmin: false,
                type: 1,
                shadeClose: true,
                content: $('#win-inreaddetail-object')
            });
            layer.full(index);
        }
    },
    /**
     * 常用意见列表
     */
    opinionList: function () {
        var pm = {
            _csrf: _csrf
        };
        tool.post("/per/autUsefulOpinion/opinionList", pm, function (retMsg) {
            if (null != retMsg) {
                var fileBack = retMsg;
                var opinionlist = {opinionlist: fileBack};
                $('#opinion-data').html(template("opinion-script", opinionlist));
            }
        }, false);
    },
    /**
     * 加入常用意见
     */
    addOpinion: function () {
    	if($("#draw_sug").val() ==""){
    		layer.msg("请加入有内容的意见")
    		return false;
    	}
    	var pm = {
            _csrf: _csrf,
            content: $("#draw_sug").val()
        };
        tool.post("/per/autUsefulOpinion/add", pm, function (retMsg) {
            if (retMsg.code == 0) {
            	layer.msg(retMsg.message, {
                    icon: 1,
                    time: 1500
                }, function (index) {
                    layer.close(index);
                    readReceipt.opinionList();
                });
            } else {
                tool.error(retMsg.message);
            }
        }, false);
    },
    voidProcess: function () {
        layer.confirm('确定删除吗 ?', {
            icon: 3,
            title: '提示'
        }, function (index) {
            var param = {
                processInstanceId: $("#processInstanceId").val(),
                bizId: $("#bizId").val(),
                _csrf: $("#_csrf").val()
            };
            tool.post("/ioa/ioaReceiveFile/void", param, function (retMsg) {
                if (retMsg.code == 0) {
                	layer.msg(retMsg.message, {
                        icon: 1,
                        time: 1500
                    }, function (index) {
                        parent.layer.closeAll();
                        loadToReadData();
                        loadInReadData();
                    });
                } else {
                    tool.error(retMsg.message);
                }
            }, false);
        });
    },
   
    /**
     * 打印
     */
    print : function () {
    	$("form").css("display","none");
    	$("#inRead-object-form").find("#btns").css("display","none");
    	$("#inRead-object-form").show();
        window.print();
        $("form").css("display","block");
        $("#inRead-object-form").find("#btns").css("display","block");
        return false;
    }
    
}

/**
 * 回调函数
 * @param retMsg
 */
function postCallBackPubInfo(retMsg) {
    if (retMsg.code == 0) {
    	layer.msg(retMsg.message, {
            icon: 1,
            time: 1500
        }, function () {
            location.href = "/ioa/ioaReceiveWork/ioaReceiveWorkPage";
            layer.closeAll();
        });
    } else {
        tool.error(retMsg.message);
    }
}

