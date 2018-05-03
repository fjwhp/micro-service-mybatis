//初始化数据
$(function() {
	// 功能按钮显示
	var operateAuthIds = $("#operateAuthIds").val();
	var operateArray = operateAuthIds.split(",");
	var taskClaimIs = $.inArray("aljoin-task-claim", operateArray); // 校验是否签收
	var signInTime = $("#signInTime").val(); // 签收时间
	var activityId = $("#activityId").val(); // 流程id
	for (var i = 0; i < operateArray.length; i++) {
		if (activityId == "" && signInTime == "") {
			$("#" + operateArray[i]).show();
			$("#aljoin-task-claim").hide(); // 第一节点签收时间和流程id为空
		} else {
			if (taskClaimIs != -1) {
				$("#aljoin-task-claim").show();
			}
			if (signInTime != "") {
				$("#" + operateArray[i]).show();
				$("#aljoin-task-claim").hide();
			}
			$("#aljoin-task-flowchart").show();// 流程图按钮
			$("#aljoin-task-circulation-log").show();// 流转日志按钮
		}

	}
	$("#btn-controlShow").find(".dropdown-menu li a").each(function() {
		var openShow = $.inArray($(this).attr("id"), operateArray);
		if (openShow != -1) {
			$(this).parents(".dropdown-menu").siblings("button").show();
		}

	});
	$("#aljoin-task-opinion").hide(); // 填写意见域隐藏
	// 如果不是待办 就隐藏所有按钮只显示 在办详情需要的 流程图 流转日志 撤回 打印按钮
	if ($("#isWait").val() == "0") {
		$("#btn-controlShow").find("button").hide();
		$("#aljoin-task-flowchart").show();// 流程图按钮
		$("#aljoin-task-flowcharts").show();// 流程图按钮
		$("#aljoin-task-circulation-log").show();// 流转日志按钮
		$("#aljoin-task-revoke").show();// 撤回按钮
		$("#aljoin-task-print").show();// 打印按钮
		for (var i = 0; i < operateArray.length; i++) {
			$("#" + operateArray[i]).show();// 传阅按钮
		}
	}
	openCircula();
	// 意见域
	var commentWidgetIds = $("#commentWidgetIds").val();
	var commentWidgetIdArr = commentWidgetIds.split(",");
	for (var i = 0; i < commentWidgetIdArr.length; i++) {
		$("#" + commentWidgetIdArr[i]).attr("readonly", "readonly");
		$("#" + commentWidgetIdArr[i]).addClass("readonly_style");
	}
	// 是否显示
	var showWidgetIds = $("#showWidgetIds").val();
	var dataArray = showWidgetIds.split(",");
	// 是否可编辑
	var editWidgetIds = $("#editWidgetIds").val();
	var editWidgetArr = editWidgetIds.split(",");
	// 是否为空
	var notNullWidgetIds = $("#notNullWidgetIds").val();
	var notNullArr = notNullWidgetIds.split(",");
	/** *****以下为正文文件特殊属性设置****** */
	// 红头文件
	var redHeadWidgetIds = $("#redHeadWidgetIds").val();
	var redHeadArr = redHeadWidgetIds.split(",");
	// 流程节点控制是否保留痕迹
	var saveMarkWidgetIds = $("#saveMarkWidgetIds").val();
	var saveMarArr = saveMarkWidgetIds.split(",");
	/** *****正文文件特殊属性设置结束****** */
	$("#form-content-div-form")
			.find(".grid-stack-item-content")
			.each(
					function() {
						var widgetType = $(this).attr("type"); // 获取类型
						$(this).find("input").removeAttr("placeholder"); // 移除placeholder
						$(this).find("textarea").removeAttr("placeholder"); // 移除placeholder
						if (widgetType == "text") {

							secondChildPublic(this, "input"); // 初始化校验
							// 是否显示/编辑/为空
							// 判断是否已经读取数据源 1：是
							var thisObjId = $(this).children()
									.children("input").attr("id");
							;// 控件id
							var dataReadIs = $("#dataReadIs_" + thisObjId)
									.val();
							// text属性无需在赋值只需判断是否已读数据源 因为值只有一个
							if (dataReadIs == 1) {
								$(this).children().attr("aljoin-field-readis",
										"1");
							} else {
								$(this).children().attr("aljoin-field-readis",
										"0");
							}

						} else if (widgetType == "area_text") {

							secondChildPublic(this, "textarea"); // 初始化校验
							// 是否显示/编辑/为空

						} else if (widgetType == "radio") {
							// 控件id
							var thisObjId = $(this).find("#radio_flag").val();
							var showLog = $.inArray(thisObjId, dataArray); // 是否显示
							var editL = $.inArray(thisObjId, editWidgetArr); // 是否可编辑
							// 判断是否已经读取数据源 1：是
							var dataReadIs = $("#dataReadIs_" + thisObjId)
									.val();
							if (dataReadIs == 1) {
								$(this).children().attr("aljoin-field-readis",
										"1");
								var thisObj = $(this);
								sourceDataRead(thisObj, "radio");
							} else {
								$(this).children().attr("aljoin-field-readis",
										"0");
							}
							// 属性设置
							if (showLog != -1) {
								$(this).parent().show();
								$(this).parent().attr("name", "data-y");
							}
							// 是否可编辑
							if (editL == -1) {
								$(this).find("input[id^=" + thisObjId + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										});
							}
							// 单选不能为空
							$(this).children().attr("aljoin-allow-null", 0);
						} else if (widgetType == "checkbox") {
							// 控件id
							var thisObjId = $(this).find("input:hidden").val();
							var showLog = $.inArray(thisObjId, dataArray); // 是否显示
							var editL = $.inArray(thisObjId, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObjId, notNullArr); // 是否为空
							var result = $.inArray(thisObjId, dataArray);
							// 判断是否已经读取数据源 1：是
							var dataReadIs = $("#dataReadIs_" + thisObjId)
									.val();
							if (dataReadIs == 1) {
								$(this).children().attr("aljoin-field-readis",
										"1");
								var thisObj = $(this);
								sourceDataRead(thisObj, "checkbox");
							} else {
								$(this).children().attr("aljoin-field-readis",
										"0");
							}
							// 属性设置
							if (showLog != -1) {
								$(this).parent().show();
								$(this).parent().attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("input[id^=" + thisObjId + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										});
							}
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "select") {
							// 控件id
							var thisObjId = $(this).children().children().attr(
									"id");
							var editL = $.inArray(thisObjId, editWidgetArr); // 是否可编辑
							var result = $.inArray(thisObjId, dataArray);
							// 判断是否已经读取数据源 1：是
							var dataReadIs = $("#dataReadIs_" + thisObjId)
									.val();
							if (dataReadIs == 1) {
								$(this).children().attr("aljoin-field-readis",
										"1");
								var thisObj = $(this);
								sourceDataRead(thisObj, "select");
							} else {
								$(this).children().attr("aljoin-field-readis",
										"0");
							}
							// 属性设置
							if (result != -1) {
								$("#" + thisObjId).parents(".grid-stack-item")
										.show();
								$("#" + thisObjId).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObjId).attr("disabled",
										true);
							}
						} else if (widgetType == "contact_select") {
							var thisObj = $(this).find("#contact_select_flag")
									.val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$(this).parents(".grid-stack-item").show();
								$(this).parents(".grid-stack-item").attr(
										"name", "data-y");
							}
							;
							if (editL == -1) {
								$(this).find("select[id^=" + thisObj + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										})
							}
							;
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "contact_select_three") {
							// 控件id字符
							var thisObj = $(this).find("input:hidden").val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$(this).parent().show();
								$(this).parent().attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("select[id^=" + thisObj + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										})
							}
							;
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "contact_select_four") {
							// 控件id字符
							var thisObj = $(this).find("input:hidden").val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$(this).parent().show();
								$(this).parent().attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("select[id^=" + thisObj + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										})
							}
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "contact_select_five") {
							// 控件id字符
							var thisObj = $(this).find("input:hidden").val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$(this).parent().show();
								$(this).parent().attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("select[id^=" + thisObj + "_]")
										.each(function() {
											$(this).attr("disabled", true);
										})
							}
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "number") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "decimal") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "email") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "telephone") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "mobilephone") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "date") {
							// 控件id
							var thisObj = $(this).children().children()
									.children("input").attr("id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							$("#" + thisObj).attr("readonly", "readonly"); // 不可输入
							$("#" + thisObj).addClass("readonly_style");
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisObj).attr("disabled", true);
								$("#" + thisObj).removeClass("readonly_style");
							}
							if (notNull != -1) {
								$("#" + thisObj).parent().attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "date_period") {
							// 控件id字符串
							var thisObj = $(this).find("#date_period_flag")
									.val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空

							if (showLog != -1) {
								$(this).find("#date_period_flag").parents(
										".grid-stack-item").show();
								$(this).find("#date_period_flag").parents(
										".grid-stack-item").attr("name",
										"data-y");
							}

							$(this).find("input[id^=" + thisObj + "_]").each(
									function() {
										$(this).attr("readonly", "readonly"); // 不可输入
										$(this).addClass("readonly_style");
										if (editL == -1) {
											$(this).attr("disabled", true);
											$(this).removeClass(
													"readonly_style");
										}
									});
							if (notNull != -1) {
								$("input:hidden[value=" + thisObj + "]")
										.parent("div").parent().attr(
												"aljoin-allow-null", 0);
							}
						} else if (widgetType == "time") {
							// 是否显示
							var thisObj = $(this).children().children()
									.children("input:hidden").attr("id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							$(this).find("#" + thisObj).attr("readonly",
									"readonly"); // 不可输入
							$(this).find("#" + thisObj).addClass(
									"readonly_style");

							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObj).attr("disabled",
										true);
								$(this).find("#" + thisObj).removeClass(
										"readonly_style");
							}
							if (notNull != -1) {
								$(this).children("div").attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "datetime") {
							// 是否显示
							var thisObj = $(this).children().children()
									.children("input:hidden").attr("id");
							var showLog = $.inArray(thisObj, dataArray);
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							$(this).find("#" + thisObj).attr("readonly",
									"readonly"); // 不可输入
							$(this).find("#" + thisObj).addClass(
									"readonly_style");
							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObj).attr("disabled",
										true);
								$(this).find("#" + thisObj).removeClass(
										"readonly_style");
							}
							if (notNull != -1) {
								$(this).children("div").attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "datetime_period") {
							// 是否显示
							var thisObj = $(this).find("#datetime_period_flag")
									.val();
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$(this).find("#datetime_period_flag").parents(
										".grid-stack-item").show();
								$(this).find("#datetime_period_flag").parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							$(this).find("input[id^=" + thisObj + "_]").each(
									function() {
										$(this).attr("readonly", "readonly"); // 不可输入
										$(this).addClass("readonly_style");
										if (editL == -1) {
											$(this).attr("disabled", true);
											$(this).removeClass(
													"readonly_style");
										}
									})
							if (notNull != -1) {
								$("input:hidden[value=" + thisObj + "]")
										.parent("div").parent().attr(
												"aljoin-allow-null", 0);
							}
						} else if (widgetType == "image") {
							// 控件id
							var thisObj = $(this).find("input[type='file']")
									.attr("id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisObj).parents(".grid-stack-item")
										.show();
								$("#" + thisObj).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObj).attr("disabled",
										true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parents(
										"div[id^='image-template_']").attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "attach") {
							// 控件id
							var thisObj = $(this).find("input[type='file']")
									.attr("id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObj).attr("disabled",
										true);
							}
							if (notNull != -1) {
								$("#" + thisObj).parents(
										"div[id^='attach-template_']").attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "datagrid") {
							// 控件id
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							if (editL == -1) {
								var thisParentsObj = $(this)
										.find("#" + thisObj).parent();
								thisParentsObj.find("th").children("button")
										.remove();
								thisParentsObj.find("td").children("button")
										.remove();
								thisParentsObj.find("td").children("input")
										.attr("disabled", true);
							}
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "editor") {
							// 控件id
							var thisObj = $(this).children().children()
									.children().attr("id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑

							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}

							$("#aljoin_form_editor_q6djjzpGDKQroxX").contents()
									.find("body")
									.attr("contenteditable", false);
							/*
							 * if(editL == -1){
							 * $("#aljoin_form_editor_q6djjzpGDKQroxX").contents("body").attr("contenteditable",false); }
							 */
						} else if (widgetType == "href") {
							// 是否显示
							var thisIds = $(this).children().children()
									.children("input").attr("id");
							var showLog = $.inArray(thisIds, dataArray); // 是否显示
							var editL = $.inArray(thisIds, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisIds, notNullArr); // 是否为空
							if (showLog != -1) {
								$("#" + thisIds).parents(".grid-stack-item")
										.show();
								$("#" + thisIds).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$(this).children().children().children("input")
										.attr("disabled", true);
							}
							if (notNull != -1) {
								$(this).children().attr("aljoin-allow-null", 0);
							}
						} else if (widgetType == "label") {
							// 是否显示
							var thisObj = $(this).children().children().attr(
									"id");
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
						} else if (widgetType == "biz_title") {
							secondChildPublic(this, "input"); // 初始化校验
							// 是否显示/编辑/为空
						} else if (widgetType == "biz_finish_time") {
							// 是否显示
							var thisObj = $(this).children().children()
									.children("input:hidden").attr("id");
							var showLog = $.inArray(thisObj, dataArray);
							var showLog = $.inArray(thisObj, dataArray); // 是否显示
							var editL = $.inArray(thisObj, editWidgetArr); // 是否可编辑
							var notNull = $.inArray(thisObj, notNullArr); // 是否为空
							$(this).find("#" + thisObj).attr("readonly",
									"readonly"); // 不可输入
							$(this).find("#" + thisObj).addClass(
									"readonly_style");
							if (showLog != -1) {
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").show();
								$(this).find("#" + thisObj).parents(
										".grid-stack-item").attr("name",
										"data-y");
							}
							if (editL == -1) {
								$(this).find("#" + thisObj).attr("disabled",
										true);
								$(this).find("#" + thisObj).removeClass(
										"readonly_style");
							}
							if (notNull != -1) {
								$(this).children("div").attr(
										"aljoin-allow-null", 0);
							}
						} else if (widgetType == "biz_text_office") {
							// 控件id
							var thisIds = $(this).children().children("input")
									.attr("id");
							// var retainEecord =
							// $(this).children().attr("aljoin-office-retain-eecord");
							// //痕迹保留
							var showLog = $.inArray(thisIds, dataArray); // 是否显示
							var editL = $.inArray(thisIds, editWidgetArr); // 打开方式
							var notNull = $.inArray(thisIds, notNullArr); // 是否新建
							var redHead = $.inArray(thisIds, redHeadArr); // 红头文件
							var saveMar = $.inArray(thisIds, saveMarArr); // 保留痕迹
							if (showLog != -1) {
								$("#" + thisIds).parents(".grid-stack-item")
										.show();
								$("#" + thisIds).parents(".grid-stack-item")
										.attr("name", "data-y");
							}
							if (editL == -1) {
								$("#" + thisIds).parent("div").attr(
										"aljoin-office-on-pattern", 2); // 只读
							} else {
								if (saveMar != -1) {
									$("#" + thisIds).parent("div").attr(
											"aljoin-office-on-pattern", 3); // 可编辑并保留痕迹
								} else {
									$("#" + thisIds).parent("div").attr(
											"aljoin-office-on-pattern", 4); // 可编辑
								}
							}
							if (redHead != -1) {
								$("#" + thisIds).parent("div").attr(
										"aljoin-office-on-pattern", 5); // 红头文件
							}
							// 是否新建
							if (notNull != -1) {
								$("#" + thisIds).parent("div").attr(
										"aljoin-office-on-pattern", 1); // 新建
								// $("#"+thisIds).parent("div").attr("aljoin-office-sure-build",1);
								// //新建
							}

						}
					});
	// 单行文本,多行文本,标题
	// 当前元素和子集元素
	function secondChildPublic(thisObj, element) {
		// 控件id
		var thisIds = $(thisObj).children().children(element).attr("id");
		var showLog = $.inArray(thisIds, dataArray); // 是否显示
		var editL = $.inArray(thisIds, editWidgetArr); // 是否可编辑
		var notNull = $.inArray(thisIds, notNullArr); // 是否为空
		if (showLog != -1) {
			$("#" + thisIds).parents(".grid-stack-item").show();
			$("#" + thisIds).parents(".grid-stack-item").attr("name", "data-y");
		}
		if (editL == -1) {
			$(thisObj).children().children(element).attr("disabled", true);
		}
		if (notNull != -1) {
			$(thisObj).children().attr("aljoin-allow-null", 0);
		}
	}
	// 位置排序
	var showArrayY = new Array(); // 存储(有显示的dom)y轴坐标
	var hideArray = new Array(); // 存储(隐藏的dom)y轴坐标和高度
	var hideSize = 0;
	$("#form-content-div-form").children(".grid-stack").children(
			".ui-draggable").each(function() {
		if ($(this).attr("name") != "data-y") {
			hideSize++;
			var valueObj = new Object;
			valueObj.y = $(this).attr("data-gs-y");
			valueObj.h = $(this).attr("data-gs-height");
			var flag = 0;
			for (var int = 0; int < hideArray.length; int++) {
				if (hideArray[int].y == valueObj.y) {
					if (hideArray[int].h < valueObj.h) {
						hideArray[int].h = valueObj.h;
					}
					flag = 1;
					break;
				}
			}
			if (flag == 0) {
				hideArray.push(valueObj);
			}
			$(this).remove();
		} else {
			var valueShowY = $(this).attr("data-gs-y");
			showArrayY.push(valueShowY);
		}
		;
	});
	for (var i = 0; i < hideArray.length; i++) {
		var isArrayLogci = $.inArray(hideArray[i].y, showArrayY); // 判断是否有在显示的元素里面
		if (isArrayLogci == -1) {
			$("#form-content-div-form").children(".grid-stack").children(
					".ui-draggable").each(function() {
				var allShowY = $(this).attr("data-gs-y");
				if (parseInt(allShowY) >= parseInt(hideArray[i].y)) {
					var thisShowY = $(this).attr("data-gs-y");
					$(this).attr("data-gs-y", thisShowY - hideArray[i].h);
				}
			});
		}
	}
	// 解析数据源
	systemDataSource();
	// 数据渲染
	loadFormData();
	// 解析文件正文
	parserTextOfficeWidget();
	// 在办详情判断是否签收 没有签收显示撤回按钮
	// checkClaim();
});
// 已读数据源赋值
function sourceDataRead(thisObj, elem) {
	var thisWidgetId = "";
	var selectHtml = "";
	var readIs = "";
	if (elem == "select") {
		thisWidgetId = $(thisObj).children().children("select").attr("id");
		selectHtml = $("#sourceData_" + thisWidgetId).val();
		selectHtml = $.base64.decode(selectHtml);
		selectHtml = $.base64.decode(selectHtml);
		$(thisObj).children().children("select").html(selectHtml);
		var orgnlVal = $("#orgnl_" + thisWidgetId).val();
		$(thisObj).children().children("select").val(orgnlVal);
	} else if (elem == "checkbox") {
		thisWidgetId = $(thisObj).children().children("#checkbox_flag").val();
		selectHtml = $("#sourceData_" + thisWidgetId).val();
		selectHtml = $.base64.decode(selectHtml);
		selectHtml = $.base64.decode(selectHtml);
		$(thisObj).children().html(selectHtml);
	} else if (elem == "radio") {
		thisWidgetId = $(thisObj).children().children("#radio_flag").val();
		selectHtml = $("#sourceData_" + thisWidgetId).val();
		selectHtml = $.base64.decode(selectHtml);
		selectHtml = $.base64.decode(selectHtml);
		$(thisObj).children().html(selectHtml);

	} else if (elem == "text") {
		// text 无需执行数据源因为值只保存一次
	}

}
// 解析数据源
function systemDataSource() {
	var activityId = $("#activityId").val(); // 流程id
	$("div[id*='-template_']")
			.each(
					function(index) {
						var thisObj = $(this);
						var type = thisObj.parent().attr("type");
						var sourceFT = thisObj.attr("aljoin-data-source"); // 是否有数据来源
						var readIs = thisObj.attr("aljoin-field-readis"); // 是否已经读取数据来源
						if (sourceFT == 2 && readIs != 1) {
							// 未读取数据源赋值
							var param = new Object();
							param.type = thisObj.attr("aljoin-sys-data-source");
							param._csrf = $("#_csrf").val();
							if (param.type == "")
								return false;
							thisObj.children("label").remove(); // 去除radio和checkbox...
							thisObj.children("select").children().remove(); // 去除select
							var radioChecId = $(this).children("input").val(); // 获取单选多选控件id
							var selectWidId = $(this).children("select").attr(
									"id"); // 获取下拉控件id
							tool
									.post(
											"../actAljoinForm/getAllSource",
											param,
											function(retMsg) {
												for (var i = 0; i < retMsg.length; i++) {
													var radioChecIdUu = Math
															.uuid(10);
													if (type == "text") {
														thisObj
																.children()
																.val(
																		retMsg[i].value);
														// 添加属性,是否已经读取数据源,1:是
														thisObj
																.attr(
																		"aljoin-field-readis",
																		"1");
													} else if (type == "radio") {
														var valueHtml = "<input type=\"radio\" value="
																+ retMsg[i].value
																+ " id="
																+ radioChecId
																+ "_"
																+ Math.uuid(10)
																+ " name="
																+ radioChecId
																+ ">";
														var spanHtml = "<span>"
																+ retMsg[i].text
																+ "</span>";
														var labelHtml = "<label class=\"radio-inline\" id=l_"
																+ radioChecId
																+ '_'
																+ radioChecIdUu
																+ ">"
																+ valueHtml
																+ spanHtml
																+ "</label>";
														thisObj
																.append(labelHtml);
														// 添加属性,是否已经读取数据源,1:是
														thisObj
																.attr(
																		"aljoin-field-readis",
																		"1");
													} else if (type == "checkbox") {
														var valueHtml = "<input type=\"checkbox\" value="
																+ retMsg[i].value
																+ " id="
																+ radioChecId
																+ "_"
																+ Math.uuid(10)
																+ " name="
																+ radioChecId
																+ ">";
														var spanHtml = "<span>"
																+ retMsg[i].text
																+ "</span>";
														var labelHtml = "<label class=\"checkbox-inline\" id=l_"
																+ radioChecId
																+ '_'
																+ radioChecIdUu
																+ ">"
																+ valueHtml
																+ spanHtml
																+ "</label>";
														thisObj
																.append(labelHtml);
														var orgnlVal = $(
																"#orgnl_"
																		+ radioChecId)
																.val();
														// 添加属性,是否已经读取数据源,1:是
														thisObj
																.attr(
																		"aljoin-field-readis",
																		"1");

													} else if (type == "select") {
														var optionHtml = "<option value="
																+ retMsg[i].value
																+ " id="
																+ selectWidId
																+ "_"
																+ radioChecIdUu
																+ ">"
																+ retMsg[i].text
																+ "</option>";
														thisObj
																.children(
																		"select")
																.append(
																		optionHtml);
														// 添加属性,是否已经读取数据源,1:是
														thisObj
																.attr(
																		"aljoin-field-readis",
																		"1");
													} else if (type == "label") {
														thisObj
																.children("p")
																.text(
																		retMsg[i].value);
													}
												}
											});
						}
					});
}
// 数据渲染
function loadFormData() {
	var activityId = $("#activityId").val(); // 流程id
	if (activityId == "")
		return; // 流程id为空不执行
	$("#form-content-div-form")
			.find(".grid-stack-item-content")
			.each(
					function() {
						var widgetType = $(this).attr("type"); // 获取类型
						if (widgetType == "text") {
							// 单行文本
							secondLevelChild(this, "input");

						} else if (widgetType == "area_text") {
							// 多行文本
							secondLevelChild(this, "textarea");

						} else if (widgetType == "radio") {
							// 单选
							var thisId = $(this).find("#radio_flag").val();
							var thisValSld = $("#orgnl_" + thisId).val();
							if (thisValSld == undefined || thisValSld == "")
								return;
							$("label[id^=l_" + thisId + "]").children(
									"input[value=" + thisValSld + "]").attr(
									"checked", true);

						} else if (widgetType == "checkbox") {
							// 多选
							var thisId = $(this).find("#checkbox_flag").val();
							var thisValSld = $("#orgnl_" + thisId).val();
							if (thisValSld == undefined || thisValSld == "")
								return;
							var thisValArray = thisValSld.split(",");
							for (var i = 0; i < thisValArray.length; i++) {
								$("label[id^=l_" + thisId + "]")
										.children(
												"input[value="
														+ $.base64
																.decode(thisValArray[i])
														+ "]").attr("checked",
												true);
							}
						} else if (widgetType == "select") {
							// 下拉框
							var thisId = $(this).find("select").attr("id");
							var thisValSld = $("#orgnl_" + thisId).val();
							$(this).find("select").val(thisValSld);
						} else if (widgetType == "contact_select") {
							// 二级联动
							var thisId = $(this).find("#contact_select_flag")
									.val();
							formLinkage(this, thisId);
						} else if (widgetType == "contact_select_three") {
							// 三级联动
							var thisId = $(this).find(
									"#contact_select_three_flag").val();
							formLinkage(this, thisId);
						} else if (widgetType == "contact_select_four") {
							// 四级联动
							var thisId = $(this).find(
									"#contact_select_four_flag").val();
							formLinkage(this, thisId);
						} else if (widgetType == "contact_select_five") {
							// 五级联动
							var thisId = $(this).find(
									"#contact_select_five_flag").val();
							formLinkage(this, thisId);
						} else if (widgetType == "number") {
							// 整数
							numberTypePublicVal(this);
						} else if (widgetType == "decimal") {
							// 小数
							numberTypePublicVal(this);
						} else if (widgetType == "email") {
							// 邮箱
							numberTypePublicVal(this);
						} else if (widgetType == "telephone") {
							// 电话
							numberTypePublicVal(this);
						} else if (widgetType == "mobilephone") {
							// 手机
							numberTypePublicVal(this);
						} else if (widgetType == "date") {
							// 日期
							timeTypePublicVal(this);
						} else if (widgetType == "date_period") {
							// 日期区间
							var thisId = $(this).find("#date_period_flag")
									.val();
							timeTypePeriodVal(this, thisId, "input");
						} else if (widgetType == "time") {
							// 时间
							timeTypePublicVal(this);
						} else if (widgetType == "datetime") {
							// 日期时间
							timeTypePublicVal(this);
						} else if (widgetType == "datetime_period") {
							// 日期时间区间
							var thisId = $(this).find("#datetime_period_flag")
									.val();
							timeTypePeriodVal(this, thisId, "input");
						} else if (widgetType == "image") {
							// 图片
							var thisId = $(this).find(
									"input[id^='aljoin_form_image_']").attr(
									"id");
							var eleId = "widget_imgsrc_push";
							var eleParen = "image-template_";
							loadImgAttaData(thisId, eleParen, eleId);
						} else if (widgetType == "attach") {
							// 附件
							var thisId = $(this).find(
									"input[id^='aljoin_form_attach_']").attr(
									"id");
							var eleId = "widget_attachsrc_push";
							var eleParen = "attach-template_";
							loadImgAttaData(thisId, eleParen, eleId);
						} else if (widgetType == "datagrid") {
							// 数据表格
							var thisObj = $(this).children(
									"div[id^=datagrid-template_]").children()
									.attr("id");
							var thisValSld = $("#orgnl_" + thisObj).val();
							thisValSld = $.base64.decode(thisValSld);
							thisValSld = $.base64.decode(thisValSld);
							$(this).children().empty();
							$(this).append(thisValSld);
						} else if (widgetType == "editor") {
							// 编辑器

						} else if (widgetType == "href") {
							// 网址
							numberTypePublicVal(this);
						} else if (widgetType == "label") {
							// 标签
						} else if (widgetType == "biz_title") {
							secondLevelChild(this, "input");
						} else if (widgetType == "biz_finish_time") {
							// 办结时间
							timeTypePublicVal(this);
						} else if (widgetType == "biz_text_office") {
							// 文件正文
						}

					});
	/* 单行文本,多行文本,标题 */
	function secondLevelChild(thisObj, element) {
		var thisId = $(thisObj).find(element).attr("id");
		var thisValue = $("#orgnl_" + thisId).val()
		$(thisObj).find(element).val(thisValue);
	}
	/* 二级联动至五级联动 */
	function formLinkage(thisObj, thisId) {
		/*
		 * var jsonDecode =
		 * $.base64.decode($(thisObj).children("div").attr("aljoin-select-json"));
		 * jsonDecode = JSON.parse(jsonDecode); console.log(jsonDecode);
		 * console.log(jsonDecode); var thisValSld = $("#orgnl_"+thisId).val();
		 * var thisValArray = thisValSld.split(","); for(var i=0;i<thisValArray.length;i++){
		 * console.log($.base64.decode(thisValArray[i]));
		 * //$(thisObj).children("div").children("div").children("select").val($.base64.decode(thisValArray[i])); }
		 * 
		 * bindContactSelectJson();
		 */

	}
	/* 日期区间,日期时间区间 */
	function timeTypePeriodVal(thisObj, thisId, element) {
		// console.log($.base64.decode("MjAxNy0xMS0yNw==,MjAxNy0xMS0zMA=="));
		var thisValSld = $("#orgnl_" + thisId).val();
		var thisValArray = thisValSld.split(",");
		for (var i = 0; i < thisValArray.length; i++) {
			$(thisObj).find(element + "[id^=" + thisId + "]").eq(i).val(
					$.base64.decode(thisValArray[i]));
		}
	}
	/* 图片附件 */
	function loadImgAttaData(thisId, eleParen, eleId) {
		var headTitle = "附件";
		if (eleParen == "image-template_") {
			headTitle = "图片";
		}
		var attachObj = $("#" + thisId).parents("div[id^='" + eleParen + "']");
		// console.log(attachObj);
		var thisValSld = $("#orgnl_" + thisId).val(); // 返回数据
		thisValSld = $.base64.decode(thisValSld); // 需解码两次
		thisValSld = $.base64.decode(thisValSld);
		var thisValObj = JSON.parse(thisValSld);
		if (thisValObj.length != 0) {
			$(attachObj)
					.append(
							"<div id='"
									+ eleId
									+ "' class='widget_datasrc_push'><div class='head-title' style='display: none;'>"
									+ headTitle
									+ "</div><div class='min-content'></div></div>");
		}
		console.log(thisValObj);
		for (var i = 0; i < thisValObj.length; i++) {
			var thisSrc = thisValObj[i].src;
			var thisName = thisValObj[i].name;
			var thisDelId = thisValObj[i].dalWorkId;
			var groupName = thisValObj[i].groupName;
            var fileName = thisValObj[i].fileName;
			var dalWorkId = thisValObj[i].dalWorkId;
			var delFilesData = "<span onclick='delFilesData(this,\""
					+ thisDelId + "\");'>删除</span>";
			// 在办工作 都无权操作
			if ($("#isWait").val() == "0") {
				delFilesData = "";
			}
			$(attachObj).children("#" + eleId).children(".min-content").append("<p><a href="+thisSrc+" title='"+thisName+"' onclick=\"downImgAttachData('"+groupName+"','"+fileName+"');return false;\" groupName='"+groupName+"' fileName='"+fileName+"' dalWorkId='"+dalWorkId+"'>"+thisName+"</a>"+delFilesData+"</p>");
		}
		var srcPushObj = $(attachObj).children("#" + eleId)
		var intNum = srcPushObj.children(".min-content").children("p").size();
		if (intNum > 3) {
			srcPushObj
					.append("<button type='button' class='btn btn-default btn-sm src_push_absolu' onclick='attachSrcPushMore(this)'>更多</button>");
		}

	}
	/* 整数,小数,邮箱,电话,手机 */
	function numberTypePublicVal(thisObj) {
		var thisId = $(thisObj).find("input").attr("id");
		$(thisObj).find("input").val($("#orgnl_" + thisId).val());
	}
	/* 日期,时间,日期时间 */
	function timeTypePublicVal(thisObj) {
		var thisId = $(thisObj).find("input").attr("id");
		$(thisObj).find("input").val($("#orgnl_" + thisId).val());
	}
};
/**
 * 存稿
 */
$("#aljoin-task-save").click(
		function() {
			var signInTime = $('#signInTime').val();
			var activityId = $("#activityId").val();
			var checkActivityIdNull = $("#checkActivityIdNull").val();
			if (signInTime == "" && activityId != ""
					&& checkActivityIdNull != 1) {
				tool.error("请先签收任务");
				return;
			}

			if (activityId != "" && checkActivityIdNull != 1) {
				// tool.error("请勿重复存稿");
				// 在待办的第一节点的存稿操作视为保存
				tasksaveFunction("存稿");
				return;
			}

			var intLogic = attributeVerifi(intLogic); // 验证表单属性
			if (intLogic != 1) {
				return;
			}
			var form_data = draftSubmit(); // 构建数据
			var isUrgent = $("#isUrgent").val();
			// 发送数据
			layer.confirm('确定要存稿吗？', {
				icon : 3,
				btn : [ '确定', '取消' ]
			// 按钮
			}, function() {
				layer.closeAll();
				var param = "isUrgent=" + isUrgent + "&" + form_data;
				tool.post("../../ioa/ioaCreateWork/saveDraft", param,
						postCallBack, false);
			});
			// 新增对象回调
			function postCallBack(retMsg) {
				if (retMsg.code == 0) {
					tool.success(retMsg.message);
					layer.alert('存稿成功！', {
						icon : 1,
						title : '提示',
						closeBtn : 0
					}, function(index) {
						parent.location.reload();
					})
				} else {
					tool.error(retMsg.message);
				}
			}
		});

/**
 * 退回操作
 */
$("#form-jump-submit").click(
		function() {
			// 表单数据
			var formData = $('#taskformdata').val();

			var taskId_v = $("#activityId").val();
			;
			var targetTaskKey_v = $("#targetTaskKey_back").val();
			var targetUserId_v = $("#targetUserId_back").val();
			var _csrf_v = $("#_csrf").val();

			var isTask_v = $('#isTask').val();
			var nextNode_v = $('#targetTaskKey_back').val();

			var thisTaskUserComment_v = $("#thisTaskUserComment").val();
			var param = "taskId=" + taskId_v + "&targetTaskKey="
					+ targetTaskKey_v + "&targetUserId=" + targetUserId_v
					+ "&_csrf=" + _csrf_v + "&thisTaskUserComment="
					+ thisTaskUserComment_v + "&" + formData + "&isTask="
					+ isTask_v + "&nextNode=" + nextNode_v;
			if ($("#thisTaskUserComment").val() == "") {
				layer.confirm('意见为空，确定提交？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					tool.post("../../ioa/ioaWaitWork/jumpTask", param,
							function(obj) {
								if (obj.code == 0) {
									layer.alert(obj.message, {
										title : "操作提示",
										icon : 1,
										closeBtn : 0
									}, function(index) {
										parent.layer.closeAll();
										parent.loadData();
										parent.location.reload();
									});
								} else {
									tool.error(obj.message);
								}
							}, false);
				}, function() {

				});
			} else {
				tool.post("../../ioa/ioaWaitWork/jumpTask", param,
						function(obj) {
							if (obj.code == 0) {
								layer.alert(obj.message, {
									title : "操作提示",
									icon : 1,
									closeBtn : 0
								}, function(index) {
									parent.layer.closeAll();
									parent.loadData();
									parent.location.reload();
								});
							} else {
								tool.error(obj.message);
							}
						}, false);
			}
			return false;
		});

/**
 * 审批完成任务(确认)
 */

$("#form-add-submit").click(
		function() {
			var taskId = $("#activityId").val();
			var isUrgent = $('#isUrgent').val();
			var formData = $('#taskformdata').val();
			var _csrf = $("#_csrf").val();
			var isTask = $('#isTask').val();
			var thisTaskUserComment = $('#thisTaskUserComment').val();

			// 拼接taskAuth参数
			var uidStr = "";
			if (processType == 1 || processType == 3) {
				// 1-自由选择节点(逗号分隔-构造成下拉)，3-自由节点(逗号分隔-构造成下拉)
				$(".free_keyId").each(
						function(index, val) {
							var string = $(this).val();
							if (index % 2 == 0) {
								if ($(this).next().val() != ""
										&& $(this).next().val() != null) {
									uidStr += string + ",";
								}
							} else {
								if (string != "" || string != null) {
									string = string
											.substr(0, string.length - 1);
									string = string.replace(/;/g, "#")
									uidStr += string + ";";
								}
							}
						})
			} else {
				// 2-排他节点(节点列表)，4-并行节点(节点列表)
				$(".uid_key").each(
						function(index, val) {
							var string = $(this).val();
							if (index % 2 == 0) {
								if ($(this).next().val() != ""
										&& $(this).next().val() != null) {
									uidStr += string + ",";
								}
							} else {
								if (string != "" || string != null) {
									string = string
											.substr(0, string.length - 1);
									string = string.replace(/;/g, "#")
									uidStr += string + ";";
								}
							}
						})
			}
			uidStr = uidStr.substr(0, uidStr.length - 1);

			// 获取下一级审批环节的值
			/*
			 * var nextNode = $("#nextNode").val(); console.log(nextNode)
			 */
			if (isTask == "false") {
				var tempUserId = $("#testarea").attr("userId");
				if (tempUserId) {
					userId = tempUserId;
				}
			}
			var userId = "";
			if (tempUserId) {
				userId = tempUserId;
			}

			// 判空
			if (processType == 1 || processType == 3) {
				// 获取下一级审批环节的值
				var nextNode = $("#nextNode").val();
				// 下拉判空，1-自由选择节点(逗号分隔-构造成下拉)，3-自由节点(逗号分隔-构造成下拉)
				var option_text = $("#nextNode option:selected").text();
				var muloption_text = $("#testarea").html();
				if (option_text == "请选择") {
					tool.error("请填写下一级审批环节");
					return false;
				} else if (option_text != "结束" && option_text != "请选择") {
					if (muloption_text == "") {
						tool.error("请填写下一级审批处理人");
						return false;
					}
				}
			} else {
				// 获取下一级审批环节的值
				if ($(".activity_key").length == 1) {
					var nextNode = $(".activity_key").val();
				}
				// 列表uid判空
				if ($(".uname").text() != "结束") {
					var nullFlag = 0;
					$(".uid").each(function(index, val) {
						if ($(this).val() == "") {
							nullFlag++;
						}
					});
					if (nullFlag > 0) {
						tool.error("请填写审批人员");
						return false;
					}
				}
			}
			var topButtonComment = $("#topButtonComment").val();
			var param = "topButtonComment=" + topButtonComment
					+ "&thisTaskUserComment=" + thisTaskUserComment + "&_csrf="
					+ _csrf + "&" + "isUrgent=" + isUrgent + "&"
					+ "activityId=" + taskId + "&" + "isTask=" + isTask + "&"
					+ "nextNode=" + nextNode + "&" + "userId=" + userId + "&"
					+ formData + "&" + "taskAuth=" + uidStr;
			if ($("#thisTaskUserComment").val() == "") {
				layer.confirm('意见为空，确定提交？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					tool.post("../../ioa/ioaCreateWork/doCreateWork", param,
							postCallBackPubInfo, false);
				}, function() {
				});
			} else {
				layer.confirm('确定提交？', {
					icon : 3,
					btn : [ '确定', '取消' ]
				// 按钮
				}, function() {
					tool.post("../../ioa/ioaCreateWork/doCreateWork", param,
							postCallBackPubInfo, false);
				}, function() {

				});
			}
			return false;
		});
/**
 * 提交
 */
var processType, isFinishMergeTask, deptId, openType, taskType;
$("#aljoin-task-submit")
		.click(
				function() {
					// 每次点击提交打开审批页面置空
					$("#thisTaskUserComment").val("");
					$("#testarea").html("");
					$("#free_key").val("");
					$("#free_id").val("");

					var signInTime = $('#signInTime').val();
					var activityId = $("#activityId").val();
					var checkActivityIdNull = $("#checkActivityIdNull").val();
					if (signInTime == "" && activityId != ""
							&& checkActivityIdNull != 1) {
						tool.error("请先签收任务");
						return;
					}
					var intLogic = attributeVerifi(intLogic); // 验证表单属性
					if (intLogic != 1) {
						return;
					}
					var form_data = draftSubmit();
					var isUrgent = $("#isUrgent").val();
					if (activityId == "") {
						var param = "isUrgent=" + isUrgent + "&" + form_data;
						tool.post("../../ioa/ioaCreateWork/saveDraft", param,
								postCallBack, false);
						// 新增对象回调
						function postCallBack(retMsg) {
							var data = retMsg.object;
							var taskIdArr = data.split(",");
							activityId = taskIdArr[2];
							$("#activityId").val(activityId);

						}
					}
					var param = "taskId=" + activityId + "&" + form_data;
					tool
							.post(
									"../../ioa/ioaWaitWork/getNextTaskInfo",
									param,
									function(retMsg) {
										console.log(retMsg)
										var data = retMsg.object;
										var isTask = data.isTask;
										isFinishMergeTask = data.isFinishMergeTask;
										processType = data.processType;
										var taskName = "";
										var taskKey = "";
										var defaultAssigneeUser = "";
										$('#taskformdata').val(data.result);
										if (retMsg.object.length == 0) {
											taskName = "结束";
										} else {
											taskKey = data.taskKey;
											taskName = data.taskName;
										}
										/* if(isTask==false){ */
										if (processType == 1
												|| processType == 3) {
											// 1-自由选择节点(逗号分隔-构造成下拉)，3-自由节点(逗号分隔-构造成下拉)
											$("#audit-orgn").hide();
											$("#audit-member").show();
											$("#isTask").val(isTask);
											var taskNames = new Array(); // 定义一数组
											var taskKeys = new Array(); // 定义一数组
											taskNames = taskName.split(","); // 字符分割
											taskKeys = taskKey.split(","); // 字符分割
											$("#nextNode").empty();
											$("#nextNode")
													.append(
															'<option value="">请选择</option>');
											for (i = 0; i < taskKeys.length; i++) {
												$("#nextNode")
														.append(
																'<option value="'
																		+ taskKeys[i]
																		+ '" readonly="readonly">'
																		+ taskNames[i]
																		+ '</option>');
											}
										} else {
											// 2-排他节点(节点列表)，4-并行节点(节点列表)
											$("#audit-orgn").show();
											$("#audit-member").hide();
											if (null != retMsg) {
												var fileBack = data.taskVOList;
												var notfreelist = {
													notfreelist : fileBack
												};
												$('#objList').html(
														template("obj-script",
																notfreelist));
												// 人员选择一级弹窗数据遍历
												for (var i = 0; i < retMsg.object.taskVOList.length; i++) {
													openType = data.taskVOList[i].openType;
													taskType = data.taskVOList[i].taskType;

													$(".taskKeyClass")
															.each(
																	function(
																			index,
																			value) {
																		var that = $(this);
																		if ($(
																				this)
																				.attr(
																						"value") == retMsg.object.taskVOList[i].taskKey) {
																			listArr = data.taskVOList[i].userList;
																			deptId = data.taskVOList[i].deptList;
																			if (data.taskVOList[i].defaultAssigneeUser != null) {
																				defaultAssigneeUser = data.taskVOList[i].defaultAssigneeUser;
																				// 当只有一个人员时默认填上
																				if (openType == 2) {
																					$(
																							this)
																							.parent()
																							.prev()
																							.find(
																									".userList_name")
																							.text(
																									defaultAssigneeUser.userName);
																					$(
																							this)
																							.parent()
																							.prev()
																							.find(
																									".userList_id")
																							.val(
																									defaultAssigneeUser.userId
																											+ ";");
																				}
																				if (openType == 3
																						|| openType == 4) {
																					$(
																							this)
																							.parent()
																							.prev()
																							.find(
																									".userList_name")
																							.text(
																									defaultAssigneeUser.userName);
																					$(
																							this)
																							.parent()
																							.prev()
																							.find(
																									".userList_id")
																							.val(
																									defaultAssigneeUser.userId
																											+ ";");
																				}
																			}
																			// 默认人员全选
																			if (isFinishMergeTask > 0
																					&& listArr.length != 1) {
																				$
																						.each(
																								listArr,
																								function(
																										index,
																										value) {
																									var valueS = that
																											.parent()
																											.prev()
																											.find(
																													".userList_name")
																											.text();
																									// console.log(valueS)
																									that
																											.parent()
																											.prev()
																											.find(
																													".userList_name")
																											.text(
																													valueS
																															+ value.userName
																															+ ";");
																									var valueId = that
																											.parent()
																											.prev()
																											.find(
																													".userList_id")
																											.val();
																									that
																											.parent()
																											.prev()
																											.find(
																													".userList_id")
																											.val(
																													valueId
																															+ value.userId
																															+ ";");
																								})
																			}
																			// 将对象转为字符串
																			var listArr = JSON
																					.stringify(listArr);
																			var deptId = JSON
																					.stringify(deptId);

																			$(
																					this)
																					.attr(
																							"listArr",
																							listArr);
																			$(
																					this)
																					.attr(
																							"openType",
																							openType);
																			$(
																					this)
																					.attr(
																							"deptId",
																							deptId);
																			if (data.taskVOList[i].defaultAssigneeUser != null) {
																				var defaultAssigneeUser = JSON
																						.stringify(defaultAssigneeUser);
																				$(
																						this)
																						.attr(
																								"defaultAssigneeUser",
																								defaultAssigneeUser);
																			}
																		}
																	})
												}
											}
										}
										if (retMsg.code == 0) {
											var index = layer.open({
												title : '审批处理',
												maxmin : false,
												type : 1,
												area : [ '900px', '510px' ],
												shadeClose : true,
												content : $('#win-yes-object')
											});
											pform.render();
										}
										$("#checkActivityIdNull").val("1");
									}, false);
					options();
				});
/**
 * 通过
 */
$("#aljoin-task-pass").click(function() {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();

	if (signInTime == "" && activityId != "") {
		tool.error("请先签收任务");
		return;
	}

	alert("通过");
});

/**
 * 退回
 */
$("#aljoin-task-back").click(function() {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();

	if (signInTime == "" && activityId != "") {
		tool.error("请先签收任务");
		return;
	}

	alert("退回");
});

/**
 * 打印
 */
$("#aljoin-task-print").click(function() {
	$("#btn-controlShow").hide();
	document.execCommand("print");
	$("#btn-controlShow").show();
});

/**
 * 分发
 */
$("#aljoin-task-distribute").click(function() {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();

	if (signInTime == "" && activityId != "") {
		tool.error("请先签收任务");
		return;
	}

	alert("分发");
});

/**
 * 传阅
 */
$("#aljoin-task-circulation").click(function() {
	var index1 = layer.open({
		title : '选择传阅',
		btn : [ '确认', '取消' ],
		maxmin : false,
		type : 1,
		shadeClose : true,
		area : [ '500px', '360px' ],
		yes : function(index, layero) {
			circulation();
		},
		btn2 : function(index, layero) {
			$("#circulation_sendId").val("");
			$("#send-person-name").html("");
			layer.close(index1)
		},
		content : $('#circulation-person-choose')
	});
});

/**
 * 加签
 */
$("#aljoin-task-addsign").click(function() {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();

	if (signInTime == "" && activityId != "") {
		tool.error("请先签收任务");
		return;
	}

	alert("加签");
});

/**
 * 加急
 */
$("#aljoin-task-urgent3").click(function() {
	layer.confirm('是否加急?', {
		icon : 3,
		title : '提示'
	}, function(index) {
		$('#isUrgent').val("3");
		tool.success("设置成功");
	});
});

/**
 * 紧急
 */
$("#aljoin-task-urgent2").click(function() {
	layer.confirm('是否紧急?', {
		icon : 3,
		title : '提示'
	}, function(index) {
		$('#isUrgent').val("2");
		tool.success("设置成功");
	});
});

/**
 * 一般
 */
$("#aljoin-task-urgent1").click(function() {
	layer.confirm('是否一般?', {
		icon : 3,
		title : '提示'
	}, function(index) {
		$('#isUrgent').val("3");
		tool.success("设置成功");
	});
});

/**
 * 意见
 */
$("#aljoin-task-opinion")
		.click(
				function() {
					var signInTime = $('#signInTime').val();
					var activityId = $('#activityId').val();

					if (signInTime == "" && activityId != "") {
						tool.error("请先签收任务");
						return;
					}

					var index = layer.open({
						title : '意见域',
						area : [ '960px', '450px' ],
						type : 1,
						content : $('#win-update-object')
					});
					var _csrf = $("#_csrf").val();
					var param = {
						_csrf : _csrf,
					}
					tool
							.post(
									"../../ioa/ioaWaitWork/fillOpinion",
									param,
									function(data) {
										for (var i = 0; i < data.length; i++) {
											// console.log(data[i].content);
											var tr = "<tr><td width:90%>"
													+ data[i].content
													+ "</td><td style=\"text-align: center;width:10%\"><span class=\"glyphicon\" onclick=\"spanTextOpinion(this)\"></span></td></tr>";
											$("#win-update-object").find(
													"#style_xh_numtr").append(
													tr);
										}
									}, false);

				});

/**
 * 作废
 */
$("#aljoin-task-close").click(
		function() {
			var signInTime = $('#signInTime').val();
			var activityId = $('#activityId').val();
			var checkActivityIdNull = $("#checkActivityIdNull").val();
			if (signInTime == "" && activityId != ""
					&& checkActivityIdNull != 1) {
				tool.error("请先签收任务");
				return;
			}

			layer.confirm('确定作废吗 ?', {
				icon : 3,
				title : '提示'
			}, function(index) {
				var _csrf = $("#_csrf").val();
				var data = {
					activityId : $('#activityId').val(),
					_csrf : _csrf,
				}
				tool.post("../../act/actAljoinFormDataDraft/closeTask", data,
						function(retMsg) {
							if (retMsg.code == 0) {
								tool.success(retMsg.message);
								parent.location.reload();
							} else {
								tool.error(retMsg.message);
							}
						}, false)
			});
		});

/**
 * 签收
 */
$("#aljoin-task-claim").click(function() {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();

	if (signInTime != "") {
		tool.error("该任务已被签收");
		return;
	}
	layer.confirm('确定签收吗 ?', {
		icon : 3,
		title : '提示'
	}, function(index) {
		// 功能按钮显示
		var operateAuthIds = $("#operateAuthIds").val();
		var operateArray = operateAuthIds.split(",");
		for (var i = 0; i < operateArray.length; i++) {
			$("#" + operateArray[i]).show();
		}
		$("#aljoin-task-claim").hide();
		var _csrf = $("#_csrf").val();
		var data = {
			ids : activityId,
			_csrf : _csrf,
		}
		tool.post("../../ioa/ioaWaitWork/claimTask", data, function(retMsg) {
			if (retMsg.code == 0) {
				// tool.success(retMsg.message);
				layer.alert(retMsg.message, {
					title : "操作提示",
					icon : 1,
					closeBtn : 0
				}, function(index) {
					layer.closeAll();
					$('#signInTime').val("1970-01-01 12:12:12");
				});
			} else {
				tool.error(retMsg.message);
			}
		}, false)
	});
});

function tasksaveFunction(flagType) {
	var signInTime = $('#signInTime').val();
	var activityId = $('#activityId').val();
	var checkActivityIdNull = $("#checkActivityIdNull").val();
	if (signInTime == "" && activityId != "" && checkActivityIdNull != 1) {
		tool.error("请先签收任务");
		return;
	}

	if (activityId == "" && activityId.length == 0) {
		tool.error("工作未新建");
		return;
	}

	var intLogic = attributeVerifi(intLogic); // 验证表单属性
	if (intLogic != 1) {
		return;
	}
	var form_data = draftSubmit();
	var activityId = $("#activityId").val();
	// 发送数据
	layer.confirm('确定要' + flagType + '吗？', {
		btn : [ '确定', '取消' ],
		icon : 3
	}, function() {
		var param = "activityId=" + activityId + "&" + form_data;
		tool.post("../../ioa/ioaCreateWork/addDraft", param, postCallBack,
				false);
	});
	// 新增对象回调
	function postCallBack(retMsg) {
		if (retMsg.code == 0) {
			layer.alert(retMsg.message, {
				title : "操作提示",
				icon : 1,
				closeBtn : 0
			}, function(index) {
				parent.layer.closeAll();
				var isWait = $("#isWait").val();
				if (isWait == "") {
					parent.getWaitWork(); // 调用首页待办方法重新加载数据
				} else {
					parent.loadData();
				}
			});
		} else {
			tool.error(retMsg.message);
		}
	}
}

/**
 * 保存
 */
$("#aljoin-task-tasksave").click(function() {
	tasksaveFunction("保存");
});
// 存稿,提交共用
function draftSubmit() {
	// 最终需要进行表单提交的数据
	var bpmnId = $("#bpmnId").val();
	var formId = $("#formId").val();
	var _csrf = $("#_csrf").val();
	var form_data = "bpmnId=" + bpmnId + "&formId=" + formId + "&_csrf="
			+ _csrf + "&";
	var formWidgetIds = ""; // (动态获取表单控件需要提交参数的ID)

	// 获取text(动态获取)
	$("#form-content-div-form").find("div[id^='text-template_']").each(
			function() {
				secondLevelChildValue(this, "input");
			});
	// 获取area_text(动态获取)
	$("#form-content-div-form").find("div[id^='area_text-template_']").each(
			function() {
				secondLevelChildValue(this, "textarea");
			});
	// 获取radio(动态获取)
	$("#form-content-div-form").find("div[id^= radio-template_]").each(
			function() {
				var checkedInput = $(this).find("#radio_flag")
						.siblings("label").children("input:checked").val();
				form_data += $(this).find("#radio_flag").val() + "="
						+ checkedInput + "&";
				formWidgetIds += $(this).find("#radio_flag").val() + ",";
			});
	// 获取checkbox(动态获取)
	$("#form-content-div-form").find("div[id^=checkbox-template_]").each(
			function() {
				var thisId = $(this).find("#checkbox_flag").val();
				var checkArrVal = "";
				$(this).children("label[id^=l_" + thisId + "]").each(
						function() {
							var checkedInput = $(this).find("input:checked")
									.val();
							if (checkedInput == undefined)
								return;
							var base64Code = $.base64.encode(checkedInput);
							checkArrVal += base64Code + ",";
						});
				if (checkArrVal.indexOf(",") > -1) {
					if (checkArrVal.lastIndexOf(",") > -1) {
						checkArrVal = checkArrVal.substr(0, checkArrVal
								.lastIndexOf(","));
					}
				}
				form_data += thisId + "=" + checkArrVal + "&";
				formWidgetIds += thisId + ",";
			});
	// 获取select(动态获取)
	$("#form-content-div-form").find("div[id^=select-template_]").each(
			function() {
				form_data += $(this).children().attr("id") + "="
						+ $(this).children().val() + "&";
				formWidgetIds += $(this).children().attr("id") + ",";
			});
	// 获取contact_select(动态获取)
	$("#form-content-div-form").find("div[id^=contact_select-template_]").each(
			function() {
				var thisId = $(this).find("#contact_select_flag").val();
				contactSelect(this, thisId, "select");
			});
	// 获取contact_select_three(动态获取)
	$("#form-content-div-form").find("div[id^=contact_select_three-template_]")
			.each(function() {
				var thisId = $(this).find("#contact_select_three_flag").val();
				contactSelect(this, thisId, "select");
			});
	// 获取contact_select_four(动态获取)
	$("#form-content-div-form").find("div[id^=contact_select_four-template_]")
			.each(function() {
				var thisId = $(this).find("#contact_select_four_flag").val();
				contactSelect(this, thisId, "select");
			});
	// 获取contact_select_five(动态获取)
	$("#form-content-div-form").find("div[id^=contact_select_five-template_]")
			.each(function() {
				var thisId = $(this).find("#contact_select_five_flag").val();
				contactSelect(this, thisId, "select");
			});
	// 获取image(动态获取)
	$("#form-content-div-form").find("input[id^=aljoin_form_image_]")
			.each(
					function() {
						imgAttachSrcPush(this, "image-template_",
								"#widget_imgsrc_push");
					});
	// 获取attach(动态获取)
	$("#form-content-div-form").find("input[id^=aljoin_form_attach_]").each(
			function() {
				imgAttachSrcPush(this, "attach-template_",
						"#widget_attachsrc_push");
			});
	// 获取number(动态获取)
	$("#form-content-div-form").find("div[id^=number-template_]").each(
			function() {
				form_data += $(this).children().attr("id") + "="
						+ $(this).children().val() + "&";
				formWidgetIds += $(this).children().attr("id") + ",";
			});
	// 获取decimal(动态获取)
	$("#form-content-div-form").find("div[id^=decimal-template_]").each(
			function() {
				numberTypePublic(this);
			});
	// 获取email(动态获取)
	$("#form-content-div-form").find("div[id^=email-template_]").each(
			function() {
				numberTypePublic(this);
			});
	// 获取telephone(动态获取)
	$("#form-content-div-form").find("div[id^=telephone-template_]").each(
			function() {
				numberTypePublic(this);
			});
	// 获取mobilephone(动态获取)
	$("#form-content-div-form").find("div[id^=mobilephone-template_]").each(
			function() {
				numberTypePublic(this);
			});
	// 获取date(动态获取)
	$("#form-content-div-form").find("div[id^=date-template_]").each(
			function() {
				timeTypePublic(this);
			});
	// 获取date_period(动态获取)
	$("#form-content-div-form").find("div[id^=date_period-template_]").each(
			function() {
				var thisId = $(this).find("#date_period_flag").val();
				contactSelect(this, thisId, "input");
			});
	// 获取time(动态获取)
	$("#form-content-div-form").find("div[id^=time-template_]").each(
			function() {
				timeTypePublic(this);
			});
	// 获取datetime(动态获取)
	$("#form-content-div-form").find("div[id^=datetime-template_]").each(
			function() {
				timeTypePublic(this);
			});
	// 获取datetime_period(动态获取)
	$("#form-content-div-form").find("div[id^=datetime_period-template_]")
			.each(function() {
				var thisId = $(this).find("#datetime_period_flag").val();
				contactSelect(this, thisId, "input");
			});
	// 获取datagrid(动态获取)
	$("#form-content-div-form").find("div[id^=datagrid-template_]").each(
			function() {
				var thisId = $(this).children().attr("id");
				var checkArrVal = $(this).children.html();
				checkArrVal = $.base64.encode(checkArrVal);
				checkArrVal = $.base64.encode(checkArrVal);
				form_data += thisId + "=" + checkArrVal + "&";
				formWidgetIds += thisId + ",";
			});
	// 获取href(动态获取)
	$("#form-content-div-form").find("div[id^=href-template_]").each(
			function() {
				form_data += $(this).children().children("input").attr("id")
						+ "=" + $(this).children().children("input").val()
						+ "&";
				formWidgetIds += $(this).children().children("input")
						.attr("id")
						+ ",";
			});
	// 获取biz_title(动态获取)
	$("#form-content-div-form").find("div[id^=biz_title-template_]").each(
			function() {
				secondLevelChildValue(this, "input");
			});
	// 获取biz_finish_time(动态获取)
	$("#form-content-div-form").find("div[id^=biz_finish_time-template_]")
			.each(function() {
				timeTypePublic(this);
			});
	// 获取biz_text_office(动态获取)
	$("#form-content-div-form").find("input[id^=aljoin_form_biz_text_office_]")
			.each(
					function() {
						form_data += $(this).attr("id") + "="
								+ $("#attachId").val() + "&";
						formWidgetIds += $(this).attr("id") + ",";
						// console.log(formWidgetIds);
						// $(this).parent("div").attr("aljoin-attachId-val",$("#attachId").val());
					});
	/* 单行文本,多行文本,标题 */
	function secondLevelChildValue(thisObj, element) {
		var thisValue = $(thisObj).children(element).val();
		thisValue = thisValue.replace(/\n|\r\n/g, "\n");
		thisValue = encodeURIComponent(thisValue);
		form_data += $(thisObj).children(element).attr("id") + "=" + thisValue
				+ "&";
		formWidgetIds += $(thisObj).children(element).attr("id") + ",";
	}
	/* 图片，附件 */
	function imgAttachSrcPush(thisObj, eleParent, eleChild) {
		var dataSrcPush = $(thisObj).parents("div[id^='" + eleParent + "']")
				.children(eleChild).children(".min-content");
		var pushDataObj = new Object;
		var pushDataArray = [];
		dataSrcPush.children("p").each(function() {
			var thisSrc = $(this).children("a").attr("href");
            var thisName = $(this).children("a").text();
            var dalWorkId = $(this).children("a").attr("dalWorkId");
            var groupName = $(this).children("a").attr("groupName");
            var fileName = $(this).children("a").attr("fileName");
            var thisDAtaObj = new Object;
            thisDAtaObj.src = thisSrc;
            thisDAtaObj.name = thisName;
            thisDAtaObj.dalWorkId = dalWorkId;
            thisDAtaObj.groupName = groupName;
            thisDAtaObj.fileName = fileName;
		});
		pushDataObj = pushDataArray;
		pushDataObj = JSON.stringify(pushDataObj);
		pushDataObj = $.base64.encode(pushDataObj); // 需编码两次
		pushDataObj = $.base64.encode(pushDataObj);
		form_data += $(thisObj).attr("id") + "=" + pushDataObj + "&";
		formWidgetIds += $(thisObj).attr("id") + ",";
	}
	/* 二级联动至五级联动公用函数,日期区间,日期时间区间 */
	function contactSelect(thisObj, thisId, element) {
		var checkArrVal = "";
		$(thisObj).find(element + "[id^=" + thisId + "]").each(function() {
			var checkedInput = $(this).val();
			if (checkedInput == undefined || checkedInput == "")
				return;
			var base64Code = $.base64.encode(checkedInput);
			checkArrVal += base64Code + ",";
		});
		if (checkArrVal.indexOf(",") > -1) {
			if (checkArrVal.lastIndexOf(",") > -1) {
				checkArrVal = checkArrVal.substr(0, checkArrVal
						.lastIndexOf(","));
			}
		}
		form_data += thisId + "=" + checkArrVal + "&";
		formWidgetIds += thisId + ",";
	}
	/* 小数,邮箱,电话,手机 */
	function numberTypePublic(thisObj) {
		form_data += $(thisObj).children().attr("id") + "="
				+ $(thisObj).children().val() + "&";
		formWidgetIds += $(thisObj).children().attr("id") + ",";
	}
	/* 日期,时间,日期时间 */
	function timeTypePublic(thisObj) {
		form_data += $(thisObj).children().children().attr("id") + "="
				+ $(thisObj).children().children().val() + "&";
		formWidgetIds += $(thisObj).children().children().attr("id") + ",";
	}

	// 构建数据并返回
	form_data += "formWidgetIds=" + formWidgetIds;
	return form_data;
};

// 表单校验
function attributeVerifi() {
	// 可见的控件id
	var dataArray = $("#showWidgetIds").val().split(",");
	var intLogic = 0;
	for (var i = 0; i < dataArray.length; i++) {
		var formWidgetId = dataArray[i];
		var widgetType = $("#form-content-div-form").find(".grid-stack-item")
				.eq(i).children(".grid-stack-item-content").attr("type");
		if (widgetType == "text") {
			// 单行文本aljoin-allow-null aljoin-min-length aljoin-max-length
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var minLength = $("#" + formWidgetId).parent().attr(
					"aljoin-min-length"); // 最小长度
			var maxLength = $("#" + formWidgetId).parent().attr(
					"aljoin-max-length"); // 最大长度
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必填项不能为空!";
				promptTion(text);
				return;
			}
			if (minLength != "") {
				if (widgetValue.length < minLength) {
					var text = "最小长度不能小于" + minLength + "!";
					promptTion(text);
					return;
				}
			}
			if (maxLength != "") {
				if (widgetValue.length > maxLength) {
					var text = "最大长度不能大于" + maxLength + "!";
					promptTion(text);
					return;
				}
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "area_text") {
			// 多行文本
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必填项不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "radio") {
			// 单选
			var widgetValue = formWidgetId;
			var radioLogic = $("input:radio[name^=" + widgetValue + "]:checked")
					.val();
			if (radioLogic == undefined) {
				var text = "单选按钮为必选项，请您选择!";
				promptTion(text);
				$("input:radio[name^=" + widgetValue + "]").siblings("span")
						.css("color", "#ff5722");
				return;
			}
			$("input:radio[name^=" + widgetValue + "]").siblings("span").css(
					"color", "#2c3e50");
		} else if (widgetType == "checkbox") {
			// 多选
			var widgetValue = formWidgetId;
			var radioLogic = $(
					"input:checkbox[name^=" + widgetValue + "]:checked").val();
			var isAllowNull = $("input:hidden[value=" + widgetValue + "]")
					.parent("div").attr("aljoin-allow-null");// 是否为空
			if (radioLogic == undefined && isAllowNull == 0) {
				var text = "多选不能为空，请您选择!";
				promptTion(text);
				$("input:checkbox[name^=" + widgetValue + "]").siblings("span")
						.css("color", "#ff5722");
				return;
			}
			$("input:checkbox[name^=" + widgetValue + "]").siblings("span")
					.css("color", "#2c3e50");
		} else if (widgetType == "select") {
			// 下拉框

		} else if (widgetType == "contact_select") {
			// 二级联动
			var widgetIds = formWidgetId;
			var isAllowNull = $("input:hidden[value=" + widgetIds + "]")
					.parent().parent("div").attr("aljoin-allow-null");// 是否为空
			var widgetValue = $("select[id^=" + widgetIds + "]").eq(0).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必选项不能为空!";
				promptTion(text);
				$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
						"#ff5722");
				return;
			}
			$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
					"#cccccc");
		} else if (widgetType == "contact_select_three") {
			// 三级联动
			var widgetIds = formWidgetId;
			var isAllowNull = $("input:hidden[value=" + widgetIds + "]")
					.parent().parent("div").attr("aljoin-allow-null");// 是否为空
			var widgetValue = $("select[id^=" + widgetIds + "]").eq(0).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必选项不能为空!";
				promptTion(text);
				$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
						"#ff5722");
				return;
			}
			$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
					"#cccccc");
		} else if (widgetType == "contact_select_four") {
			// 四级联动
			var widgetIds = formWidgetId;
			var isAllowNull = $("input:hidden[value=" + widgetIds + "]")
					.parent().parent("div").attr("aljoin-allow-null");// 是否为空
			var widgetValue = $("select[id^=" + widgetIds + "]").eq(0).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必选项不能为空!";
				promptTion(text);
				$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
						"#ff5722");
				return;
			}
			$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
					"#cccccc");
		} else if (widgetType == "contact_select_five") {
			// 五级联动
			var widgetIds = formWidgetId;
			var isAllowNull = $("input:hidden[value=" + widgetIds + "]")
					.parent().parent("div").attr("aljoin-allow-null");// 是否为空
			var widgetValue = $("select[id^=" + widgetIds + "]").eq(0).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必选项不能为空!";
				promptTion(text);
				$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
						"#ff5722");
				return;
			}
			$("select[id^=" + widgetIds + "_]").eq(0).css("border-color",
					"#cccccc");
		} else if (widgetType == "number") {
			// 整数
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必填项不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "decimal") {
			// 小数
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "必填项不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "email") {
			// 邮箱
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "邮箱不能为空!";
				promptTion(text);
				return;
			}
			if (isAllowNull == 0) {
				if (!widgetValue
						.match(/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/)) {
					var text = "邮箱格式不正确！请重新输入!";
					promptTion(text);
					return;
				}
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "telephone") {
			// 电话
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "电话不能为空!";
				promptTion(text);
				return;
			}
			if (isAllowNull == 0) {
				if (!/^(\(\d{3,4}\)|\d{3,4}-|\s)?\d{7,14}$/.test(widgetValue)) {
					var text = "电话号码格式不正确！请重新输入!";
					promptTion(text);
					return;
				}
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "mobilephone") {
			// 手机
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "手机号码不能为空!";
				promptTion(text);
				return;
			}
			if (isAllowNull == 0) {
				if (!(/^1(3|4|5|7|8)\d{9}$/.test(widgetValue))) {
					var text = "手机号码格式不正确！请重新输入!";
					promptTion(text);
					return;
				}
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "date") {
			// 日期
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "日期不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "date_period") {
			// 日期区间
			var isAllowNull = $("input:hidden[value=" + formWidgetId + "]")
					.parent().parent().attr("aljoin-allow-null"); // 是否为空
			var widgetValue = "";
			$("input[id^=" + formWidgetId + "_]").each(function() {
				widgetValue = $(this).val();
				if (widgetValue == "" && isAllowNull == 0) {
					$(this).css("border-color", "#ff5722");
				} else {
					$(this).css("border-color", "#cccccc");
				}
			});
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "日期区间不能为空!";
				promptTion(text);
				return;
			}
		} else if (widgetType == "time") {
			// 时间
			var isAllowNull = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "时间不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "datetime") {
			// 日期时间
			var isAllowNull = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "日期时间不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "datetime_period") {
			// 日期时间区间
			var isAllowNull = $("input:hidden[value=" + formWidgetId + "]")
					.parent().parent().attr("aljoin-allow-null"); // 是否为空
			var widgetValue = "";
			$("input[id^=" + formWidgetId + "_]").each(function() {
				widgetValue = $(this).val();
				if (widgetValue == "" && isAllowNull == 0) {
					$(this).css("border-color", "#ff5722");
				} else {
					$(this).css("border-color", "#cccccc");
				}
			});
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "日期时间区间不能为空!";
				promptTion(text);
				return;
			}
		} else if (widgetType == "image") {
			// 图片
			// alert(145);
			// dataSrcPushCheck(formWidgetId,"image-template_","图片不能为空!");
			var thisParenObj = $("#" + formWidgetId).parents(
					"div[id^='image-template_']");
			var isAllowNull = thisParenObj.attr("aljoin-allow-null"); // 是否为空
			var widgetValue = thisParenObj.children(".widget_datasrc_push")
					.size();
			if (isAllowNull == 0 && widgetValue == 0) {
				layer.alert("图片不能为空!", {
					icon : 5,
					title : '提示'
				});
				thisParenObj.find(".kv-fileinput-caption").css("border-color",
						"#ff5722");
				return;
			}
			thisParenObj.find(".kv-fileinput-caption").css("border-color",
					"#cccccc");
		} else if (widgetType == "attach") {
			// 附件
			// dataSrcPushCheck(formWidgetId,"attach-template_","附件不能为空!");
			var thisParenObj = $("#" + formWidgetId).parents(
					"div[id^='attach-template_']");
			var isAllowNull = thisParenObj.attr("aljoin-allow-null"); // 是否为空
			var widgetValue = thisParenObj.children(".widget_datasrc_push")
					.size();
			if (isAllowNull == 0 && widgetValue == 0) {
				layer.alert("附件不能为空!", {
					icon : 5,
					title : '提示'
				});
				thisParenObj.find(".kv-fileinput-caption").css("border-color",
						"#ff5722");
				return;
			}
			thisParenObj.find(".kv-fileinput-caption").css("border-color",
					"#cccccc");
		} else if (widgetType == "datagrid") {
			// 数据表格
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			/*
			 * if(isAllowNull == 0){ var logicNum="";
			 * $("#"+formWidgetId).find("tbody tr").each(function(){
			 * $(this).find("input").each(function(){ if($(this).val() == ""){
			 * $(this).css("border-color","#ff5722"); logicNum = 0; }else{
			 * $(this).css("border-color","#cccccc"); logicNum = 1; } }); });
			 * if(logicNum == 0){ var text ="表格不能为空!"; promptTion(text); return; } }
			 */

		} else if (widgetType == "editor") {
			// 文本编辑器

		} else if (widgetType == "href") {
			// 网址
			var isAllowNull = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-allow-null"); // 是否为空
			var minLength = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-min-length"); // 最小长度
			var maxLength = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-max-length"); // 最大长度
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "网址不能为空!";
				promptTion(text);
				return;
			}
			if (minLength != "") {
				if (widgetValue.length < minLength) {
					var text = "最小长度不能小于" + minLength + "!";
					promptTion(text);
					return;
				}
			}
			if (maxLength != "") {
				if (widgetValue.length > maxLength) {
					var text = "最大长度不能大于" + maxLength + "!";
					promptTion(text);
					return;
				}
			}
			if (widgetValue.length != 0) {
				if (!/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/.test(widgetValue)) {
					var text = "网址格式不正确！请重新输入!";
					promptTion(text);
					return;
				}
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "biz_title") {
			// 标题
			var isAllowNull = $("#" + formWidgetId).parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "标题不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "biz_finish_time") {
			// 办结时间
			var isAllowNull = $("#" + formWidgetId).parent().parent().attr(
					"aljoin-allow-null"); // 是否为空
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == 0 && widgetValue.length == 0) {
				var text = "办结时间不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		} else if (widgetType == "biz_text_office") {
			// 文件正文
			var isAllowNull = $("#attachId").val(); // 是否为空
			var isSureBuild = $("#" + formWidgetId).parent().attr(
					"aljoin-office-sure-build"); // 是否新建
			var widgetValue = $("#" + formWidgetId).val();
			if (isAllowNull == "" && isSureBuild == 1) {
				var text = "文件正文不能为空!";
				promptTion(text);
				return;
			}
			$("#" + formWidgetId).css("border-color", "#cccccc");
		}
		intLogic = 1;
	}
	// 验证样式
	function promptTion(text) {
		layer.alert(text, {
			icon : 5,
			title : '提示'
		});
		$("#" + formWidgetId).css("border-color", "#ff5722");
	}
	;
	// 图片附件校验
	function dataSrcPushCheck(thisObjId, element, text) {
		var liclog = 1;
		var thisParenObj = $("#" + thisObjId).parents(
				"div[id^='" + element + "']");
		var isAllowNull = thisParenObj.attr("aljoin-allow-null"); // 是否为空
		var widgetValue = thisParenObj.children(".widget_datasrc_push").size();
		if (isAllowNull == 0 && widgetValue == 0) {
			layer.alert(text, {
				icon : 5,
				title : '提示'
			});
			thisParenObj.find(".kv-fileinput-caption").css("border-color",
					"#ff5722");
			liclog = 0;
		}
		thisParenObj.find(".kv-fileinput-caption").css("border-color",
				"#cccccc");
		return liclog;
	}

	return intLogic;
};
function changeRightNow4Text(thisObj) {
	// $(thisObj).attr('value',$(thisObj).val());
	$(thisObj).css("border-color", "#ccc");
	$(thisObj).parent().attr('aljoin-default-value', $(thisObj).val())
}
/* 意见域勾选 */
function spanTextOpinion(thisObj) {
	var opinionTexteLement = $("#opinion_textelement").val();
	if (opinionTexteLement == "") {
		$(thisObj).addClass("glyphicon-ok add_text");
		$(thisObj).parents("tr").siblings().find("span").removeClass(
				"glyphicon-ok add_text");
	} else {
		layer.alert("意见您已自定义，请勿重复选择!", {
			icon : 5,
			title : '提示'
		});
	}
}
/* 勾选意见域或编辑 */
function storageDataT() {
	var opinionTexteLement = $("#opinion_textelement").val();
	var thisText = "";
	if (opinionTexteLement == "") {
		thisText = $("#style_xh_numtr").find(".add_text").parent().siblings()
				.text();
	} else {
		thisText = opinionTexteLement;
	}
	var commentWidgetIds = $("#commentWidgetIds").val();
	var commentWidgetIdArr = commentWidgetIds.split(",");
	var plusText = "";
	for (var i = 0; i < commentWidgetIdArr.length; i++) {
		var plusText = $("#orgnl_" + commentWidgetIdArr[i]).val();
		if (plusText == "" || plusText == undefined) {
			$("#" + commentWidgetIdArr[i]).val(thisText);
		} else {
			$("#" + commentWidgetIdArr[i]).val(plusText + "\n" + thisText);
		}
	}
	$("#topButtonComment").val(thisText);
}
function setWidgetTextMain() {
	$("#style_xh_numtr").children("tr").find("span").removeClass(
			"glyphicon-ok add_text");
}

// 解析文件正文
function parserTextOfficeWidget() {
	$("div[id^='biz_text_office-template_']").each(
			function() {
				var onPattern = $(this).attr("aljoin-office-on-pattern"); // 打开模式
				// var sureBuild = $(this).attr("aljoin-office-sure-build");
				// //是否新建
				var widgetId = $(this).children("input").attr("id");
				var attachIdVal = $("#orgnl_" + widgetId).val();
				if (attachIdVal == undefined || attachIdVal == "") {
					if (onPattern != 1) {
						$(this).children("input[type='button']").attr(
								"onclick", "fileNnllErr()");
						return false;
					}
				}
				if (attachIdVal != undefined) {
					$("#attachId").val(attachIdVal);
				}
				var onButtonText = "";
				var onButtonEvent = "";
				var onButtonUrl = "";
				if (onPattern == 1) {
					// 新建
					onButtonText = "文件正文";
					onButtonEvent = "newWord";
				} else if (onPattern == 2) {
					// 只读
					onButtonText = "文件正文";
					onButtonEvent = "viewWord";
					onButtonUrl = "'fw/test.doc'";
				} else if (onPattern == 3) {
					// 保留痕迹
					onButtonText = "文件正文";
					onButtonEvent = "revisionWord";
					onButtonUrl = "'fw/test.doc'";
				} else if (onPattern == 4) {
					// 编辑
					onButtonText = "文件正文";
					onButtonEvent = "editWord";
					onButtonUrl = "'fw/test.doc'";
				} else if (onPattern == 5) {
					// 红头文件
					onButtonText = "文件正文";
					onButtonEvent = "redTitle";
					onButtonUrl = "";
				}
				$(this).children("input[type='button']").val(onButtonText);
				$(this).children("input[type='button']").attr("onclick",
						onButtonEvent + "(" + onButtonUrl + ")");
			});
}
// 文件为创建并且该节点无权创建提示错误
function fileNnllErr() {
	layer.alert('文件未创建，并且您无权限创建！', {
		icon : 7,
	})
}
// 查看word
function viewWord(wordUrl) {
	POBrowser.openWindow('/pag/pageOffice/poBrowser?openModeType=1&attachId='
			+ $("#attachId").val()
			+ '&isNewWord=0&resourceId=123&redTitle=0&handSign=0',
			'width=1200px;height=800px;');
}
// 编辑
function editWord(wordUrl) {
	POBrowser.openWindow('/pag/pageOffice/poBrowser?openModeType=2&attachId='
			+ $("#attachId").val()
			+ '&isNewWord=0&resourceId=123&redTitle=0&handSign=0',
			'width=1200px;height=800px;');
}
// 痕迹保留
function revisionWord(wordUrl) {
	POBrowser.openWindow('/pag/pageOffice/poBrowser?openModeType=3&attachId='
			+ $("#attachId").val()
			+ '&isNewWord=0&resourceId=123&redTitle=0&handSign=0',
			'width=1200px;height=800px;');
}
// 新建页面
function newWord() {
	POBrowser.openWindow('/pag/pageOffice/poBrowser?openModeType=3&attachId='
			+ $("#attachId").val()
			+ '&isNewWord=1&resourceId=123&redTitle=0&handSign=0',
			'width=1200px;height=800px;');
}
// 红头文件
function redTitle() {
	POBrowser.openWindow('/pag/pageOffice/poBrowser?openModeType=3&attachId='
			+ $("#attachId").val()
			+ '&isNewWord=0&resourceId=123&redTitle=1&handSign=0',
			'width=1200px;height=800px;');
}

// 给子窗调用，返回附件id
function setAttachId(attachId) {
	if (attachId != '') {
		$("#attachId").val(attachId.replace("_Str", ""));
	}
}
// 意见列表接口
function options() {
	var _csrf = $("#_csrf").val();
	var pm = {
		_csrf : _csrf,
		isAll : 0,
	};
	tool.post("/ioa/ioaWaitWork/fillOpinion", pm, function(retMsg) {
		if (null != retMsg) {
			var fileBack = retMsg;
			var opinionlist = {
				opinionlist : fileBack
			};
			$('#opinion-data').html(template("opinion-script", opinionlist));
		}
	}, false);
}

// 查看流程图
$("#aljoin-task-flowchart")
		.on(
				"click",
				function() {
					var processInstanceId = $("#processInstanceId").val();
					var activityId = $("#activityId").val();
					var content = "";
					if (processInstanceId != '') {
						content = '<img src="/ioa/ioaWaitWork/showImg?processInstanceId='
								+ processInstanceId + '" />'
					}
					if (activityId != '') {
						content = '<img src="/act/actFixedForm/showImg?taskId='
								+ taskId + '" />'
					}
					var index = layer.open({
						title : '流程图',
						maxmin : false,
						type : 1,
						content : content
					});
					pform.render();
					layer.full(index);
				});

// 流转日志
$("#aljoin-task-circulation-log").on("click", function() {
	var processInstanceId = $("#processInstanceId").val();
	var activityId = $("#activityId").val();
	var param = {};
	if (processInstanceId != '') {
		param.processInstanceId = processInstanceId;
	}
	if (activityId != '') {
		param.taskId = activityId;
	}
	param._csrf = $("#_csrf").val();
	tool.post("/act/actFixedForm/getLog", param, function(data) {
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
});
// 图片附件回调
function attachSrcPushMore(thisObj) {
	$(thisObj).parent("div").hide();
	$(thisObj).parent("div").addClass("on");
	$(thisObj).siblings("div").show();
	$(thisObj).attr("onclick", "attachSrcPushOff(this)");
	$(thisObj).text("关闭");
	$(thisObj).parent("div").show('300');
	$(thisObj).parents(".grid-stack-item").css('z-index', 999);
}
function attachSrcPushOff(thisObj) {
	$(thisObj).parent("div").hide();
	$(thisObj).parent("div").removeClass("on");
	$(thisObj).siblings(".head-title").hide();
	$(thisObj).attr("onclick", "attachSrcPushMore(this)");
	$(thisObj).text("更多");
	$(thisObj).parent("div").show('300');
	$(thisObj).parents(".grid-stack-item").css('z-index', '');
	var isThisPSize = $(thisObj).siblings(".min-content").children("p").size();
	if (isThisPSize < 4) {
		$(thisObj).hide();
	}
}
// 判断记录是否 签收
function checkClaim() {
	var processInstanceId = $("#processInstanceId").val();
	var pm = {
		"processInstanceId" : processInstanceId,
		"_csrf" : _csrf
	};
	tool.post("/act/actFixedForm/isClaim", pm, function(retMsg) {
		if (retMsg.code == 2) {// 流程已经被签收
			$("#aljoin-task-revoke").hide();
		}
		if (retMsg.code == 3) {// 流程没有被签收
			$("#aljoin-task-revoke").show();
		}
		// 如果是流转监控过来的 就隐藏撤回按钮
		if ($("#isMonitor").val() == 1) {
			$("#aljoin-task-revoke").hide();
		}
	}, false);
}
// 传阅
$("#aljoin-task-circula").on("click", function() {
	var processInstanceId = $("#processInstanceId").val();
	var param = {};
	var activityId = $("#activityId").val();
	if (activityId != '') {
		param.taskId = activityId;
	}
	if (processInstanceId != '') {
		param.processInstanceId = processInstanceId;
	}
	param.sendIds = "940770372280823809;";
	param._csrf = $("#_csrf").val();
	tool.post("/ioa/ioaWaitWork/circula", param, function(data) {
		if (data.code == 0) {
			// tool.success(retMsg.message);
			layer.msg('传阅成功！', {
				icon : 1,
				time : 1500
			// 2秒关闭（如果不配置，默认是3秒）
			}, function(index) {
				parent.location.reload();
			})
		} else {
			tool.error(data.message);
		}
	}, false);
});
// 撤回
$("#aljoin-task-revoke").on(
		"click",
		function() {
			var processInstanceId = $("#processInstanceId").val();
			var activityId = $("#activityId").val();
			var bpmnId = $("#bpmnId").val();
			var pm = {
				taskId : activityId,
				_csrf : _csrf,
				bpmnId : bpmnId
			};
			// 获取上级节点（以谁提交过了为主）
			tool.post("/act/actFixedForm/getPreTaskInfo2", pm,
					function(retMsg) {
						if (retMsg.code != 0) {
							tool.error(retMsg.message);
						} else {
							var param = {
								"processInstanceId" : processInstanceId,
								"taskId" : activityId,
								_csrf : _csrf
							};
							tool.post("/act/actFixedForm/revoke", param,
									postCallBackPubInfo, false);
						}
					}, false);
		});

// 打开传阅操作
function openCircula() {
	var processInstanceId = $("#processInstanceId").val();
	var param = new Object();
	param.processInstanceId = processInstanceId;
	param._csrf = $("#_csrf").val();
	tool.post("/ioa/ioaCircula/openCirculaWork", param, function(retMsg) {
		if(retMsg.code == 0){
			var object = retMsg.object;
			if(object.isCirculate == 1){ // 是否是传阅件
				$("#aljoin-task-circulation-opinion").show();
				if(object.isOver == 1){//流程流转已结束
					$("#aljoin-task-circulation").hide();
					$("#aljoin-task-circulaOpi").hide(); // 填写传阅意见按钮
				}else if(object.isread == 1){
					$("#aljoin-task-circulaOpi").hide()
				}else if(object.isread == 0){
					$("#aljoin-task-circulaOpi").show()
				}
			}else{
				$("#aljoin-task-circulaOpi").hide(); // 填写传阅意见按钮
				$("#aljoin-task-circulation-opinion").hide();
			}
		}
	}, false);

}
function circulation() {
	var sendIds = $("#circulation_sendId").val();
	if (sendIds == "") {
		layer.msg("请选择传阅对象");
		return;
	}
	var processInstanceId = $("#processInstanceId").val();
	var param = {};
	var activityId = $("#activityId").val();
	if (activityId != '') {
		param.taskId = activityId;
	}
	if (processInstanceId != '') {
		param.processInstanceId = processInstanceId;
	}
	param.sendIds = sendIds;
	param._csrf = $("#_csrf").val();
	tool.post("/ioa/ioaWaitWork/circula", param, function(retMsg) {
		if (retMsg.code == 0) {
			// tool.success(retMsg.message);
			layer.msg('传阅成功！', {
				icon : 1,
				time : 1500
			// 2秒关闭（如果不配置，默认是3秒）
			}, function(index) {
				parent.location.reload();
			})
		} else {
			tool.error(retMsg.message);
		}
	}, false);
}