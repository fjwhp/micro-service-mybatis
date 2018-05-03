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
					'color' : 'gray',
					'border' : '1px solid gray'
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