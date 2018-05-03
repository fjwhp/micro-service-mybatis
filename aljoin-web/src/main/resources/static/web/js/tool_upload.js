
var arr=[],arrImage=[];
var nameArr=[],nameArrImage=[];
//人员选择弹窗
var index1;

var tool = {
		
 containerTree:"",
 treeDiv:"",
 truediv:"",
 showname:"",
 appendiv:"",
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
		//document.getElementById(param.container + "-data").innerHTML = template(param.container + "-script", pageData);
		$("#"+param.container + "-data").html(template(param.container + "-script", pageData));
		if(pageData.records.length == 0){
			//如果没有数据，合并显示一行"暂无数据"
			//列头数
			var thCount = $("#"+param.container+"-data").prev().find("th").length;
			var thCountHidden = $("#"+param.container+"-data").prev().find("th[style='display: none;']").length;			
			var thCountShow = thCount - thCountHidden;
			var blankTrTd = "<tr><td style=\"text-align:center;font-size:14px;\" colspan=\""+thCountShow+"\">暂无数据</td></tr>";
			//document.getElementById(param.container + "-data").innerHTML = blankTrTd;
			$("#"+param.container + "-data").html(blankTrTd);
		}
		// 重新加载元素样式
		layui.use('form', function() {
			var form = layui.form;
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
		$(".layui-laypage-pagesize").val(param.pageSize);
		$(".layui-laypage-pagesize").change(function(){
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
		});6
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
			closeBtn:0
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
	deleteByIds : function(url, id) {
		var obj;
		var param = new Object();
		param._csrf = $("#_csrf").val();
		param.ids = id;
		tool.post(url, param, function(o) {
			obj = o;
		}, false);
		return obj;
	},
	selectLinkTagByPid : function(elementId,topElementId,data){
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
					var form = layui.form;
					    form.render();
				});
			}
		}, false);
	},
	selectLinkTagByPid2 : function(elementId,topElementId,id){
		var param = new Object();
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
				var str='<div class="layui-input-inline  nextselects">'+o.message+'</div>'
				$("#"+topElementId).append(str);
			}
		}, false);
	},
	getfiletiao:function(url){
		 var obj=new Object()
		    tool.post(url,{_csrf:$("#_csrf").val()},function(data){
		    	obj.allowType = data.allowType.substr(1)
		    	obj.limitSize = Number(data.limitSize)
		     },false)
		     return obj
	}
	,
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
	timeParse:function(time){
		var start = {
	 		      elem: time,
	 		      format:'YYYY-MM-DD hh:mm',
	 		      istime: true,
	 		      istoday: true
	 		    };
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
	linkJump2:function (href1,href2,icon,title){
		//parent.tab.deleteTab(parent.tab.getCurrentTabId());
		//跳转到协同办公--待办工作
		parent.tab.tabAdd({
		  	href : href1,
		  	icon : icon,
		  	title : title
		});		
		//location.href="/per/autMsgOnline/autMsgOnlinePage"
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
	fileUpload:function(body,cases,url,bindAction,fileModuleName){
		  //body   放文件的容器
		  //cases  点击文件上传的区域
		  // url  接口地址
		  //fileModuleName 文件所属模块
		 // bindAction  手动上传触发的按钮
      var fileData = tool.getfiletiao('../../util/utilController/getAllowType') //调取后台文件格式 大小数据
      layui.use('upload', function(){
          var upload = layui.upload;
          //多文件列表示例
          var demoListView = $('#'+body)
              ,uploadListIns = upload.render({
               elem: '#'+cases
              ,url: url
              ,accept: 'file'//接收文件
            //  ,exts:fileData.allowType//接收文件后缀
              ,method: 'post'
              ,size:fileData.limitSize*1024   //文件大小  kb
              ,data:{_csrf:$("#_csrf").val(),allowType:fileData.allowType,limitSize:fileData.limitSize,fileModuleName:fileModuleName}//上传的参数
              ,multiple: true//是否多文件上传
              ,auto: false//设置是否自动上传(true)   false需要指定一个bindAction按钮来点击上传
              ,bindAction: '#'+bindAction
              ,choose: function(obj){ //选中玩文件调用
              	 var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
                  //读取本地文件
                  obj.preview(function(index, file, result){
                  	//fileSize:Math.round(file.size/1024 * 10) / 10
                      nameArr.push({fileName:file.name,index:index,fileSize:Math.ceil((file.size/1014).toFixed(1))})
                       var tr = $(['<tr id="upload-'+ index +'">'
                          ,'<td>'+ file.name +'</td>'
                          ,'<td>'+ Math.ceil((file.size/1014).toFixed(1)) +'kb</td>'
                          ,'<td>等待上传</td>'
                          ,'<td>'
                          ,'<a class="demo-delete" style="color:#33a1ff;cursor:pointer;" index="'+index+'">删除</a>'
                          ,'</td>'
                          ,'</tr>'].join(''));                    
                      //单个重传
                      tr.find('.demo-reload').on('click', function(){
                          obj.upload(index, file);
                          return false;
                      });
                      //删除
                      tr.find('.demo-delete').on('click', function(){
                      	var index = $(this).attr("index");
                      		for(var i=0;i<nameArr.length;i++){
                      			if(nameArr[i].index==index){
                      				nameArr.splice(i,1)
                      			}
                      		}
                      		for(var i=0;i<arr.length;i++){
                      			if(arr[i].index==index){
                      				arr.splice(i,1)
                      			}
                      		}
                         delete files[index]; //删除对应的文件
                          tr.remove();
                          uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                          return false; 
                      });
                      //上传成功的 要删除对应的文件
                      /*$("#testListAction").on("click",function(){
                          delete files[index];
                      })*/
                      demoListView.append(tr);
                  });
              }
              ,allDone:function(obj){
              	var newObjArr = new Array();
              	for(var i=0;i<nameArr.length;i++){
              		var newObj = new Object();
              		for(var j=0;j<arr.length;j++){
              			if(nameArr[i].index==arr[j].index){
              				newObj.res = arr[j].res;
              				newObj.index = arr[j].index;
              				newObjArr.push(newObj)
              			}
              		}
              	}
              	
              	arr=newObjArr;
              }
              ,done: function(res, index, upload){//开始上传到接口调用
              	if(res.code!=0){
              		tool.error(res.message)
              	}
                  //res 后台返回给前台的数据
                  var item = this.item;
                  if(res.code==0){ //上传成功c
                      arr.push({res:res.object[0],index:index})
                      //延时改变状态为了防止 出现等待上传的情况
                      setTimeout(function(){
                      	  var tds = demoListView.find('tr#upload-'+ index).children();
                            tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                      },500)
                      delete this.files[index];
                      return;
                  }
                  this.error(index, upload);
              }
            
              ,error: function(index, upload){
              	    //如果上传失败 删除源文件名数组中的这项目
              		for(var i=0;i<nameArr.length;i++){
              			if(nameArr[i].index==index){
              				nameArr.splice(i,1);
              			}
              		}
                  setTimeout(function(){	
	                    var tr = demoListView.find('tr#upload-'+ index)
	                        ,tds = tr.children();
	                    tds.eq(2).html('<span style="color: #FF5722;">上传失败</span>');
	                    tds.eq(3).find('.demo-reload').removeClass('layui-hide'); //显示重传
                  },500)
              }
          });
          
      });
	},
    //表单导入
	fileUploadForm:function(body,cases,url,bindAction){
		  //body   放文件的容器
		  //cases  点击文件上传的区域
		  // url  接口地址
		 // bindAction  手动上传触发的按钮
      var fileData = tool.getfiletiao('../../util/utilController/getAllowType') //调取后台文件格式 大小数据
      layui.use('upload', function(){
          var upload = layui.upload;
          //多文件列表示例
          var demoListView = $('#'+body)
              ,uploadListIns = upload.render({
               elem: '#'+cases
              ,url: url
              ,accept: 'file'//接收文件
              ,method: 'post'
              ,size:fileData.limitSize*1024   //文件大小  kb
              ,data:{_csrf:$("#_csrf").val(),allowType:fileData.allowType,limitSize:fileData.limitSize,formName:$("#formName").val(),categoryId:$("#categoryId").val()}//上传的参数
              ,multiple: false//是否多文件上传
              ,auto: false//设置是否自动上传(true)   false需要指定一个bindAction按钮来点击上传
              ,bindAction: '#'+bindAction
              ,number:1
              ,choose: function(obj){ //选中玩文件调用
              	 //var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
              	 demoListView.children("tr").remove();
                  //读取本地文件
                  obj.preview(function(index, file, result){ 
                       var tr = $(['<tr id="upload-'+ index +'">'
                          ,'<td>'+ file.name +'</td>'
                          ,'<td>'+ Math.ceil((file.size/1014).toFixed(1)) +'kb</td>'
                          ,'<td>等待上传</td>'
                          ,'<td>'
                          ,'<a class="demo-delete" style="color:#33a1ff;cursor:pointer;" index="'+index+'">删除</a>'
                          ,'</td>'
                          ,'</tr>'].join(''));                    
                      //单个重传
                      tr.find('.demo-reload').on('click', function(){
                          obj.upload(index, file);
                          return false;
                      });
                      //删除
                      tr.find('.demo-delete').on('click', function(){
                      	var index = $(this).attr("index");
                      		for(var i=0;i<nameArr.length;i++){
                      			if(nameArr[i].index==index){
                      				nameArr.splice(i,1)
                      			}
                      		}
                      		for(var i=0;i<arr.length;i++){
                      			if(arr[i].index==index){
                      				arr.splice(i,1)
                      			}
                      		}
                        // delete file[index]; //删除对应的文件
                          tr.remove();
                          uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                          	var filename=file.name.split(".")[0] //上传文件的文件名
		              		if($("#fileName").val()==filename.replace(/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g,'')){
								$("#fileName").val("");  //同时清除表单名称
		              		}
                          
                          $("#filePath").val("");  //清除临时文件路径
                          return false; 
                      });
                      demoListView.append(tr);
                  });
              },done: function(res, index, upload){//开始上传到接口调用
            	  console.log(res, index, upload)
              	if(res.code!=0){
              		tool.error(res.message)
              	 }else{
              		 //成功操作
              		var path = res.object.path;
              		var fileName = $("#fileList tr td:eq(0)").text().split(".")[0];
              		var fileNameWrite = $("#fileName").val();
              	  console.log(res.object)
              		if($("#fileName").val()==""){//表单名称为空时
              			 if(fileName!="") {
	                  		$("#fileName").val(fileName.replace(/[^\a-\z\A-\Z0-9\u4E00-\u9FA5]/g,''));
	              		 }
              		}
              		else if($("#fileName").val() !=""){
              			$("#fileName").val(fileNameWrite);
              		}
              		
              		$("#filePath").val(path);
              	 }
                //res 后台返回给前台的数据
                var item = this.item;
                if(res.code==0){ //上传成功c
                    arr.push({res:res.object[0],index:index})
                    //延时改变状态为了防止 出现等待上传的情况
                    setTimeout(function(){
                    	  var tds = demoListView.find('tr#upload-'+ index).children();
                          tds.eq(2).html('<span style="color: #5FB878;">上传成功</span>');
                    },500)
                    //delete this.files[index];
                    return;
                }
              },error: function(index, upload){
              }
          });
          
      });
    },      
    imageUpload:function(elem,url,bindAction,yuimage,fileModuleName){
    	layui.use('upload', function(){
          var upload = layui.upload;
    	 //普通图片上传
		  var uploadInst = upload.render({
		    elem: '#'+elem
		    ,url: url
		    ,multiple: false//是否多文件上传
		    ,data:{_csrf:$("#_csrf").val(),fileModuleName:fileModuleName}
		    ,before: function(obj){
		      obj.preview(function(index, file, result){
		    	nameArrImage=[];
		    	arrImage=[];
		    	nameArrImage.push({fileName:file.name,index:index,fileSize:Math.ceil((file.size/1014).toFixed(1))})
		    	$("#"+bindAction).val(file.name)
		        $('#'+yuimage).attr('src', result); //图片链接（base64）
		      });
		    }
		    ,done: function(res,index){
		      //如果上传失败
		      if(res.code > 0){
		        $("#"+bindAction).val("");
		        return layer.msg('上传失败');
		      }
		      //上传成功
		      arrImage.push({res:res.object[0],index:index})
		    }
		    ,allDone:function(obj){
		    	var newObjArr = new Array();
           	for(var i=0;i<nameArrImage.length;i++){
           		var newObj = new Object();
           		for(var j=0;j<arrImage.length;j++){
           			if(nameArrImage[i].index==arrImage[j].index){
           				newObj.res = arrImage[j].res;
           				newObj.index = arrImage[j].index;
           				newObjArr.push(newObj)
           			}
           		}
           	}
           	arrImage=newObjArr;
		    },error: function(index, upload){
       	    //如果上传失败 删除源文件名数组中的这项目
       		for(var i=0;i<nameArr.length;i++){
       			if(nameArr[i].index==index){
       				nameArr.splice(i,1);
       			}
       		}
           }
		  });
    	})	  
 },
    /**
	 * 人员选择树
     * @param containerTree  树弹窗ID
     * @param treeDiv        树ID
     * @param truediv        隐藏 树选中元素ID
     * @param showname       显示 树选中元素值（用于input 元素赋值）
     * @param showname_html  显示 树选中元素值（用于div 元素赋值）
     * @param isSingleCheck  是否单选（true:单选 false:多选）
     * @param deptName       搜索关键字 部门名称(多个用分号分隔)
     * @param departmentId   搜索关键字 部门ID(多个用分号分隔)
     * @param fullName       搜索关键字 用户姓名(多个用分号分隔)
	 * @param appendiv
     * @param userIds        搜索关键字 用户Id(多个用分号分隔)
     */
    treeDemo:function(containerTree,treeDiv,truediv,showname,showname_html,isSingleCheck,deptName,departmentId,fullName,appendiv,userIds){
     $("#peopelSel").val("")	
	 tool.containerTree=containerTree;
	 tool.treeDiv=treeDiv;
	 tool.truediv=truediv;
	 tool.showname=showname;
	 tool.showname_html=showname_html;
	 tool.appendiv=appendiv;
	  index1 = layer.open({
          title : '人员选择',
          maxmin : false,
          type : 1,
          shadeClose: true,
          area: ['360px', '500px'],
          content : $('#'+containerTree)
      });
     /*  layer.full(index); */
      tool.deptUserTree(treeDiv,truediv,showname,showname_html,isSingleCheck,deptName,departmentId,fullName,"",userIds);
      var objIds = $("#"+truediv).val();
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
deptUserTree:function(treeDiv,truediv,showname,showname_html,isSingleCheck,deptName,departmentId,fullName,appendiv,userIds){
    var param = {};
	param._csrf = $("#_csrf").val();
	if(deptName != ''){
        param.deptNames = deptName;
	}
    if(fullName != ''){
        param.fullName = fullName;
    }
    if(departmentId != ''){
        param.departmentIds = departmentId;
    }
    if(userIds != ''){
        param.userIds = userIds;
    }
	tool.post("/pub/public/organList",param,function(data){
        var  departmentList=data.departmentList;//部门
        var  userPositionList=data.departmentUserList;//用户
        var setting = null;
        if(isSingleCheck){
        	setting = {
                    check : {
                        enable : true,
                        chkboxType : { "Y" : "", "N" : "" }
                    },
                    data : { simpleData : { enable : true }  },
                    callback: {
                    	beforeCheck: function(){$.fn.zTree.getZTreeObj(treeDiv).checkAllNodes(false);},
                        onCheck: function() {
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
                    }
                };
        }else{
        	setting = {
                    check : {
                        enable : true,
                        chkStyle: "checkbox"
                    },
                    data : { simpleData : { enable : true }  },
                    callback: {
                        onCheck: onCheck
                    }
                };
        }
        
        var obj=[];
        for(var i=0;i<departmentList.length;i++){
            obj.push({id:departmentList[i].id,pId: departmentList[i].parentId,name:departmentList[i].deptName,necessary:0})
            if(userPositionList != null){
	        	 for(var j=0;j<userPositionList.length;j++){
	                 if(userPositionList[j].deptId==departmentList[i].id){
	                     obj.push({id:userPositionList[j].id,pId:departmentList[i].id,name:userPositionList[j].fullName,necessary:1,userId:userPositionList[j].userId,userName:userPositionList[j].userName })
	                 }
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
	 Hours=date.getHours();
	 Minutes=date.getMinutes();
	 Seconds=date.getSeconds();
	 if(showMonth<10){
    	 showMonth="0"+showMonth;
     }
	 if(showday<10){
		 showday="0"+showday;
     }if(Hours<10){
    	 Hours="0"+Hours;
     }if(Minutes<10){
    	 Minutes="0"+Minutes;
     }if(Seconds<10){
    	 Seconds="0"+Seconds;
     }
	 var time = Hours + ":" + Minutes + ":" + Seconds;
	 var Dates = showYear+"-"+showMonth+"-"+showday;
	 arr.push(time);
	 arr.push(Dates);
	 return arr;
},
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
    if(treeArr.length>0){
		 var str="",namestr="",fullname="";
		 var divs="";
		$.each(treeArr,function(index,value){ 
			str+=value.userId+";"
			namestr+=value.userName+";"
			fullname+=value.name+";";
			divs+='<div class="layui-input-inline" style="position: relative;float:none;width:auto;margin-right: 10px;">'+
			 '<i class="layui-icon" onclick="removeElement(this)" strid="'+value.userId+'" namestr="'+value.userName+'"  fullname="'+value.name+'"   style="cursor: default; font-size:16px; color: #d9d9d9;position: absolute;right: -8px;z-index: 10; ">&#x1007;</i>'+  
	    	 '<a class="layui-btn" style="cursor: pointer;color: black; background: none;height:26px;line-hieght:26px;padding-left:6px;padding-right:6px" >'+value.name+'</a>'+
	    	 '</div>'	    		
		})
   	  }
	  /*  var choose=$("#hiddenPp").val()
		if(choose==0){
		$("#add-object-form").find("#receiveUserId").val(str);
		$("#add-object-form").find("#receiveUserName").val(namestr);
		$("#add-object-form").find("#receiveFullName").val(fullname);*/
		/*$("#win-director-object").find(".test").empty();
		$("#win-director-object").find(".test").append(divs);
		
		$("#win-director-object").find("#"+changeName).empty();
		$("#win-director-object").find("#"+changeName).append(divs);*/
		
	/*	$("#receiveUserCount").val(treeArr.length)
	
		}else{
			$("#add-object-form").find("#copyUserIds").val(str);
			$("#add-object-form").find("#copyUserNames").val(namestr);
			$("#add-object-form").find("#copyFullNames").val(fullname);
			$("#add-object-form").find(".newgg1").empty();
			$("#add-object-form").find(".newgg1").append(divs);
		}*/
	  $(".sure_s").click(function(){
		  $("#"+tool.appendiv).empty(); 
		  $("#"+tool.appendiv).append(divs)
		  $("#"+tool.truediv).val(uId);
		  $("#"+tool.showname).val(fullname);
		  $("#"+tool.showname_html).html(fullname);
		  layer.close(index1);
	  })
}

//移除
   function removeElement(tis) {
      var parent=$(tis).parent().parent().parent();
      var id=$(tis).attr("strid")+";";
      var namestr=$(tis).attr("namestr")+";";
      var fullname=$(tis).attr("fullname")+";";
      
      var receiveFullName = parent.find("input").eq(0).val();
      var fullStr = receiveFullName.replace(fullname,"");
            parent.find("input").eq(0).val(fullStr);
          
       var receiveUserName =  parent.find("input").eq(1).val();
       var userStr = receiveUserName.replace(namestr,"");
       parent.find("input").eq(1).val(userStr);   
       
      var ids =  parent.find("input").eq(2).val();
      var pmnIdStr = ids.replace(id,"");
      parent.find("input").eq(2).val(pmnIdStr);
      pmnIdStr=pmnIdStr.substr(0,pmnIdStr.length-1).split(";");
	   $(tis).parent().remove();

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
		  loadData_log();
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
//截取地址栏参数
function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
//li下拉按钮
function click_li(myself){  
   $(".nav_child").hide();
   $(myself).parent().find(".nav_child").show();
}   
function click_liout(myself){  
   $(myself).hide();
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
	   	$("#number").html("")
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


