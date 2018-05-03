/**
 * 固定表单流程审批工具类
 * 
 * @date: 2017/11/07
 * @author: wangj
 */
var flag = true;
var fixed = {
	/**
	 * 检查当前任务是否被签收
	 * 
	 * @param taskId
	 *            任务ID
	 * @param _csrf
	 *            token
	 */
	checkClaim : function(taskId, _csrf) {
		var pm = {};
		if (taskId == '') {
			var processInstanceId = $("#processInstanceId").val();
			pm.processInstanceId = processInstanceId;
		} else {
			pm.taskId = taskId;
		}
		pm._csrf = _csrf;
		tool.post("/act/actFixedForm/isClaim", pm, function(retMsg) {
			// 待办：[任务未签收：打印/查看流程/流程日志/签收][任务被签收：提交/作废/打印/查看流程/流程日志]
			// 在办：[任务未被签收：打印/查看流程/流程日志/撤回][任务被签收：打印/查看流程/流程日志]
			$(".btns").show();
			if (retMsg.code == 0) {// 待办详情 流程已经被签收
				$("#sign").hide();
				$("#revoke").hide();
				$(".act").show();
			} else if (retMsg.code == 1) {// 待办详情 流程未被签收
				$("#sign").show();
				$("#revoke").hide();
				$(".act").hide();
			} else if (retMsg.code == 2) {// 在办详情 流程已被签收
				$("#sign").hide();
				$("#revoke").hide();
				$(".act").hide();
			} else if (retMsg.code == 3) {// 在办详情 非上一环节办理人
				$("#sign").hide();
				$("#revoke").hide();
				$(".act").hide();
			} else if (retMsg.code == 4) {// 在办详情 流程未被签收并且是属于自己办理的 可撤回
				$("#sign").hide();
				$("#revoke").show();
				$(".act").hide();
			} else {
				$("#sign").hide();
				$("#revoke").hide();
				$(".act").hide();
			}
			// 如果是从流转监控打开的 隐藏撤回按钮
			if ($("#isMonitor").val() == 1) {
				$("#revoke").hide();
			}
		}, false);
	},
	/**
	 * 签收任务
	 * 
	 * @param taskId
	 *            任务ID
	 * @param _csrf
	 *            token
	 */
	claimTask : function(taskId, _csrf) {
		/*layer.confirm('确定签收吗 ?', {
			icon : 3,
			title : '提示'
		}, function(index) {*/
			var data = {
				ids : taskId,
				_csrf : _csrf
			}
			tool.post("/ioa/ioaWaitWork/claimTask", data, function(retMsg) {
				if (retMsg.code == 0) {
					//tool.success(retMsg.message);
					layer.msg(retMsg.message,{icon:1,time: 1500 })
					$(".act").show();
					$("#sign").hide();
				} else {
					tool.error(retMsg.message);
				}
			}, false)
		/*});*/
	},
	/**
	 * 查看流程图
	 */
	showImg : function() {
		var processInstanceId = $("#processInstanceId").val()
		var imgUrl = ""
		if (taskId == null || taskId == "") {
    		imgUrl = '<img src="/act/actFixedForm/showImgfromproInsId?processInstanceId=' + processInstanceId + '" />'
		} else {
    		imgUrl = '<img src="/act/actFixedForm/showImg?taskId='+taskId+'" />'
		}

		var index = layer.open({
			title : '流程图',
			maxmin : false,
			type : 1,
			content : imgUrl
		});
		pform.render();
		layer.full(index);
	},
	/**
	 * 查看完结的流程图
	 */
	showHisImg : function(processInstanceId) {
		var index = layer
				.open({
					title : '流程图',
					maxmin : false,
					type : 1,
            content : '<img src="/act/actFixedForm/showHisImg?processInstanceId='+processInstanceId+'" />'
				});
		pform.render();
		layer.full(index);
	},
	/**
	 * 审批 完成任务
	 * 
	 * @param selector
	 *            按钮
	 * @param url
	 *            完成任务的url(后台controller)
	 * @param taskId
	 *            任务ID
	 * @param _csrf_
	 *            token
	 * @param bizId
	 *            业务表主键ID
	 */
	completeTask : function(selector, url, taskId, _csrf, bizId,nextStept,elementIdStr) {
		//在线弹窗进入构造title
		var iframeStry = $("iframe");
		var iframeUrlStry = iframeStry.context.URL;
		var titleTxIs = iframeUrlStry.indexOf("titleTx");
		if(titleTxIs != -1){
			var titleMinStr = iframeUrlStry.substring(iframeUrlStry.indexOf("titleTx")+8,iframeUrlStry.length);
			var BreakLastIs = titleMinStr.indexOf("&");
			if(BreakLastIs != -1){
				titleMinStr = iframeUrlStry.substring(0,BreakLastIs);
			}
			titleMinStr = decodeURIComponent(titleMinStr);
		    var childSpan = "<span class='layui-layer-setwin'><a class='layui-layer-ico layui-layer-close layui-layer-close1' href='javascript:;' onclick='offTitleTop()'></a></span>";
		    var titleDiv = "<div class='layui-layer-title' style='cursor: move;margin-bottom: 5px;'>"+titleMinStr + childSpan+"</div>";
	    	$("#"+elementIdStr).find("#btns").prepend(titleDiv);
	    	$("#"+elementIdStr).css("padding-top","80px");
	    	//公共信息样式调整
	    	$("#win-pubmsg-object").css("padding-top","40px");
		}
		//在线弹窗进入构造title end
		pform.on('submit(' + selector + ')', function(data) {
			var userId = "";
			var tempUserId = $("#testarea").attr("userId");
			var receiverId = $("#n_receiverNameid").val();
			if (tempUserId) {
				userId = tempUserId;
			}
			if (receiverId) {
				userId = receiverId;
			}
			var isnotend = $("#isNotEnd").val();// 值为0是结束节点
			if (isnotend != "0") {
				if (userId == "undefined" || userId == "") {
					layer.msg("请选择下一级审批处理人");
					return false;
				}
			}
			var param = {
				"taskId" : taskId,
				_csrf : _csrf,
				"bizId" : bizId,
				message : $("#draw_sug").val(),
				userId : userId
			};
			if (nextStept != 'undefined' || nextStept != '') {
				nextStept = $("#nextStept").val();
				param.nextStept = nextStept
			}
			if ($("#draw_sug").val() == "") {
				layer.confirm('意见为空，确定提交？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					if(flag == true){
						flag = false;
						tool.post(url, param, postCallBackPubInfo, false);
					}
					//tool.post(url, param, postCallBackPubInfo, false);
				}, function() {

				});
			} else {
				layer.confirm('确定提交？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					//tool.post(url, param, postCallBackPubInfo, false);
					if(flag == true){
						flag = false;
						tool.post(url, param, postCallBackPubInfo, false);
					}
				}, function() {

				});
			}
			return false;
		});
	},
	/**
	 * 填写意见
	 * 
	 * @param selector
	 *            按钮
	 * @param taskId
	 *            任务ID
	 * @param _csrf_
	 *            token
	 * @param message
	 *            意见
	 */
	addComment : function(selector, taskId, _csrf, message) {
		pform.on('submit(' + selector + ')', function(data) {
			var param = {
				"taskId" : taskId,
				_csrf : _csrf,
				"message" : message
			};
			tool.post("/act/actFixedForm/addComment", param,
					postCallBackPubInfo, false);
			return false;
		});
	},
	/**
	 * 获得上一个节点的信息(虽然他写在公共信息模块，但是全世界都是调这一个)
	 * 
	 * @param taskId
	 *            任务ID
	 * @param _csrf
	 *            token
	 */
	getPreTaskInfo : function(taskId, _csrf) {
		var url = "/act/actFixedForm/getPreTaskInfo4fix";
		var pm = {
			taskId : taskId,
			_csrf : _csrf
		};
		tool.post(url, pm, function(retMsg) {
			$(".agree").hide();
			$(".noagree").show();
			$(".yes").removeClass("blue_border");
			$(".no").addClass("blue_border");
			if(retMsg.code == 0){
				if (retMsg.object != null) {
					if (retMsg.object.length > 0) {
						var data = retMsg.object[0];
						var text = "";
						if (null != data.assigneeName) {
							text = data.assigneeName;
						}
						if (null != data.deptName) {
							text = data.deptName;
						}
						var operator = ""
						if (text != "") {
							operator = " [ 办理人：" + text + " ]"
						}
						$("#pre-nodeName").text(data.nextNodeName + operator);
					} else {
						$(".noagree").attr("disabled", true);
					}
				}
			}else{
				tool.error(retMsg.message);
			}
		}, false);
	},
	/**
	 * 回退到上一节点
	 * 
	 * @param selector
	 *            按钮
	 * @param taskId
	 *            任务ID
	 * @param _csrf
	 *            token
	 */
	jump2Task2 : function(selector, url, taskId, bizId, _csrf) {
		pform.on('submit(' + selector + ')', function(data) {
			var message = $("#draw_sug").val();
			/*
			 * if(message == "" || message =="undefined"){ layer.msg("请填写回退意见");
			 * return false; }
			 */
			var param = {
				"taskId" : taskId,
				bizId : bizId,
				message : message,
				_csrf : _csrf
			};
			if ($("#draw_sug").val() == "") {
				layer.confirm('意见为空，确定退回？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					//tool.post(url, param, postCallBackPubInfo, false);
					if(flag == true){
						flag = false;
						tool.post(url, param, postCallBackPubInfo, false);
					}
				}, function() {

				});
			} else {
				layer.confirm('确定退回？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					//tool.post(url, param, postCallBackPubInfo, false);
					if(flag == true){
						flag = false;
						tool.post(url, param, postCallBackPubInfo, false);
					}
				}, function() {

				});
			}
			return false;
		});
	},
	/**
	 * 检查下个节点指定是 个人（多个人）、部门、岗位
	 * 
	 * @param url
	 * @param taskId
	 * @param _csrf
	 */
	checkNextTaskInfo : function(url, taskId, _csrf, nextStept) {
		var pm = {
			taskId : taskId,
			_csrf : _csrf,
			nextSteps : nextStept
		};
		tool.post(url, pm, function(retMsg) {
			var data = retMsg.object;
			if (null != data) {
				if (data.isNotBack) {
					$("#no_agree_btn").hide()
				}
				var openType = data.openType;
				$("#openType").val(openType);
				if (data.isOrgn == false) {
					$("#audit-orgn").hide();
					$("#audit-member").show();
					var userList = data.user;
					if (userList != 'undefined') {
						var str = '';
						var count = 0;
						var strId = ''
						var strName = '';
						$(userList).each(
							function(index) {
								var data = userList[index];
								str += '<a class="seitem" value="'
										+ data.id + '">' + data.fullName
										+ '</a>';
								strId += data.id + ";";
								strName += data.fullName + ";";
								count++;
							});
						$("#audit-member-list").empty();
						$("#audit-member-list").append(str);

						/*
						 * $("#testarea").text(strName);
						 * $("#testarea").attr("userId",strId);
						 */
					}
				} else {
					$("#audit-orgn").show();
					$("#audit-member").hide();
					var deptList = data.dept;
					var deptIds = data.assineedGroupIdList;
					var userList = data.user
					if (userList != 'undefined') {
						var uId = '';
						$(userList).each(function(index) {
							var data = userList[index];
							uId += data.id + ";";
						});
						$("#deptUserId").val(uId);
					}
					if (deptIds != 'undefined') {
						var strIds = "";
						$(deptIds).each(function(index) {
							var data = deptIds[index];
							strIds += data + ";";
						});
						$("#deptId").val(strIds);
						return;
					}
					if (deptList != 'undefined') {
						var strId = '';
						var strName = '';
						$(deptList).each(function(index) {
							var data = deptList[index];
							strId += data.deptId + ";";
							strName += data.fullName + ";";
						});
						// $("#n_receiverNameid").val(strId);
						$("#n_receiverName_").val(strName);
						$("#deptId").val(strId);
					}
				}
			}
		}, false);
	},
	/**
	 * 获取下个节点信息
	 * 
	 * @param taskId
	 * @param _csrf
	 */
	getNextTaskInfo : function(taskId, _csrf) {
		var pm = {
			taskId : taskId,
			_csrf : _csrf
		};
		tool.post("/act/actFixedForm/getNextTaskInfo", pm, function(retMsg) {
			console.log(retMsg);
			var nextNodeName = "";
			if (retMsg.object.length == 0) {
				nextNodeName = "结束";
				$("#audit-orgn").hide();
				$("#audit-member").hide();
				$("#isNotEnd").val("0");// 值为0是结束节点
			} else {
				var data = retMsg.object[0];
				nextNodeName = data.nextNodeName;
			}
			$("#nextNode").empty();
			console.log(nextNodeName)
			// $("#nextNode").append('<option value="">请选择</option>');
			$("#nextNode").append('<option value="\'+nextNodeName+\'" selected="selected" readonly="readonly">'+ nextNodeName + '</option>');
			if (retMsg.code == 0) {
				setTimeout(function(){
					var index = layer.open({
						title : '审批处理',
						maxmin : false,
						type : 1,
						area : [ '900px', '510px' ],
						shadeClose : true,
						content : $('#win-suggest-object')
					});
					setTimeout(function(){
						pform.render();
					},200)
				},200)
			}
		}, false);
	},
	/**
	 * 获取日志信息
	 * 
	 * @param url
	 * @param taskId
	 * @param _csrf
	 */
	getAllTaskLogInfo : function(url, taskId, _csrf) {
		url = "/act/actFixedForm/getLog"
		var processInstanceId = $("#processInstanceId").val();
		var pm = {
			taskId : taskId,
			_csrf : _csrf,
			processInstanceId : processInstanceId
		};
		tool.post(url, pm, function(data) {
			// console.log(data)
			var loglist = {
				loglist : data
			};
			$('#paging2-data').html(template("paging2-script", loglist));
			var index = layer.open({
				title : '流转日志',
				maxmin : false,
				type : 1,
				content : $('#win-daily-object')
			});
			layer.full(index);
		}, false);
	},
	/**
	 * 流程作废
	 * 
	 * @param url
	 * @param taskId
	 * @param bizId
	 * @param _csrf
	 */
	voidProcess : function(url, taskId, bizId, _csrf) {
		layer.confirm('确定作废该流程吗 ?', {
			icon : 3,
			title : '提示'
		}, function(index) {
			var param = {
				taskId : taskId,
				bizId : bizId,
				_csrf : _csrf
			};
			tool.post(url, param, function(retMsg) {
				if (retMsg.code == 0) {
					layer.msg(retMsg.message, {
						icon : 1,
						time: 1500 //2秒关闭（如果不配置，默认是3秒）
					}, function(index) {
						if(parent.attSignIndex=="isAtt"){
							parent.calUtil.eventName="search";
							parent.calUtil.id="search";
							parent.calUtil.init();
						}else if(parent.parentIndex=="isme"){
							parent.loadData_workMR();
						}
						parent.layer.closeAll();
						parent.loadData();
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
	print : function(printIdStr,title) {
		if(title == 1){
			var titleVal = $("#"+printIdStr).find("#title").val();
			$(document).attr("title",titleVal);
		}
    	$("form").css("display","none");
    	$("#btns").css("display","none");
    	$("#"+printIdStr).show();
        window.print();
        $("form").css("display","block");
        $("#btns").css("display","block");
		if(title == 1){
			$(document).attr("title","入库报溢,入库报损");
		}
         return false;
	},
	/**
	 * 常用意见列表
	 */
	opinionList : function() {
		var pm = {
			_csrf : _csrf
		};
		tool.post("/per/autUsefulOpinion/opinionList", pm, function(retMsg) {
			if (null != retMsg) {
				var fileBack = retMsg;
				var opinionlist = {
					opinionlist : fileBack
				};
				$('#opinion-data')
						.html(template("opinion-script", opinionlist));
			}
		}, false);
	},
	/**
	 * 加入常用意见
	 */
	addOpinion : function() {
		if ($("#draw_sug").val() == "") {
			layer.error("请加入有内容的意见")
			return false;
		}

		var pm = {
			_csrf : _csrf,
			content : $("#draw_sug").val()
		};
		tool.post("/per/autUsefulOpinion/add", pm, function(retMsg) {
			if (retMsg.code == 0) {
				layer.msg(retMsg.message, {
					icon : 1,
					time: 1500 //2秒关闭（如果不配置，默认是3秒）
				}, function(index) {
					fixed.opinionList();
					layer.close(index);
				});
			} else {
				tool.error(retMsg.message);
			}
		}, false);
	},
	hideOrShowBtn : function() {
		var processInstanceId = $("#processInstanceId").val();
		if (processInstanceId != '') {
			$("#sign").hide();
			fixed.checkDoingClaim();
		} else {
			$("#sign").show();
			$("#revoke").hide();
		}
	},
	/**
	 * 撤回
	 */
	revokeProcess : function() {
		layer.confirm('确定将该任务撤回？', {
			icon : 3,
			// 按钮
			btn : [ '确定', '取消' ]
		}, function() {
			var processInstanceId = $("#processInstanceId").val();
			var param = {
				"processInstanceId" : processInstanceId,
				_csrf : _csrf
			};
			tool.post("/act/actFixedForm/jump2Task2", param,postCallBackPubInfo, false);
		}, function() {

		});
	}
}

// 撤回
$("#revoke").on('click', function() {
	fixed.revokeProcess();
});

/**
 * 回调函数
 * 
 * @param retMsg
 */
function postCallBackPubInfo(retMsg) {
	if (retMsg.code == 0) {
		layer.msg(retMsg.message,{
			  icon: 1,
			  time: 1500 //2秒关闭（如果不配置，默认是3秒）
			},function(index) {
			//月报页面单独判断
			if(parent.parentIndex=="isme"){
				parent.loadData_workMR();
			}	
			//在线弹窗进入
			var iframeStry = $("iframe");
			var iframeUrlStry = iframeStry.context.URL;
			var titleTxIs = iframeUrlStry.indexOf("titleTx");
			var isIndexLog = iframeUrlStry.indexOf("isIndexLog");
			var isIndexLogStr = "";
			if(isIndexLog != -1){
				//截取isIndexLog的值为“0”标签页进入
				isIndexLogStr = iframeUrlStry.substring(isIndexLog+11,isIndexLog+12);
				
			}
			//是否从标签页进入
			var bizId = $("#bizId").val();
			if(bizId != ""){
				if(isIndexLogStr != "0"){
					$(".layui-nav-tree", window.parent.parent.document).find("li").removeClass("layui-nav-itemed");
					$("a[data-url='/tempMenu/indexPage']",window.parent.parent.document).parent().trigger("click");	
				}else{
					parent.layer.closeAll();
					parent.loadData();
				}
			}else{
				parent.layer.closeAll();
				parent.loadData();
			}
/*			if(titleTxIs != -1){
				$(".layui-nav-tree", window.parent.parent.document).find("li").removeClass("layui-nav-itemed");
				$("a[data-url='/tempMenu/indexPage']",window.parent.parent.document).parent().trigger("click");	
			}else{
				parent.layer.closeAll();
				parent.loadData();
			}*/
			//parent.location.reload();
		});
	} else {
		flag = true;
		tool.error(retMsg.message);
	}
}
// 计算文本域数字
function countnum(par) {
	var min = 0;
	if (par.value.length > min) {
		var str = min + par.value.length;
		document.getElementById("count").innerHTML = str.toString();
	}
}
//动态关闭iframe
function offTitleTop(){
	 $(".layui-nav-tree", window.parent.parent.document).find("li").removeClass("layui-nav-itemed");
	 $("a[data-url='/tempMenu/indexPage']",window.parent.parent.document).parent().trigger("click");
}
