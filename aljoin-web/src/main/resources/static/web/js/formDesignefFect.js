$(function(){
/*
 * 
 * 表单设计
 * 
*/	
	//左边
	$(".widget-region").children(".title-type-widget.puls").click(function(){
		$(this).siblings("div").toggle();
		if($(this).siblings("div").is(":hidden")){
			$(this).children("i").removeClass("glyphicon-chevron-up");
			$(this).children("i").addClass("glyphicon-chevron-down");
		}else{
			$(this).children("i").removeClass("glyphicon-chevron-down");
			$(this).children("i").addClass("glyphicon-chevron-up");
		}
	});
	//右边
	$(".form-main .layui-input-inline").append("<span>一级分类</span>");
	//左边滚动
	$(".scrollbar").scroll(function(){
		scrollPosition(this,".position-scroll");
	});	
});

/*
 * 
 * 表单设计
 * 
*/	
//控件设置和表单设置
function widgetRightSetObj(spanObj,num){
	$(".attribute-right .widget-set").children(spanObj).css("color","#333");
	$(".attribute-right .widget-set").children(spanObj).eq(num).css("color","#339bf1");
	$(".form-widget-Rtwrap").children(".fm-wt-public").eq(num).show().siblings().hide();
}
//滚动条出现时定位
function scrollPosition(thisObj,setClass){
	var scrTop = $(thisObj).scrollTop();
	$(thisObj).find(setClass).css({
		"position": "relative",
		"top":scrTop+"px",
		"z-index":9999,
	});
}
//删除控件位置发生改变
function removeWidgetAxis(thisObj,g){
	var thisX  = $(thisObj).parent().parent().attr("data-gs-x");   //删除控件的X轴
	thisX = parseInt(thisX);
	var thisY = $(thisObj).parent().parent().attr("data-gs-y");   //删除控件的Y轴
	thisY = parseInt(thisY);
	var thisH = $(thisObj).parent().parent().attr("data-gs-height");   //删除控件的高度
	thisH = parseInt(thisH);
	var isY = 0; //判断同轴是否还有控件
	var minYAndH = 0;  //高度大于1的高度和Y坐标和
	var minLjY = -1;  //小于当前控件并且与它最相邻的Y坐标
	var minLjH = 1;  //小于当前控件并且与它最相邻的高度
	var isYArrObj =[];  //存储同轴的控件
	var axisYArr =[];  //存储同轴或者大于的所有Y轴坐标值
	var widgetMaxYObj = [] //存储要操作的控件大于当前Y轴
	var widgetMinYObj = [] //存储要操作的控件小于当前Y轴
	var nearYVal = (thisY+thisH); //取出临近的值 ,根据临近的值来移动位置
	$("div[id*='-template_']").each(function(index){
		var axisX = $(this).parent().parent().attr("data-gs-x");
		axisX = parseInt(axisX);
		var axisY = $(this).parent().parent().attr("data-gs-y");
		axisY = parseInt(axisY);
		var axisH = $(this).parent().parent().attr("data-gs-height");
		axisH = parseInt(axisH);
		//判断同轴是否还有控件
		if(thisY == axisY){
			isYArrObj.push($(this).attr("id"));
			isY = 1;
		}
		//存储要操作的控件
		if(thisY < axisY){
			widgetMaxYObj.push($(this).attr("id"));
			//存储同轴或者大于的所有Y轴
			var logicY = $.inArray(axisY,axisYArr);
			if(logicY == -1){
				axisYArr.push(axisY);
			}
			//取出临近的值
			if(nearYVal > axisY){
				nearYVal = axisY;
			}
		}else{
				widgetMinYObj.push($(this).attr("id"));
		}
	});
	//取出小于当前并且最临近的控件
	for(var t= 0;t < widgetMinYObj.length;t++){
		var mY = -1; //取出小于当前轴高度最高值的Y轴坐标
		var isMY = $("#"+widgetMinYObj[t]).parent().parent().attr("data-gs-y");
		isMY = parseInt(isMY);
		var IsMH = $("#"+widgetMinYObj[t]).parent().parent().attr("data-gs-height");
		IsMH = parseInt(IsMH);
		//筛选出高度大于1的，小于1是同轴
		if(IsMH > 1){
			//从数组中遍历小于当前Y轴，但是在当前数组中y轴最大的
			if(mY < isMY){
				mY = isMY;
				minYAndH = mY+IsMH;
			}
		}
		if(minLjY < isMY){
			minLjY = isMY;
		}
		
	}
	//控件操作
	var YaxisObj = {};  //把分类好的控件保存到对象里
	var YaxisArrId = [];  //把控件id保存到数组里
	var YaxisArrVal = [];  //把控件Y轴的值
	for(var i=0; i<axisYArr.length; i++){
		var YArrChiId = [];
		var YArrChiVal = [];
		for(var y=0; y<widgetMaxYObj.length; y++){
			var dataGsY = $("#"+widgetMaxYObj[y]).parent().parent().attr("data-gs-y");
			if(dataGsY == axisYArr[i]){
				YArrChiId.push(widgetMaxYObj[y]);
				YArrChiVal.push(dataGsY);
			}
		}
		YaxisArrId.push(YArrChiId);
		YaxisArrVal.push(YArrChiVal);
		YaxisObj.id = YaxisArrId;
		YaxisObj.y = YaxisArrVal;
	}
	
	for(var y=0; y<widgetMaxYObj.length; y++){
		var dataGsH = $("#"+widgetMaxYObj[y]).parent().parent().attr("data-gs-height"); 
		dataGsH = parseInt(dataGsH);
		var dataGsY = $("#"+widgetMaxYObj[y]).parent().parent().attr("data-gs-y");
		dataGsY = parseInt(dataGsY);
		//同轴是否还有控件,为0没有
		if(isY == 0){
			//当前高度等于1
			if(thisH == 1){
				//当前最临近的Y坐标和高度大于当前的Y轴 说明同轴没有控件但是控件的高度到达当前控件的坐标
				if(minYAndH < thisY || minYAndH == thisY){
					var xx = $("#"+widgetMaxYObj[y]).parent().parent().attr("data-gs-x");
					g.move($("#"+widgetMaxYObj[y]).parent().parent(),xx,dataGsY-thisH);
				}
			}else{
				var maxLjY = thisY + thisH;  //大于当前控件并且与它最相邻的Y坐标
				if(maxLjY > dataGsY){
					maxLjY = dataGsY;
				}
				//Y轴之间的距离加上小于当前Y轴的最临近高度
				$("#"+widgetMaxYObj[y]).parent().parent().attr("data-gs-y",dataGsY-(maxLjY-(minLjY+minLjH)));
			}
		}else{

		}
	}
	
	
	
	
/*	for(var i = 0;i < YaxisObj.id.length;i++){
		for(var y = 0;y < YaxisObj.id[i].length;y++){
			var dataGsH = $("#"+YaxisObj.id[i][y]).parent().parent().attr("data-gs-height"); 
			dataGsH = parseInt(dataGsH);
			var dataGsY = $("#"+YaxisObj.id[i][y]).parent().parent().attr("data-gs-y");
			dataGsY = parseInt(dataGsY);
			//同轴是否还有控件,为0没有
			if(isY == 0){
				//当前高度等于1
				if(thisH == 1){
					//当前最临近的Y坐标和高度大于当前的Y轴 说明同轴没有控件但是控件的高度到达当前控件的坐标
					if(minYAndH < thisY || minYAndH == thisY){
						$("#"+YaxisObj.id[i][y]).parent().parent().attr("data-gs-y",dataGsY-thisH);
					}
				}else{
					debugger;
					var maxLjY = thisY + thisH;  //大于当前控件并且与它最相邻的Y坐标
					if(maxLjY > dataGsY){
						maxLjY = dataGsY;
					}
					//Y轴之间的距离加上小于当前Y轴的最临近高度
					$("#"+YaxisObj.id[i][y]).parent().parent().attr("data-gs-y",dataGsY-(maxLjY-(minLjY+minLjH)));
				}
			}else{

			}
		}
	}*/
	
}
//多选编辑框.关闭
function tiggerDom(thisObj){
	$(thisObj).parents(".popup-gy-style").hide();
}
//多选编辑框.保存
function depositPopupData(thisObj){
	var popupId = $(thisObj).parents(".popup-gy-style").attr("id"); //弹窗Id
	var popupType = $(thisObj).parents(".popup-gy-style").attr("type");
	var widgerId = popupId.substring(6); //根据弹窗Id得到控件Id
	var dictData =""; //多选值
	var dJsonArr =[];
	$("#"+popupId).find(".edit-che-rt").children("p").each(function(){
		dictData += $(this).text()+ ";";
		//再次保存值操作
		var dJsonObj = {};
		dJsonObj.text = $(this).text(); 
		dJsonObj.id = $(this).attr("id");
		dJsonObj.cheche = true; //右边的都是选中状态
		//多选编辑框需要多加一个参数来判断用户是用户自定义添加与否
		if(popupType == "multiselect_edit-popup" || popupType == "company-popup"){
		    dJsonObj.addType = $(this).attr("add-type");
		}
		dJsonArr.push(dJsonObj);
	});
	//遍历没选中的元素。
	$("#"+popupId).find(".edit-che-lt").children("p").each(function(){
		var dJsonObj = {};
		dJsonObj.text = $(this).text(); 
		dJsonObj.id = $(this).attr("id");
		dJsonObj.cheche = false; //右边的都是选中状态
		//多选编辑框需要多加一个参数来判断用户是用户自定义添加与否
		if(popupType == "multiselect_edit-popup" || popupType == "company-popup"){
		    dJsonObj.addType = $(this).attr("add-type");
		}
		dJsonArr.push(dJsonObj);
	});
	var dJson = JSON.stringify(dJsonArr);
	dJson = $.base64.encode(dJson);
	$("#"+popupId).attr("aljoin-datazd-json",dJson); //更新json数据
	$("#"+widgerId).val(dictData); //给多选控件赋值
	tiggerDom(thisObj); //关闭弹窗
}
//多选编辑框.选项点击
function thisTextClick(thisObj){
	$(thisObj).addClass("on");
	$(thisObj).siblings().removeClass("on");
}
function dblThisTextClick(thisObj){
	var attrTy = $(thisObj).parent().attr("type"); //获取类型根据类型判断是添加或移除
	var thisPbutt = $(thisObj).parents(".popup-center").children(".butt").children("p"); //按钮Dom
	if(attrTy == "lt"){
		//添加方法把左边赋值右边
		addChoiceFun(thisPbutt); 
	}else{
		//移除方法把右边移除到左边
		addEditCheFun(thisPbutt,0);
	}
}
//多选编辑框.添加或者删除
function addEditCheFun(thisObj,isNum){
	//说明 isNum=0删除，isNum=1添加，isNum=2用户自己添加（针对多选编辑），
	var pObj ="";
	if(isNum == "0"){
		pObj = $(thisObj).parent().siblings(".edit-che-rt").children(".on").clone();
		//添加类型如果是用户自定义添加的删除不保存在左边
		var addType = $(thisObj).parent().siblings(".edit-che-rt").children(".on").attr("add-type"); 
		if(pObj.length > 0){
			$(pObj).removeClass("on");
			$(thisObj).parent().siblings(".edit-che-rt").children(".on").remove();
			if(addType != "1"){
				$(thisObj).parent().siblings(".edit-che-lt").append(pObj);
			}
		}
	}else if(isNum == "1"){
		pObj = $(thisObj).parent().siblings(".edit-che-lt").children(".on").clone();
		if(pObj.length > 0){
			$(pObj).removeClass("on");
			$(thisObj).parent().siblings(".edit-che-lt").children(".on").remove();
			$(thisObj).parent().siblings(".edit-che-rt").append(pObj);
		}
	}else if(isNum == "2"){
		var addText = $(thisObj).parents(".form-group").find("input#model_text").val();
		if(addText != ""){
			$(thisObj).parents(".form-group").find("input#model_text").val("");
			var minObj = $(thisObj).parents(".popup-bottom").siblings(".popup-center");
			var pId = Math.uuid(10);
			pObj = minObj.children(".edit-che-lt").children("p").eq(0).clone();
			$(pObj).removeClass("on");
			$(pObj).text(addText);
			$(pObj).attr("add-type","1");
			$(pObj).attr("id","p_"+pId);
			minObj.children(".edit-che-rt").append(pObj);
		}
	}
}
//岗位选择。添加方法
function addChoiceFun(thisObj){
	var popupCenter = $(thisObj).parents(".popup-center");
	var popupId = $(thisObj).parents(".popup-gy-style").attr("id");
	var widgerId = popupId.substring(6);
	var popupType= $(thisObj).parents(".popup-gy-style").attr("type"); 
	var isMuch = $("#"+widgerId).parent().attr("aljoin-is-much");  //判断是否多选
	var clonePObj = popupCenter.children(".edit-che-lt").children(".on").clone();
	popupCenter.children(".edit-che-lt").children(".on").remove();
	clonePObj.removeClass("on"); //移除on属性然后在插入
	//赋值Dom为空不执行。避免清空到右边数据
	if(clonePObj.length == 0){
		return false;
	}
	if(popupType == "choice_gw-popup"){
		if(isMuch == "1"){
			popupCenter.children(".edit-che-rt").append(clonePObj);
		}else{
			//0单选操作
			var cloneRi = popupCenter.children(".edit-che-rt").children("p").clone();
			popupCenter.children(".edit-che-lt").append(cloneRi); //单选返回未选
			popupCenter.children(".edit-che-rt").empty(); //清空
			popupCenter.children(".edit-che-rt").append(clonePObj);
		}
	}else{
		popupCenter.children(".edit-che-rt").append(clonePObj);
	}
	
}
//接口获取数据。获取文号分类
function obtainClassList(widgetId,dataObjAttr){
	var urlStr = dataObjAttr.urlStr;  //接口地址
	var attrType= dataObjAttr.attrType; //属性类型
	var dictType = dataObjAttr.dictType; //字典类型
	var param = {};
	param.dictType = dictType;
	param._csrf = $("#_csrf").val();
	//避免二次进入不在调用接口获取数据
	if($("#panel_"+widgetId).find("#"+attrType).children().length != 0){
		return false;
	}
	var whClass= $("#"+widgetId).parent().attr(attrType);
	tool.post(urlStr, param,function(retMsg) {
		var optionDiv = "";
		for(var i = 1;i < retMsg.length;i++){
			optionDiv += "<option value="+retMsg[i].id+">"+retMsg[i].categoryName+"</option>";
		}
		$("#panel_"+widgetId).find("#"+attrType).empty();
		$("#panel_"+widgetId).find("#"+attrType).append(optionDiv);
		if(whClass.length > 0){
			$("#panel_"+widgetId).find("#"+attrType).val(whClass); //再次编辑默认选中
		}
		$("#panel_"+widgetId).find("#"+attrType).trigger("change"); //触发事件获取文号列表
	});
}
//公文文号和流水号获取分类同时也获取到当前的分类列表
function obtainClassAndClalist(targetObj,thisObj,WidgType){
	var widgAtIdSr = "panel_aljoin_form_" + WidgType;   //获取属性id前缀
	var urlSr ="";
	var attrTypeId ="";
	if(WidgType == "waternum"){
		urlSr = "../actAljoinForm/getSerialNumberList"; //接口地址
 		attrTypeId = "aljoin-sys-data-lshlist";
	}else if(WidgType == "writing"){
		urlSr = "../actAljoinForm/getDocumentNumberList"; //接口地址
 		attrTypeId = "aljoin-sys-data-whlist";
	}
	var listIDstrin = $(targetObj).attr(attrTypeId);
	var whlistObj = $(thisObj).parents("div[id^='"+widgAtIdSr+"']");
	var param = {};
	param.categoryId = $(thisObj).val();
	param._csrf = $("#_csrf").val();
	tool.post(urlSr, param,function(retMsg) {
		var optionDiv = "";
		for(var i = 0;i < retMsg.length;i++){
			var listId ="";
			var listText ="";
			if(WidgType == "waternum"){
				listId= retMsg[i].id;
				listText= retMsg[i].serialNumName;
			}else if(WidgType == "writing"){
				listId= retMsg[i].id;
				listText= retMsg[i].documentNumName;
			}
			if(listId == null){
				listId= "";
			}
			optionDiv += "<option value="+listId+">"+listText+"</option>";
		}
		whlistObj.find("#"+attrTypeId).empty();
		whlistObj.find("#"+attrTypeId).append(optionDiv);
		if(listIDstrin.length > 0){
			whlistObj.find("#"+attrTypeId).val(listIDstrin); //再次编辑默认选中
		}else{
			targetObj.attr(attrTypeId,""); //流水号列表默认选择第一个全选。全选为“空”
		}
	});
}
//常用字典获取分类
function commonZdSourceList(thisObj,modifyWiTy){
	//modifyWiTy此参数是修改页面的。
	//获取panelID进而知道控件所在div的ID，然后设置对应的属性
	var panelId = $(thisObj).parents("div[id^='panel_aljoin_form_']").attr("id");
	var hideValue = panelId.substring(6);
	//0未默认值所以不要调用接口
	var dictIS = $("#"+hideValue).parent().attr("aljoin-commonzd-source");
	var dataJson = $("#popup-"+hideValue).parent().attr("aljoin-datazd-json");
	if(dictIS == "1"){
		var widgetType = hideValue.substring(0,hideValue.lastIndexOf("_"));
		var dictType ="3";
		var urlStr ="../actAljoinForm/getDictCategoryList";
		var param = {};
		param.dictType = dictType;
		param._csrf = $("#_csrf").val();
		tool.post(urlStr, param,function(retMsg) {
			var optionDiv = "";
			for(var i = 0;i < retMsg.length;i++){
				if(i == 0){
					retMsg[i].id ="";
					retMsg[i].categoryName ="请选择";
				}
				optionDiv += "<option value="+retMsg[i].id+">"+retMsg[i].categoryName+"</option>";
			}
			$("#"+panelId).find("#aljoin-datazd-classid").empty();
			$("#"+panelId).find("#aljoin-datazd-classid").append(optionDiv);
			//表单修改页面操作。设置控件属性--开始
			if(modifyWiTy != undefined){
				//控件属性设置
				var classList = $("#"+hideValue).parent().attr("aljoin-datazd-classid");
				var zdName = $("#"+hideValue).parent().attr("aljoin-datazd-name");
				var zdListObj = $("#panel_"+hideValue).find("#aljoin-datazd-classid");
				zdListObj.val(classList);
				commonZdSourceName(zdListObj,hideValue,zdName); //设置字典名称select属性选中状态
			}
			//表单修改页面操作。设置控件属性--结束
		});
	}
}
//常用字典获取分类的名称
function commonZdSourceName(thisObj,hideValue,zdName){
	var thisObjId = $(thisObj).attr("id"); 
	var thisSibl ="";
	var categoryId ="";
	var urlStr ="";
	//当id是分类id是调用接口获取数据
	if(thisObjId == "aljoin-datazd-classid"){
		thisSibl = "aljoin-datazd-name";
		categoryId = $(thisObj).val();
		urlStr = "../actAljoinForm/getCommonListByCategory";
		$("#"+hideValue).parent().attr("aljoin-datazd-classid",categoryId); //更新控件属性
	}else{
		var valAndTe = $(thisObj).val();
		$("#"+hideValue).parent().attr(thisObjId,valAndTe); //给当前属性赋值。
		//更新常用字典保存值
		if(thisObjId == "aljoin-commonzd-source"){
			$(thisObj).parents("#panel_"+hideValue).find("#aljoin-is-commonzd-source").val(valAndTe);
			//清空常用字典数据
			if(valAndTe == "0"){
				//清除控件属性的值
				$("#"+hideValue).parent().attr("aljoin-datazd-classid","");
				$("#"+hideValue).parent().attr("aljoin-datazd-name","");
				//清除select的值
				$("#panel_"+hideValue).find("#aljoin-datazd-classid").empty();
				$("#panel_"+hideValue).find("#aljoin-datazd-name").empty();
			}else{
				$("#popup-"+hideValue).attr("aljoin-datazd-json",""); //清除json数据
			}
		}
		return false;
	}
	var param = {};
	param.categoryId = categoryId;
	param._csrf = $("#_csrf").val();
	tool.post(urlStr, param,function(retMsg) {
		var optionDiv = "";
		for(var i = 0;i < retMsg.length;i++){
			optionDiv += "<option value="+retMsg[i].dictCode+">"+retMsg[i].dictName+"</option>";
		}
		$(thisObj).parents("div[id^='panel_aljoin_form_']").find("#"+thisSibl).empty();
		$(thisObj).parents("div[id^='panel_aljoin_form_']").find("#"+thisSibl).append(optionDiv);
		//zdName不为空就是修改页面
		if(zdName != undefined){
			//表单修改页面操作。设置select属性选中状态
			$(thisObj).parents("div[id^='panel_aljoin_form_']").find("#"+thisSibl).val(zdName);
		}else{
			var thisSiVal = $(thisObj).parents("div[id^='panel_aljoin_form_']").find("#"+thisSibl).children("option").val();
			if(thisSiVal == null){
				thisSiVal = "";
			}
			$("#"+hideValue).parent().attr(thisSibl,thisSiVal); //赋值字典名称
		}

	}); 
}

	


