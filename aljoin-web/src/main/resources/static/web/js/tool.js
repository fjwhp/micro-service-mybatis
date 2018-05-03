//人员选择弹窗
var index1;
var tool = {
		 containerTree:"",
		 treeDiv:"",
		 truediv:"",
		 showname:"",
	/**
	 * 分页加载数据
	 */
	loadpage : function(param) {
		var bottomPageSize = $("#"+param.container+"-footer").find(".layui-laypage-pagesize").val();
		if (typeof(bottomPageSize) != "undefined") {
			param.pageSize = bottomPageSize;
		}
		var initLoadIndex = layer.load(2, {
			time : 1 * 300
		});
		param._csrf = $("#_csrf").val();
		param.pageNum = 1;
		var initPageData;
		tool.post(param.url, param, function(initPageData) {
			//console.log("初始化："+initPageData);
			layui.laypage({
				cont : param.container + "-footer",// 分页容器id
				total : initPageData.total,// 总记录数
				pages : initPageData.pages,// 总页数
				pageSize : param.pageSize,// 每页记录数
				skip : true,// 是否显示跳转页
				url : param.url,// 数据请求接口
				jump : function(obj, first) {
					// 得到了当前页，用于向服务端请求对应数据
					if (first) {
						// 关闭初次loading
						layer.close(initLoadIndex);
						tool.render(param, initPageData)
					} else {
						param.pageNum = obj.curr;
						var pageLoadIndex = layer.load(2, {
							time : 1 * 300
						});
						tool.post(param.url, param, function(pageData) {
							//console.log("非初始化："+pageData);
							layer.close(pageLoadIndex);
							tool.render(param, pageData)
						}, true);
					}

				}
			});
		}, true);
	},
	/**
	 * 渲染表格
	 */
	render : function(param, pageData) {
		//console.log(pageData)
		//document.getElementById(param.container + "-data").innerHTML = template(param.container + "-script", pageData);
		$("#"+param.container + "-data").html(template(param.container + "-script", pageData));
		if(pageData.records.length == 0){
			//如果没有数据，合并显示一行"暂无数据"
			//列头数
			var thCount = $("#"+param.container+"-data").prev().find("th").length;
			var thCountHidden = $("#"+param.container+"-data").prev().find("th:hidden").length;
			var thCountShow = thCount - thCountHidden;
			var blankTrTd = "<tr><td style=\"text-align:center;font-size:14px;\" colspan=\""+thCountShow+"\">暂无数据</td></tr>";
			//document.getElementById(param.container + "-data").innerHTML = blankTrTd;
			$("#"+param.container + "-data").html(blankTrTd);
		}
		// 重新加载元素样式
		layui.use('form', function() {
			var form = layui.form();
			form.on('checkbox(' + param.container + '_check-all)', function(data) {
				//console.log(data)
				var checkElement = $(data.elem);
				var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]');
				child.each(function(index, item) {
					item.checked = data.elem.checked;
				});
				form.render('checkbox');
			});
			form.render();
		});
		$("#"+param.container+"-footer").find(".layui-laypage-pagesize").val(param.pageSize);
		$("#"+param.container+"-footer").find(".layui-laypage-pagesize").change(function(){
			tool.loadpage(param);
		});
		  
	},
	/**
	 * post请求
	 */
	post : function(url, data, callBack, async) {
		$.ajax({
			/* 发送请求的地址 */
			url : url,
			/* 默认为true异步，如果设置为false则同步 */
			async : async,
			/* 请求类型 */
			type : "post",
			/* 服务器返回的数据类型 */
			dataType : "json",
			/* 发送到服务器的数据，键值对 */
			data : data,
			// 回调函数
			success : function(httpResponse) {
				callBack(httpResponse);
			}
		});
	},
	/**
	 * form submit请求
	 */
	submit : function(formId, callBack) {
		$("#" + formId).ajaxSubmit({
			url : $("#" + formId).attr("action"),
			type : "post",
			complete : function(params, status, xhr) {
				if (params.status == 200) {
					callBack(JSON.parse(params.responseText));
				}
			}
		});
	},
	/**
	 * 成功提示
	 */
	success : function(message) {
		layer.alert(message, {
			title : "操作提示",
			icon : 1,
			closeBtn:0
		}, function(index) {
			layer.close(index);
		});
	},
	/**
	 * 失败提示
	 */
	error : function(message) {
		layer.alert(message, {
			title : "操作提示",
			icon : 2,
			clsoeBtn:0
		}, function(index) {
			layer.close(index);
		});
	},
	/**
	 * 根据ID获取对象
	 */
	getById : function(url, id) {
		var obj;
		var param = new Object();
		param._csrf = $("#_csrf").val();
		param.id = id;
		tool.post(url, param, function(o) {
			obj = o;
		}, false);
		return obj;
	},
	/**
	 * 根据ID删除对象
	 */
	deleteById : function(url, id) {
		var obj;
		var param = new Object();
		param._csrf = $("#_csrf").val();
		param.id = id;
		tool.post(url, param, function(o) {
			obj = o;
		}, false);
		return obj;
	},
	/**
	 * 根据父id获取子列表
	 */
	selectLinkTagByPid : function(elementId,topElementId,data){
		//console.log(data)
		var param = new Object();
		var thisdata=data.elem;
		//$("#"+topElementId).find("#"+elementId);
		//获取选中的控件的该属性值
		param.table = $("#"+elementId).attr("aljoin-table");
		param.parentId = data.value;
		param.key = $("#"+elementId).attr("aljoin-key");
		param.text = $("#"+elementId).attr("aljoin-text");
		param.id = $("#"+elementId).attr("aljoin-id");
		param.name = $("#"+elementId).attr("aljoin-name");
		param.def = $("#"+elementId).attr("aljoin-def");
		param.sclass = $("#"+elementId).attr("aljoin-sclass");
		param.layVerify = $("#"+elementId).attr("aljoin-layVerify");
		param.where = $("#"+elementId).attr("aljoin-where");
		param.level = $("#"+elementId).attr("aljoin-level");
		param._csrf = $("#_csrf").val();
		tool.post("/pub/public/getListByParentId", param, function(o) {
			$(thisdata).parent().nextAll(".nextselects").remove();
			if(o.message!=""){
				var str='<div class="layui-input-inline  nextselects">'+o.message+'</div>'
				$(str).insertAfter($(thisdata).parent());
				$(".nextselects").find("select").removeAttr("lay-verify")
				layui.use('form', function() {
					var form = layui.form();
					    form.render();
				});
			}
		}, false);
	},
	timeset:function(startTime,endTime){
		 var start = {
			elem : startTime,
			choose : function(datas) {
				end.min = datas; //开始日选好后，重置结束日的最小日期
				end.start = datas //将结束日的初始值设定为开始日
				$(startTime).focus()
			}
			};
		var end = {
			elem :endTime,
			choose : function(datas) {
				start.max = datas; //结束日选好后，重置开始日的最大日期
				$(endTime).focus()
			}
		};
		laydate(start);
		laydate(end);
	},
	 dateTime:function(startTime1,endTime1,format,istime){
	    	var start = {
		 		      elem: startTime1,
		 		      format:format,
		 		      istime: istime,
		 		      istoday: true,
		 		      choose: function (datas) {
		 		        end.min = datas; //开始日选好后，重置结束日的最小日期
		 		        end.start = datas //将结束日的初始值设定为开始日
		 		      }
		 		    };
		 		    var end = {
		 		      elem: endTime1,
		 		      format: format,
		 		      istime: istime, //是否开启时间选择
		 		      istoday: true,
		 		      choose: function (datas) {
		 		        start.max = datas; //结束日选好后，重置开始日的最大日期
		 		      }
		 		    };
			 	$(startTime1).click(function(){
			 		var value=$(endTime1).val();
			 		if(value==""||value==null){
			 			delete start.max; 
			 			
			 		}else{
			 			start.max=value;
			 		}
			 		 laydate(start);
			 	})
			 	$(endTime1).click(function(){
			 		var value=$(startTime1).val();
			 		if(value==""||value==null){
			 			delete end.min; 
			 			delete end.start; 
			 			
			 		}else{
			 			 end.min=value; 
			 			 end.start=value; 
			 		}
			 		 laydate(end);
			 	})
	    },
	timeset1:function(Time){
		var start = {
		elem : Time,};
		laydate(start);
	},
	linkJump:function (href1,href2,icon,title){
		parent.tab.deleteTab(parent.tab.getCurrentTabId());
		//跳转到协同办公--待办工作
		parent.tab.tabAdd({
		  	href : href1,
		  	icon : icon,
		  	title : title
		});			
		$("a[data-url='"+href2+"']",window.parent.document).parent().removeClass("layui-this");
		$("a[data-url='"+href1+"']",window.parent.document).parent().addClass("layui-this");
		$("a[data-url='"+href1+"']",window.parent.document).parent().parent().parent().addClass("layui-nav-itemed");
	},
	linkJump3:function(href1,href2){
		var a=href1.substr(0,1);
		if(a!=="/"){
			location.href="/"+href1;
		}else{
			location.href=href1;
		}
		$("a[data-url='"+href2+"']",window.parent.document).parent().removeClass("layui-this");
		$("a[data-url='"+href1+"']",window.parent.document).parent().addClass("layui-this");
		$("a[data-url='"+href1+"']",window.parent.document).parent().parent().parent().addClass("layui-nav-itemed");
		
	},
	//人员选择树
	treeDemo:function(containerTree,treeDiv,truediv,showname,showname_html){
		 $("#peopelSel").val("")
		 tool.containerTree=containerTree;
		 tool.treeDiv=treeDiv;
		 tool.truediv=truediv;
		 tool.showname=showname;
		 tool.showname_html=showname_html;
		  index1 = layer.open({
	          title : '人员选择',
	          maxmin : false,
	          type : 1,
	          shadeClose: true,
	          area: ['360px', '500px'],
	          content : $('#'+containerTree)
	      });
	     /*  layer.full(index); */
	      tool.deptUserTree(treeDiv,truediv,showname,showname_html);
	      var objIds = $("#"+truediv).val();
	      //console.log(objIds)

	      var ids = [];
	      if(objIds.length>0 && objIds.lastIndexOf(";")>-1){
	          ids = objIds.substr(0,objIds.lastIndexOf(";")).split(";");
			}
	      if(ids.length>0){
	          var mytree = $.fn.zTree.getZTreeObj(treeDiv);
	          //打开弹窗前重置节点以及关闭展开状态，避免叠加
	          mytree.checkAllNodes(false)
	          mytree.expandAll(false);
	          for(var i=0;i<ids.length;i++){
	              var nodes= mytree.getNodeByParam("userId",ids[i]);
	              if(nodes){
	            	  nodes.checked=true;
					  mytree.selectNode(nodes,true);//指定选中ID的节点  
					  mytree.updateNode(nodes)
					  mytree.expandNode(nodes, true, false);//指定选中ID节点展开 
	              }
	          }
	      }
	},
	deptUserTree:function(treeDiv,truediv,showname,showname_html){
		//console.log(treeDiv,truediv,showname)
		tool.post("/pub/public/organList",{_csrf:$("#_csrf").val()},function(data){
	        var  departmentList=data.departmentList;//部门
	        var  userPositionList=data.departmentUserList;//用户
	        var setting = {
	            check : {
	                enable : true ,
	            },
	            data : { simpleData : { enable : true }  },
	            callback: {
	                onCheck: onCheck
	            }
	        };
	        var obj=[];
	        for(var i=0;i<departmentList.length;i++){
	            obj.push({id:departmentList[i].id,pId: departmentList[i].parentId,name:departmentList[i].deptName,necessary:0})
	            for(var j=0;j<userPositionList.length;j++){
	                if(userPositionList[j].deptId==departmentList[i].id){
	                    obj.push({id:userPositionList[j].id,pId:departmentList[i].id,name:userPositionList[j].fullName,necessary:1,userId:userPositionList[j].userId,userName:userPositionList[j].userName })
	                }
	            }
	        }
	        mytree= $.fn.zTree.init($("#"+treeDiv), setting, obj);
	      
	    },false)
	},
	getNowDate:function(){
		 var arr=[];
		 var date = new Date();
		 showYear=date.getFullYear();
		 showMonth=date.getMonth() + 1;
		 showday = date.getDate();    
		 if(showMonth<10){
	    	 showMonth="0"+showMonth;
	     }
		 if(showday<10){
			 showday="0"+showday;
	     }
		 var t1 = date.getHours();
		 var t2 = date.getMinutes();
		 var t3 = date.getSeconds();
		 if(t1 < 10){
			 t1 = "0"+t1;
		 }
		 if(t2 < 10){
			 t2 = "0"+t2;	 
		 }
		 if(t3 < 10){
			 t3 = "0"+t3;
		 }
	     
		 var time = t1 + ":" + t2 + ":" + t3;
		 var Dates = showYear+"-"+showMonth+"-"+showday;
		 var Dates_CN = showYear+"年"+showMonth+"月"+showday+"日";
		 arr.push(time);
		 arr.push(Dates);
		 arr.push(Dates_CN);
		 
		 var week = date.getDay();  
		 var str="";
		 if (week == 0) {  
		         str = "星期日";  
		 } else if (week == 1) {  
		         str = "星期一";  
		 } else if (week == 2) {  
		         str = "星期二";  
		 } else if (week == 3) {  
		         str = "星期三";  
		 } else if (week == 4) {  
		         str = "星期四";  
		 } else if (week == 5) {  
		         str = "星期五";  
		 } else if (week == 6) {  
		         str = "星期六";  
		 }
		 arr.push(str);
		 return arr;
		 
	}
	};
	function onCheck(){
		var  treeArr=[];
	    var zTree = $.fn.zTree.getZTreeObj(tool.treeDiv);
	    var changeNodes = zTree.getChangeCheckedNodes();
	    //数组去重
	    var res = [];
	    var json = {};
	    for(var i = 0; i < changeNodes.length; i++){
	        if(!json[changeNodes[i].userId]){
	            res.push(changeNodes[i]);
	            json[changeNodes[i].userId] = 1;
	        }
	    }
	    changeNodes=res
	    if(changeNodes.length>0){
	        $.each(changeNodes,function(index,value){
	            if(value.necessary==1){
	                treeArr.push({id:value.id,name:value.name,userId:value.userId,userName:value.userName})
	            }
	        })
	    }

	    var uId="",fullname="";
	    if(treeArr.length>0){
	        $.each(treeArr,function(index,value){
	            uId+=value.userId+";"
	            fullname+=value.name+";"
	        })
	    }else{
	        var uId="",fullname="";
	    }
	 
	  $(".sure_s").click(function(){
		  $("#"+tool.truediv).val(uId);
		  $("#"+tool.showname).val(fullname);
		  $("#"+tool.showname_html).html(fullname);
		  layer.close(index1);
	  })
	}
	$(function() {
		/**
		 * 禁用input标签回车
		 */
		$("input").on('keypress', function(e) {
			var key = window.event ? e.keyCode : e.which;
			if (key.toString() == "13") {
				return false;
			}
		});
	});

//给人员选择input绑定回车键
$(function(){ 
	$("#peopelSel").keydown(function(e) {  
	  if (e.keyCode == 13) {  
		  searchNodes(); 
	  } 
	})
	$("#searchKey").keydown(function(e) {  
	  if (e.keyCode == 13) {  
		  loadData(); 
	  } 
	})
})
//li下拉按钮
 function click_li(myself){  
	$(".nav_child").hide();
    $(myself).parent().find(".nav_child").show();
 }   
 function click_liout(myself){  
    $(myself).hide();
}  

 function click_li_out(myself){  
    $(".nav_child").hide();
}  

//所有input 不能输入百分号
 keyupD('searchKey');
 keyupD('titleMR');
 keyupD('title_receivedMR');
 keyupD('title_manageMR');
 keyupD('theme1');
 keyupD('searchKey_');
 keyupD('searchKey-yes');
 keyupD('searchKey-no');
 keyupD('fullnameMail');

 keyupD('fullnameWork');
 keyupD('fullnameSign');
 keyupD('fullnameMee');
 keyupD('fullnameOff');
 keyupD('bpmnName');
 keyupD('searchCardKey');
 keyupD('searchUserName');
 keyupD('categoryName');
 function keyupD(searchKey){
 	$("#"+searchKey).bind('keyup',function(){
 		$(this).val($(this).val().replace(/[%]/g,''));
 	})
 }
//树模糊搜索显示全部搜索对象
//用按钮查询节点  
 var lastValue = "", nodeList = [], fontCss = {};  
 function searchNodes(){  
 	var mytree = $.fn.zTree.getZTreeObj("treeDemo");
     var keywords=$("#peopelSel").val();  
     nodeList = mytree.getNodesByParamFuzzy("name", keywords, null);  
     if (nodeList.length>0) {  
         mytree.selectNode(nodeList[0]);  
     }else{
	   	layer.msg("没有搜索结果！");  
	    return ;  
     }   
     callNumber();
 }  
 //键盘释放：当输入框的键盘按键被松开时，把查询到的数据结果显示在标签中  
 function callNumber(){  
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");  
    //如果结果有值，则显示比例；如果结果为0，则显示"[0/0]"；如果结果为空，则清空标签框；  
    if(nodeList.length){  
        //让结果集里边的第一个获取焦点（主要为了设置背景色），再把焦点返回给搜索框  
        zTree.selectNode(nodeList[0],false );  
        //document.getElementById("peopelSel").focus();  
        clickCount=0; //防止重新输入的搜索信息的时候，没有清空上一次记录  
        //显示当前所在的是第一条  
        var number = Number(clickCount)+1;
        document.getElementById("number").innerHTML="["+number+"/"+nodeList.length+"]";  
    }else if(nodeList.length == 0){  
        document.getElementById("number").innerHTML="[0/0]";  
        zTree.cancelSelectedNode(); //取消焦点  
    }  
    //如果输入框中没有搜索内容，则清空标签框  
    if(document.getElementById("peopelSel").value ==""){  
        document.getElementById("number").innerHTML="";  
        zTree.cancelSelectedNode();  
    }  
 }   
 //点击向上按钮时，将焦点移向上一条数据  
 function clickUp(){  
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");  
    //如果焦点已经移动到了最后一条数据上，就返回第一条重新开始，否则继续移动到下一条  
    if(nodeList.length==0){  
    	layer.msg("没有搜索结果！");  
        return ;  
    }else if(clickCount==0) {  
    	layer.msg("您已位于第一条记录上！");  
        return;  
        //让结果集里边的下一个节点获取焦点（主要为了设置背景色），再把焦点返回给搜索框  
        zTree.selectNode(nodeList[clickCount], false)  
    }else{  
        //让结果集里边的第一个获取焦点（主要为了设置背景色），再把焦点返回给搜索框  
 	   clickCount --;  
        zTree.selectNode(nodeList[clickCount], false);  
    }  
    var number=Number(clickCount)+1;
    //显示当前所在的是条数  
    document.getElementById("number").innerHTML = "[" + number + "/" + nodeList.length + "]";  
 }  
 //点击向上按钮时，将焦点移向下一条数据  
 function clickDown(){  
    var zTree = $.fn.zTree.getZTreeObj("treeDemo");  
    //如果焦点已经移动到了最后一条数据上，则提示用户（或者返回第一条重新开始），否则继续移动到下一条  
    if(nodeList.length==0){  
    	layer.msg("没有搜索结果！");  
        return ;  
    }else if(nodeList.length==clickCount+1)  
    {  
    	layer.msg("您已位于最后一条记录上！")  
        return;  
    }else{  
        //让结果集里边的第一个获取焦点（主要为了设置背景色），再把焦点返回给搜索框  
 	   clickCount ++; 
        zTree.selectNode(nodeList[clickCount], false)  
    }  
    var number=Number(clickCount)+1;
    //显示当前所在的条数  
    document.getElementById("number").innerHTML = "[" + number + "/" + nodeList.length + "]";  
 }    
