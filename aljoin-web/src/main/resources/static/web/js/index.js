var userfulHeaderTopItemWIdth_ = 0;
var currentTopMenuId__="";
var firstTopMenuObject__="";
var firstLeftMenuObject__="";
var currentLeftMenuId__="";
var navsMap = new Map();
var topNavsMap = new Map();
var urlIdMap = new Map();
var IdUrlMap = new Map();
var navbar_ = null;
var tab_ = null;
var isInitFirstTime = true;
//初始化顶部菜单
function initTopMenu(navs){
	//原来选择的菜单
	//$(".aljoin_top_menu_class").removeClass("layui-this");
	
	//先清空
	$(".admin-header-item2").empty();
	$("#show_more_menu_div").empty();
	
	var currentHeaderItemWIdth_= 0;
	var userfulHeaderItemWIdth_ = $(".admin-header-item2").width() - 330 - 150;
	var hideMenuCounter = 0;
	for (var i = 0; i < navs.length; i++) {
		currentHeaderItemWIdth_ = currentHeaderItemWIdth_ + $(".admin-header-item2").find("li:last").width();
		var newTopMenuTemp;
		if(currentHeaderItemWIdth_ < userfulHeaderItemWIdth_ && i <6){
			//显示出来的顶部菜单
			var topLiMenu = "<li onclick=\"pleaseSetLeftMenu('"+navs[i].id+"','"+navs[i].title+"')\" id=\""+navs[i].id+"\" class=\"layui-nav-item aljoin_top_menu_class\"  style=\"color:#ffffff;cursor: pointer;\">&nbsp;&nbsp;&nbsp;&nbsp;"+navs[i].title+"&nbsp;&nbsp;&nbsp;&nbsp;</li>";
			$(".admin-header-item2").append(topLiMenu);
			//回填
			if($(".admin-header-item2").find("li:last").attr("id") == currentTopMenuId__){
				$(".admin-header-item2").find("li:last").addClass("layui-this");
			}
		}else{
			if(hideMenuCounter == 0){
				userfulHeaderTopItemWIdth_ = currentHeaderItemWIdth_;
			}
			//隐藏的顶部菜单
			var topLiMenu = "<div onclick=\"pleaseSetLeftMenu('"+navs[i].id+"','"+navs[i].title+"')\" id=\""+navs[i].id+"\" class=\"hide_top_menu aljoin_top_menu_class\" style=\"height: 48px;line-height: 48px;float: left;cursor: pointer;\">&nbsp;&nbsp;&nbsp;&nbsp;"+navs[i].title+"&nbsp;&nbsp;&nbsp;&nbsp;</div>";
			$("#show_more_menu_div").append(topLiMenu);
			hideMenuCounter++;
			//回填
			if($("#show_more_menu_div").find("div:last").attr("id") == currentTopMenuId__){
				$("#show_more_menu_div").find("div:last").css("background-color","#000000");
				$("#show_more_menu_div").find("div:last").css("opacity","0.3");
			}
		}
		navsMap.set(navs[i].id+"",navs[i].children);
		topNavsMap.set(navs[i].id+"",navs[i]);
		
		if(i == 0){
			firstTopMenuObject__ = navs[i];
		}
	}
	//顶部下拉框菜单按钮
	//var topLiMenuIcon = "&nbsp;&nbsp;<img class=\"show_more_menu\" alt=\"\" src=\"../../web/icons/more_menu.png\" style=\"cursor: pointer;\">";
	var topLiMenuIcon = "&nbsp;&nbsp;<i class=\"show_more_menu icon aiconfont aicon-gengduo1\" onmouseenter=\"onMouseIntOutIconColor(this,1)\" onmouseleave=\"onMouseIntOutIconColor(this,2)\" style=\"cursor: pointer;font-size:21px;color:#ffffff;\"><i>";
	$(".admin-header-item2").append(topLiMenuIcon);
	//鼠标滑过样式（显示出来的）
	$(".admin-header-item2").find("li").mouseover(function(){
		$(this).css("background-color","#000000");
		$(this).css("opacity","0.3");
	});
	$(".admin-header-item2").find("li").mouseout(function(){
		$(this).css("background-color","");
		$(this).css("opacity","");
	});
	//鼠标滑过样式（隐藏的）
	$("#show_more_menu_div").find(".hide_top_menu").mouseover(function(){
		$(this).css("background-color","#000000");
		$(this).css("opacity","0.3");
	});
	$("#show_more_menu_div").find(".hide_top_menu").mouseout(function(){
		$(this).css("background-color","");
		$(this).css("opacity","");
	});
	$(".aljoin_top_menu_class").click(function(){
		//去除所有样式
		$(".aljoin_top_menu_class").removeClass("layui-this");
		$(".aljoin_top_menu_class").css("background-color","");
		$(".aljoin_top_menu_class").css("opacity","");
		//解绑事件
		$("#show_more_menu_div").find(".hide_top_menu").unbind("mouseover");
		$("#show_more_menu_div").find(".hide_top_menu").unbind("mouseout");
		//添加事件
		$("#show_more_menu_div").find(".hide_top_menu").mouseover(function(){
			$(this).css("background-color","#000000");
			$(this).css("opacity","0.3");
		});
		$("#show_more_menu_div").find(".hide_top_menu").mouseout(function(){
			$(this).css("background-color","");
			$(this).css("opacity","");
		});
		$(".admin-header-item2").find("li").mouseover(function(){
			$(this).css("background-color","#000000");
			$(this).css("opacity","0.3");
		});
		$(".admin-header-item2").find("li").mouseout(function(){
			$(this).css("background-color","");
			$(this).css("opacity","");
		});
		//当前解绑
		$(this).unbind("mouseover");
		$(this).unbind("mouseout");
		//当前菜单添加样式
		$(this).css("background-color","#000000");
		$(this).css("opacity","0.3");
		
		$("#show_more_menu_div").slideUp(200);
		currentTopMenuId__ = $(this).attr("id");
	});
	//显示更多菜单
	$('.show_more_menu').on('click', function() {
		if($("#show_more_menu_div").is(":hidden")){
			//隐藏-》显示
			$("#show_more_menu_div").width(userfulHeaderTopItemWIdth_);
			$("#show_more_menu_div").slideDown(200);
		}else{
			//显示-》隐藏
			$("#show_more_menu_div").slideUp(200);
		}
		return false;
	});
	$("#show_more_menu_div").slideUp(200);
	
	//初始化完后默认选中顶部第一个和左边第一个
	if(isInitFirstTime){
		$(".t_menu_flag_").find("#"+firstTopMenuObject__.id).trigger("click");
		$("#admin-navbar-side").find("li:first").trigger("click");
	}
	isInitFirstTime = false;
	//pleaseSetLeftMenu(firstTopMenuObject__.id,firstTopMenuObject__.title);
}
var tab;
layui.config({
	base : 'web/js/',
	version : new Date().getTime()
}).use([ 'element', 'layer', 'navbar', 'tab' ], function() {
	var element = layui.element(), $ = layui.jquery, layer = layui.layer, navbar = layui.navbar();
	tab = layui.tab({
		elem : '.admin-nav-card' // 设置选项卡容器
		,
		maxSetting: {
		max: 100
		// tipMsg: '只能开5个哇，不能再开了。真的。'
		},
		autoRefresh: false,//左侧菜单点击后是否刷新当前已经打开的页面
		contextMenu : true,
		onSwitch : function(data) {
			//点击tab后(已经打开的tab重新激活的情况)，需要定位顶部和左侧菜单
			if($(data.elem.context).prop("nodeName") == "LI"){
				var mid = data.elem.find("li.layui-this").find("i.layui-tab-close").attr("id");
				var data_url = IdUrlMap.get(mid)
				console.log(data_url );
				everyBodyGotoPageByURL(data_url)
				//如果不触发resize()，点击隐藏的tab后右边的下拉框会隐藏掉
				$("body").resize();
			}
			//console.log(data.elem.find("li.layui-this").find("i.layui-tab-close").attr("id"));
			//console.log(data.elem.html());
			//console.log(data);
			//console.log(data.id); // 当前Tab的Id
			//console.log(data.index); // 得到当前Tab的所在下标
			//console.log(data.elem); // 得到当前的Tab大容器

			//console.log(tab.getCurrentTabId())
		}
	});
	var navs = null;
	tool.post("index/menuList", "_csrf=" + $("#_csrf").val(), function(navList) {
		navs = navList;
	}, false);
	navbar_ = navbar;
	tab_ = tab;
	// iframe自适应
	$(window).on('resize', function() {
		var $content = $('.admin-nav-card .layui-tab-content');
		$content.height($(this).height() - 48);
		$content.find('iframe').each(function() {
			$(this).height($content.height());
		});
		initTopMenu(navs);
	}).resize();
	//构造urlIdMap--kru为url,value为topMenuId,leftMenuId
	for (var i = 0; i < navs.length; i++) {
		if(navs[i].children == null || navs[i].children.length == 0){
			urlIdMap.set(navs[i].href,navs[i].id+","+navs[i].id);
			IdUrlMap.set(navs[i].id,navs[i].href);
		}else{
			var pid = navs[i].id;
			for (var j = 0; j < navs[i].children.length; j++) {
				urlIdMap.set(navs[i].children[j].href,navs[i].id+","+navs[i].children[j].id);
				IdUrlMap.set(navs[i].children[j].id,navs[i].children[j].href);
			}
		}
	}
	console.log(urlIdMap);
	//setLeftMenu(navbar,navs)
	//点击隐藏左侧菜单时触发的事件
	$('.admin-side-toggle').on('click', function() {
		var sideWidth = $('#admin-side').width();
		if (sideWidth === 200) {
			$('#admin-body').animate({
				left : '0'
			}); // admin-footer
			$('#admin-footer').animate({
				left : '0'
			});
			$('#admin-side').animate({
				width : '0'
			});
			$('.ayui-side_0').animate({
				left : -200
			});
		} else {
			$('#admin-body').animate({
				left : '200px'
			});
			$('#admin-footer').animate({
				left : '200px'
			});
			$('#admin-side').animate({
				width : '200px'
			});
			$('.ayui-side_0').animate({
				left : 0
			});
		}
	});
	//点击全屏触发时触发的事件
	$('.admin-side-full').on('click', function() {
		var docElm = document.documentElement;
		// W3C
		if (docElm.requestFullscreen) {
			docElm.requestFullscreen();
		}
		// FireFox
		else if (docElm.mozRequestFullScreen) {
			docElm.mozRequestFullScreen();
		}
		// Chrome等
		else if (docElm.webkitRequestFullScreen) {
			docElm.webkitRequestFullScreen();
		}
		// IE11
		else if (elem.msRequestFullscreen) {
			elem.msRequestFullscreen();
		}
		layer.msg('按Esc即可退出全屏');
	});

	// 锁屏事件
	$(document).on('keydown', function() {
		var e = window.event;
		if (e.keyCode === 76 && e.altKey) {
			// alert("你按下了alt+l");
			lock($, layer);
		}
	});

	// 手机设备的简单适配
	var treeMobile = $('.site-tree-mobile'), shadeMobile = $('.site-mobile-shade');
	treeMobile.on('click', function() {
		$('body').addClass('site-mobile');
	});
	shadeMobile.on('click', function() {
		$('body').removeClass('site-mobile');
	});
});

//签到及其检查代码----------------------begin
set();
function set() {
	tool.post("/att/attSignInOut/getSignInOutStr", {
		_csrf : $("#_csrf").val()
	}, function(data) {
		// console.log(data)
		if (data.code == 0) {
			$(".btn_sign").text(data.object.text);
			if (data.object.disbled == true) {
				$(".btn_sign").attr({
					"disabled" : "disabled",
					"title" : "不在打卡时间范围"
				});
				$(".btn_sign").css({
					'color' : '#cccccc',
					'border' : '1px solid #cccccc'
				});
			} else {
				$(".btn_sign").removeAttr("disabled")
			}
		}
	}, true);
}
function indexStatus() {
	$.ajax({
		url : "/att/attSignInOut/confirmSign",
		async : false,
		type : "post",
		dataType : "json",
		data : {
			_csrf : $("#_csrf").val()
		},
		success : function(data) {
			if (data.code == 0) {
				layer.alert(data.message, {
					title : "签到成功",
					closeBtn : 0,
					area : [ '430px' ],
					icon : 1,
				}, function(index) {
					set();
					layer.close(index);
				});
			} else {
				if (data.message != null) {
					layer.alert(data.message, {
						title : "签到失败",
						closeBtn : 0,
						icon : 2
					}, function(index) {
						layer.close(index);
					});
				}

			}
		},
		error : function(error) {
			parent.location.href = "/";
		}
	});
	return false;
}
//签到及其检查代码-----------------------end

//获取当前时间代码-----------------------begin
(function() {
    var lastTime = 0;
    var vendors = ['webkit', 'moz'];
    for(var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
        window.requestAnimationFrame = window[vendors[x] + 'RequestAnimationFrame'];
        window.cancelAnimationFrame = window[vendors[x] + 'CancelAnimationFrame'] ||    // name has changed in Webkit
                                      window[vendors[x] + 'CancelRequestAnimationFrame'];
    }
    if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function(callback, element) {
            var currTime = new Date().getTime();
            var timeToCall = Math.max(0, 16.7 - (currTime - lastTime));
            var id = window.setTimeout(function() {
                callback(currTime + timeToCall);
            }, timeToCall);
            lastTime = currTime + timeToCall;
            return id;
        };
    }
    if (!window.cancelAnimationFrame) {
        window.cancelAnimationFrame = function(id) {
            clearTimeout(id);
        };
    }
}());
function animate() { 
	var Date = tool.getNowDate();
	$("#time_div_").text(Date[0]);
	$("#date_div_").text(Date[2]);
	$("#week_div_").text(Date[3])
	 requestAnimationFrame(animate); 
} 
requestAnimationFrame(animate);
//获取当前时间代码-----------------------end
//解除铃铛下拉列表的鼠标滑过事件-------begin

function deleteMouseOverEvent(thisObject){
	$(thisObject).css("background-color","#ffffff");
}
//解除铃铛下拉列表的鼠标滑过事件-------begin
function gotoTargetPage(thisObject){
	//$(thisObject).parent().removeClass("layui-this");
	return false;
}
//在线通知 待办 公告 文件 统计
function workTotal(){
	tool.post("/aut/autDataStatistics/indexCount",{_csrf:$("#_csrf").val()},function(data){
		if(data.object!=null){
		    var data=data.object;
		    var totalCount_ = 0;
		    for(var i=0;i<data.length;i++){
		    	$(".indexCount_top_").each(function(){
		    		var id=$(this).attr("id");
		    		if(id==data[i].objectKey){
		    			$(this).text(data[i].objectCount)
		    			//数量大于0改变颜色
		    			if(data[i].objectCount > 0){
		    				$(this).css("color","#ff3030");
		    			}else{
		    				$(this).css("color","#999999");
		    			}
		    			totalCount_ += data[i].objectCount;
		    		}
		    	});
		    }
		    if(totalCount_ >0){
	    		$(".indexCount_top_0").css("color","#ff3030");
	    	}else{
	    		$(".indexCount_top_0").css("color","#999999");
	    	}
	    	$(".indexCount_top_0").text(totalCount_);
	    	setTotalMsgCountDiv(totalCount_);
		}
	},true);
}
workTotal();
//动态设置顶部菜单
function setTotalMsgCountDiv(number){
	var right = "115px";
	var showText = "";
	if(number == 0){
		$("#total_msg_count_div").hide();
		return;
	}
	if(number > 0){
		right = "137px";
		showText = number;
	}
	if(number > 9){
		right = "134px";
		showText = number;
	}
	if(number > 99){
		right = "127px";
		showText = "99+";
	}
	$("#total_msg_count_div").css("right",right);
	$("#total_msg_count_div").text(showText);
	$("#total_msg_count_div").show();
}
function pleaseSetLeftMenu(mid,mname){
	$("#left_top_menu_name_").text(mname);
	if(navsMap.get(mid) ==  null){
		var topNavsMapArr = new Array();
		topNavsMapArr.push(topNavsMap.get(mid));
		setLeftMenu(topNavsMapArr);
		//console.log(topNavsMap.get(mid));
	}else{
		setLeftMenu(navsMap.get(mid));
	}
	//console.log(navsMap.get(mid));
}
function setLeftMenu(navData){
	// 设置navbar
	navbar_.set({
		spreadOne : true,
		elem : '#admin-navbar-side',
		cached : true,
		data : navData
	});
	// 渲染navbar
	navbar_.render();
	// 监听点击事件
	navbar_.on('click(side)', function(data) {
		var iframeId = "";
		currentLeftMenuId__ = data.field.id;
		//海洋局样式改造：因为隐藏了顶部的tab,所以这里先关闭后新增以达到刷新效果  add by yxc
		//tab_.deleteTab(tab_.getCurrentTabId());
		tab_.tabAdd(data.field);
		
		if($("#indexPageIframe").length>0){
			iframeId = "indexPageIframe";
		}else{
			iframeId = "notIndexPageIframe";
		}
		doAfterIframeLoad(iframeId);
	});
}
$("body").click(function() {
	$("#show_more_menu_div").slideUp(200);
});
function closeTopMen_() {
	$("#show_more_menu_div").slideUp(200);
}

//这段代码心好累
function doAfterIframeLoad(iframeId) {
	var iframe = document.getElementById(iframeId);
	if (iframe.attachEvent) {
		iframe.attachEvent("onload", function() {
			//iframe加载完成后你需要进行的操作
			$('#'+iframeId).contents().find("body").click(function(){
				closeTopMen_();
			});
		});
	} else {
		iframe.onload = function() {
			//iframe加载完成后你需要进行的操作
			$('#'+iframeId).contents().find("body").click(function(){
				closeTopMen_();
			});
		};
	}
}
//跳转到对应页面
function everyBodyGotoPageByURL(url){
	var ids = urlIdMap.get(url);
	var idsArr = ids.split(",");
	everyBodyGotoPageByMenuId(idsArr[0],idsArr[1])
}
function everyBodyGotoPageByMenuId(topMenuId,leftMenuId){
	$(".t_menu_flag_").find("#"+topMenuId).trigger("click");
	$("#admin-navbar-side").find("#"+leftMenuId).trigger("click");
}
function onMouseIntOutIconColor(thisObj,flag){
	if(flag == 1){
		//进
		$(thisObj).css("color","#cccccc");
	}else{
		//出
		$(thisObj).css("color","#ffffff");
	}
}




