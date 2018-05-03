var tool = {
	/**
	 * 分页加载数据
	 */
	loadpage : function(param) {
		var initLoadIndex = layer.load(2, {
			time : 1 * 300
		});
		param._csrf = $("#_csrf").val();
		param.pageNum = 1;
		var initPageData;
		tool.post(param.url, param, function(initPageData) {
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
		document.getElementById(param.container + "-data").innerHTML = template(param.container + "-script", pageData);
		// 重新加载元素样式
		layui.use('form', function() {
			var form = layui.form();
			form.on('checkbox(' + param.container + '_check-all)', function(data) {
				var checkElement = $(data.elem);
				var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]');
				child.each(function(index, item) {
					item.checked = data.elem.checked;
				});
				form.render('checkbox');
			});
			form.render();
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
			icon : 1
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
			icon : 2
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
				var str='<div class="layui-input-inline  nextselects"><span></span>'+o.message+'</div>';
				$(str).insertAfter($(thisdata).parent());
				$(".nextselects").find("select").removeAttr("lay-verify");
				layui.use('form', function() {
					var form = layui.form;
					    form.render();
				});
			}
			$(thisdata).parents("#form-type-object").children(".layui-input-inline").each(function(index){
				var level = index+1;
				level= tool.convertNum(level);
				$(this).children("span").text(level+"级分类");
			});
		}, false);
	},
	/**
	 * 根据父id获取子列表
	 */
	selectLinkTagByPid2 : function(elementId,topElementId,id,idLength){
		var idLengthNam = idLength;
		//console.log(data)
		var param = new Object();
		//var thisdata=data.elem;
		//$("#"+topElementId).find("#"+elementId);
		//获取选中的控件的该属性值
		param.table = $("#"+elementId).attr("aljoin-table");
		param.parentId = id;
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
			if(o.message!=""){
				var str='<div class="layui-input-inline nextselects"><span></span>'+o.message+'</div>'
				$("#form-type-object").append(str);
			}
			$("#form-type-object").children(".layui-input-inline").each(function(index){
				var level = index+1;
				level= tool.convertNum(level);
				$(this).children("span").text(level+"级分类");
			});
		}, false);
	},
	convertNum:function(num){
		var str = '';
		switch(num){
			case 1:
				str = '一';
				break;
			case 2:
				str = '二';
				break;
			case 3:
				str = '三';
				break;
			case 4:
				str = '四';
				break;
			case 5:
				str = '五';
				break;
			case 6:
				str = '六';
				break;
			case 7:
				str = '七';
				break;
			case 8:
				str = '八';
				break;
			case 9:
				str = '九';
				break;
			case 10:
				str = '十';
				break;
		}
		return str;	
	},	
};
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


