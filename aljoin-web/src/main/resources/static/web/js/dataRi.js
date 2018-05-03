var calUtil = {
   dataU:null,		
   namearr:[],
  //当前日历显示的年份
  showYear:2015,
  //当前日历显示的月份
  showMonth:1,
  //当前日历显示的天数
  showDays:1,
  eventName:"load",
  id:null,
  //初始化日历
  init:function(){
    calUtil.setMonthAndDay();
    calUtil.draw();
    calUtil.bindEnvent();
  },
  draw:function(id){
  	  $("#calendar").html("");
    //绑定日历
    var str = calUtil.drawCal(calUtil.showYear,calUtil.showMonth);
    $("#calendar").html(str);
     $(".samecalss").each(function(index,value){
    	    var id=value.id;
    	    var that=$(this);
    		 $.each(calUtil.namearr,function(ind,val){
    			 if(id==val.signDate){
    				  //填充数据
    				 that.attr("ids",val.id);
    				
    				 
    				 if(val.amSignInPatchStatus=="1"){
	    				 if(val.amSignInStatus=="1"){
	    					 that.find(".zhun").text("准时打卡");
	    				 }else if(val.amSignInStatus=="2"){
	    					 that.find(".zhun").text("迟到");
	    					 that.find(".zhun").css("color","#ffc232");
	    					 that.find(".wi1").append('<input type="checkbox"  class="statusIn">')
	    				 }else if(val.amSignInStatus=="3"){
	    					 that.find(".zhun").text("无打卡");
	    					 that.find(".zhun").css("color","#f15a5a");
	    					 that.find(".wi1").append('<input type="checkbox"  class="statusIn">')
	    				 }
    				
    				 }else{
    					 
    					 if(val.amSignInPatchStatus=="2"){
        					 that.find(".zhun").text("申请中");
        					 that.find(".zhun").css("color","gray");
        				 }else if(val.amSignInPatchStatus=="3"){
        					 that.find(".zhun").text("补签通过");
        					 that.find(".zhun").css("color","#3ddccd");
        				 }else if(val.amSignInPatchStatus=="4"){
        					 that.find(".zhun").text("补签失败");
        					 that.find(".zhun").css("color","black");
        				 }
    					 
    				 }
    				 
    				 
    				 
    				 if(val.amSignOutPatchStatus=="1"){
	    				 if(val.amSignOutStatus=="1"){
	    					 that.find(".zhun1").text("准时打卡");
	    					 that.find(".zhun1").css("color","#3ddccd");
	    				 }else if(val.amSignOutStatus=="2"){
	    					 that.find(".zhun1").text("早退");
	    					 that.find(".zhun1").css("color","#ff953e");
	    					 that.find(".wi2").append('<input type="checkbox"  class="statusIn">')
	    				 }else if(val.amSignOutStatus=="3"){
	    					 that.find(".zhun1").text("无打卡");
	    					 that.find(".zhun1").css("color","#f15a5a");
	    					 that.find(".wi2").append('<input type="checkbox"  class="statusIn">')
	    				 }
    				 }else{
    					 if(val.amSignOutPatchStatus=="2"){
	    					 that.find(".zhun1").text("申请中");
	    					 that.find(".zhun1").css("color","gray");
	    				 }else if(val.amSignOutPatchStatus=="3"){
	    					 that.find(".zhun1").text("补签通过");
	    					 that.find(".zhun1").css("color","#3ddccd");
	    				 }else if(val.amSignOutPatchStatus=="4"){
	    					 that.find(".zhun1").text("补签失败");
	    					 that.find(".zhun1").css("color","black");
	    				 }
    				 }
    				 
    				if(val.pmSignInPatchStatus=="1"){
	    				 if(val.pmSignInStatus=="1"){
	    					 that.find(".zhun2").text("准时打卡");
	    					 that.find(".zhun2").css("color","#3ddccd");
	    				 }else if(val.pmSignInStatus=="2"){
	    					 that.find(".zhun2").text("迟到");
	    					 that.find(".zhun2").css("color","#ffc232");
	    					 that.find(".wi3").append('<input type="checkbox"  class="statusIn">')
	    				 }else if(val.pmSignInStatus=="3"){
	    					 that.find(".zhun2").text("无打卡");
	    					 that.find(".zhun2").css("color","#f15a5a");
	    					 that.find(".wi3").append('<input type="checkbox"  class="statusIn">')
	    				 }
    				 
    				}else{
    					if(val.pmSignInPatchStatus=="2"){
	    					 that.find(".zhun2").text("申请中");
	    					 that.find(".zhun2").css("color","gray");
	    				 }else if(val.pmSignInPatchStatus=="3"){
	    					 that.find(".zhun2").text("补签通过");
	    					 that.find(".zhun2").css("color","#3ddccd");
	    				 }else if(val.pmSignInPatchStatus=="4"){
	    					 that.find(".zhun2").text("补签失败");
	    					 that.find(".zhun2").css("color","black");
	    				 }
    				}
    				 
    			  if(val.pmSignOutPatchStatus=="1"){	
    				 if(val.pmSignOutStatus=="1"){
    					 that.find(".zhun3").text("准时打卡");
    					 that.find(".zhun3").css("color","#3ddccd");
    				 }else if(val.pmSignOutStatus=="2"){
    					 that.find(".zhun3").text("早退");
    					 that.find(".zhun3").css("color","#ff953e");
    					 that.find(".wi4").append('<input type="checkbox"  class="statusIn">')
    				 }else if(val.pmSignOutStatus=="3"){
    					 that.find(".zhun3").text("无打卡");
    					 that.find(".zhun3").css("color","#f15a5a");
    					 that.find(".wi4").append('<input type="checkbox"  class="statusIn">')
    				 }
    			  }else{
    				  if(val.pmSignOutPatchStatus=="2"){
	    					 that.find(".zhun3").text("申请中");
	    					 that.find(".zhun3").css("color","gray");
	    				 }else if(val.pmSignOutPatchStatus=="3"){
	    					 that.find(".zhun3").text("补签通过");
	    					 that.find(".zhun3").css("color","#3ddccd");
	    				 }else if(val.pmSignOutPatchStatus=="4"){
	    					 that.find(".zhun3").text("补签失败");
	    					 that.find(".zhun3").css("color","black");
	    				 }
    				  
    			  } 
    				 
    				 
    				 that.find(".zhun").attr("amSignInStatus",val.amSignInStatus);
    				 that.find(".zhun1").attr("amSignOutStatus",val.amSignOutStatus);
    				 that.find(".zhun2").attr("pmSignInStatus",val.pmSignInStatus);
    				 that.find(".zhun3").attr("pmSignOutStatus",val.pmSignOutStatus);
    				 
    				 
    				 that.find(".zhun").attr("amSignInPatchStatus",val.amSignInPatchStatus);
    				 that.find(".zhun1").attr("amSignOutPatchStatus",val.amSignOutPatchStatus);
    				 that.find(".zhun2").attr("pmSignInPatchStatus",val.pmSignInPatchStatus);
    				 that.find(".zhun3").attr("pmSignOutPatchStatus",val.pmSignOutPatchStatus);
    				 
    				 that.attr("date",val.date)
    				 
    			 }
    		 })
     })
    //绑定日历表头
    var calendarName=calUtil.showYear+"-"+calUtil.showMonth;
     var isDis=parseInt(calUtil.showMonth);
     var currents = new Date();
         showYear=currents.getFullYear();
         sshowMonth=currents.getMonth() + 1;
       if(isDis+1>=sshowMonth&&showYear==calUtil.showYear){
     	  $("#calendar_month_next").attr("disabled","disabled");
           $("#calendar_month_next").css("background","gray");
       }
     //console.log(calUtil.showYear,calUtil.showMonth);
     
    $("#startTime").val(calendarName);  
  },
 //绑定事件
  bindEnvent:function(){
    //绑定上个月事件
	$(".calendar_month_prev").off("click");
    $(".calendar_month_prev").click(function(){
      //ajax获取日历json数据
      calUtil.eventName="prev";
      calUtil.id="prev";
      calUtil.init();
      $("#calendar_month_next").removeAttr("disabled");
      $("#calendar_month_next").css("background","#339bf1");
      return false;
    });
   
    //绑定下个月事件
    $(".calendar_month_next").off("click");
    $(".calendar_month_next").click(function(){
      //ajax获取日历json数据
      calUtil.eventName="next";
      calUtil.id="next";
      calUtil.init();
      return false;
    });
    //查询事件
    $(".lookqian").off("click");
    $(".lookqian").click(function(){
    	 calUtil.eventName="search";
    	 calUtil.id="search";
         calUtil.init();
    })
    //本月
    $("#thisweekI").off("click"); 
    $("#thisweekI").click(function(){
    	calUtil.eventName="load";
    	 calUtil.id=null;
         calUtil.init();
         $("#calendar_month_next").attr("disabled","disabled");
         $("#calendar_month_next").css("background","gray");
    })
  },
  //获取当前选择的年月
  setMonthAndDay:function(){
    switch(calUtil.eventName)
    {
      case "load":
        var current = new Date();
        calUtil.showYear=current.getFullYear();
        calUtil.showMonth=current.getMonth() + 1;
        break;
      case "prev":
        var nowMonth=$("#startTime").val().split("-")[1];
        var nowYear=$("#startTime").val().split("-")[0];
        calUtil.showMonth=parseInt(nowMonth)-1;
        calUtil.showYear=parseInt(nowYear);
        if(calUtil.showMonth==0)
        {
            calUtil.showMonth=12;
            calUtil.showYear-=1;
        }
  
        break;
      case "next":
        var nowMonth=$("#startTime").val().split("-")[1];
        var nowYear=$("#startTime").val().split("-")[0];
        calUtil.showMonth=parseInt(nowMonth)+1;
        calUtil.showYear=parseInt(nowYear);
        if(calUtil.showMonth==13)
        {
            calUtil.showMonth=1;
            calUtil.showYear+=1;
        }
        break;
      case "search":
    	  var nowMonth=$("#startTime").val().split("-")[1];
    	  var nowYear=$("#startTime").val().split("-")[0];
    	      calUtil.showYear=nowYear;
    	      calUtil.showMonth=parseInt(nowMonth);
    	     
      break;
    }
    if(calUtil.showMonth<10){
    	calUtil.showMonth="0"+calUtil.showMonth;
    }
    var mosd=calUtil.showYear+"-"+calUtil.showMonth;
          calUtil.dataU=mosd;
  },
  getDaysInmonth : function(iMonth, iYear){
   var dPrevDate = new Date(iYear, iMonth, 0);
   return dPrevDate.getDate();
  },
  bulidCal : function(iYear, iMonth,dataC) {
	       calUtil.namearr=[];
 	       for ( var key in dataC) {
 	    	     dataC[key][0].date=key;
 	    	    calUtil.namearr.push(dataC[key][0]);
				}
       var len=calUtil.namearr.length;
       for (var i = 0; i < len; i++) {
        for (var j = 0; j < len - 1 - i; j++) {
            if (calUtil.namearr[j].date > calUtil.namearr[j+1].date) {        // 相邻元素两两对比
                var temp = calUtil.namearr[j+1];        // 元素交换
                calUtil.namearr[j+1] = calUtil.namearr[j];
                calUtil.namearr[j] = temp;
            }
        }
    }
   var aMonth = new Array();
   aMonth[0] = new Array(7);
   aMonth[1] = new Array(7);
   aMonth[2] = new Array(7);
   aMonth[3] = new Array(7);
   aMonth[4] = new Array(7);
   aMonth[5] = new Array(7);
   aMonth[6] = new Array(7);
   var dCalDate = new Date(iYear, iMonth - 1, 1);
   var iDayOfFirst = dCalDate.getDay();
   var iDaysInMonth = calUtil.getDaysInmonth(iMonth, iYear);

       
   var iVarDate = 1;
   var d, w;
   aMonth[0][0] = "日";
   aMonth[0][1] = "一";
   aMonth[0][2] = "二";
   aMonth[0][3] = "三";
   aMonth[0][4] = "四";
   aMonth[0][5] = "五";
   aMonth[0][6] = "六";
   for (d = iDayOfFirst; d < 7; d++) {
    aMonth[1][d] = iVarDate;
    iVarDate++;
   }
   for (w = 2; w < 7; w++) {
    for (d = 0; d < 7; d++) {
     if (iVarDate <= iDaysInMonth) {
      aMonth[w][d] = iVarDate;
      iVarDate++;
     }
    }
   }
   return aMonth;
  },
  ifHasSigned : function(signList,day){
   var signed = false;
   $.each(signList,function(index,item){
    if(item.signDay == day) {
     signed = true;
     return false;
    }
   });
   return signed ; 
  },
  drawCal : function(iYear,iMonth,signList) {
	  //获取后台数据
	  var dataC;
	  var param=new Object();
	  param._csrf=$("#_csrf").val();
	  if(calUtil.id==null){
		  param.thisMonth=calUtil.dataU
	  }else if(calUtil.id=="prev"){
		  param.lastMonth="1";
	  }else if(calUtil.id=="next"){
		  param.nextMonth="1";
	  }else if(calUtil.id="search"){
		  param.monthBeg=calUtil.dataU;
	  }
	  //console.log(param)
	  tool.post("/att/attSignInOut/personalSignInList",param,function(data){
		  //console.log(data)
		   if(data.tdMap!=null){
			   dataC=data.tdMap[0]
		   }
	  },false)
   var myMonth = calUtil.bulidCal(iYear, iMonth,dataC);
   var htmls = new Array();
   htmls.push("<div class='sign_main' id='sign_layer'>");
   htmls.push("<div class='sign' id='sign_cal'>");
   htmls.push("<table class='layui-table admin-table'>");
   htmls.push("<tr  style='background:#e7ecef'>");
   htmls.push("<th>周" + myMonth[0][0] + "</th>");
   htmls.push("<th>周" + myMonth[0][1] + "</th>");
   htmls.push("<th>周" + myMonth[0][2] + "</th>");
   htmls.push("<th>周" + myMonth[0][3] + "</th>");
   htmls.push("<th>周" + myMonth[0][4] + "</th>");
   htmls.push("<th>周" + myMonth[0][5] + "</th>");
   htmls.push("<th>周" + myMonth[0][6] + "</th>");
   htmls.push("</tr>");
   var d, w;
   for (w = 1; w < 6; w++) {
    htmls.push("<tr>");
    for (d = 0; d < 7; d++) {
      htmls.push("<td style='text-align:right;padding-right:16px'>" + (!isNaN(myMonth[w][d]) ? calUtil.showMonth+"/"+myMonth[w][d] : " ") + "</td>");
     
    }
     htmls.push("</tr>");
     htmls.push("<tr>");
       for (var c = 0; c < 7; c++) {
            if(!isNaN(myMonth[w][c])&&c!=0&&c!=6) {
            	  if(myMonth[w][c]<10){
            		  myMonth[w][c]="0"+myMonth[w][c];
            	  }
       	 htmls.push('<td style="" class="samecalss" id="'+calUtil.showYear+"-"+calUtil.showMonth+"-"+myMonth[w][c]+' 00:00:00">'+
       	     '<div> <div class="wi1"></div><span style="color:#3ddccd"  class="width50 zhun"></span></div>'+
       	     '<div> <div class="wi2"></div><span style="color:#ffc232"  class="width50 zhun1"></span><span></span></div>'+
       	     '<div> <div class="wi3"></div><span style="color:#ff953e"  class="width50 zhun2"></span><span></span></div>'+
       	     '<div> <div class="wi4"></div><span style="color:#f15a5a"  class="width50 zhun3"></span><span></span></div>'+
       	 '</td>');
       	   }else{
       	   	 	 htmls.push('<td style="background:#f2f2f2"></td>');
       	   }
       }
     htmls.push("</tr>");
   }
   htmls.push("</table>");
   htmls.push("</div>");
   htmls.push("</div>");
   return htmls.join('');
  
  }
};