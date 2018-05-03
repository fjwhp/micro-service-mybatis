
var calUtil = {
  //数据		
  mydata:{},
  //当前日历显示的年份
  showYear:2015,
  //当前日历显示的月份
  showMonth:1,
  //当前日历显示的天数
  showDays:1,
  eventName:"load",
  //初始化日历
  init:function(){
    calUtil.setMonthAndDay();
    calUtil.draw();
    calUtil.bindEnvent();
  },
  data:function(){
  	var data={"s":1}
  	return data;
  },
  draw:function(){
  	  $("#calendar").html("");
    //绑定日历
    var str = calUtil.drawCal(calUtil.showYear,calUtil.showMonth);
    $("#calendar").html(str);
    //绑定日历表头
    var calendarName=calUtil.showYear+"-"+calUtil.showMonth;
    $("#startTime2").val(calendarName); 
  },
 //绑定事件
  bindEnvent:function(){
    //绑定上个月事件
	$(".calendar_month_prev").off("click");
    $(".calendar_month_prev").click(function(){
      //ajax获取日历json数据
      calUtil.eventName="prev";
      calUtil.init();
    });
   
    //绑定下个月事件
    $(".calendar_month_next").off("click");
    $(".calendar_month_next").click(function(){
      //ajax获取日历json数据
      calUtil.eventName="next";
      calUtil.init();
    });
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
    	 var nowMonth=$("#startTime2").val().split("-")[1];
        calUtil.showMonth=parseInt(nowMonth)-1;
        if(calUtil.showMonth==0)
        {
            calUtil.showMonth=12;
            calUtil.showYear-=1;
        }
        if(calUtil.showMonth<10){
        	calUtil.showMonth="0"+calUtil.showMonth;
        }
        var mosd=calUtil.showYear+"-"+calUtil.showMonth;
        break;
      case "next":
    	  var nowMonth=$("#startTime2").val().split("-")[1];
        calUtil.showMonth=parseInt(nowMonth)+1;
        if(calUtil.showMonth==13)
        {
            calUtil.showMonth=1;
            calUtil.showYear+=1;
        }
        if(calUtil.showMonth<10){
        	calUtil.showMonth="0"+calUtil.showMonth;
        }
        var mosd=calUtil.showYear+"-"+calUtil.showMonth;
        break;
    }
    
    //这里用年和月份 调后台接口
    console.log("这里用年和月份 调后台接口##############"+calUtil.showYear,calUtil.showMonth)
  },
  getDaysInmonth : function(iMonth, iYear){
   var dPrevDate = new Date(iYear, iMonth, 0);
   return dPrevDate.getDate();
  },
  //构造日历数据
  bulidCal : function(iYear, iMonth) {
   var aMonth = new Array();
   aMonth[0] = new Array(7);
   aMonth[1] = new Array(7);
   aMonth[2] = new Array(7);
   aMonth[3] = new Array(7);
   aMonth[4] = new Array(7);
   aMonth[5] = new Array(7);
   aMonth[6] = new Array(7);
   //获取每月的第一天是 看看是星期几
   var dCalDate = new Date(iYear, iMonth - 1, 1);
   var iDayOfFirst = dCalDate.getDay();
   //获取每个月的最后一天是几号  就知道每个月有多少天
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
  drawCal : function(iYear, iMonth) {
	  //得到每个月的数据 二维数组
   var myMonth = calUtil.bulidCal(iYear, iMonth);
   var data=calUtil.data();
   console.log(data)
   var htmls = new Array();
   htmls.push("<div class='sign_main' id='sign_layer'>");
   htmls.push("<div class='sign' id='sign_cal'>");
   htmls.push("<table>");
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
     htmls.push("<tr style='height:100px;'>");
       for (var c = 0; c < 7; c++) {
            if(!isNaN(myMonth[w][c])&&c!=0&&c!=6) {
            	if(c==1){
		       	 htmls.push('<td style="background:#e6edf4" class="hei33">'+
		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #eaa905">***会议通知</div></div>'+
		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #009688">***个人计划</div></div>'+
		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #336fe1">***共享日程</div></div>'+
		       	 '</td>');
            	}else if(c==2){
            		 htmls.push('<td style="background:#e6edf4"  class="hei33">'+
        		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #eaa905">***会议通知</div></div>'+
        		       	     '<div></div>'+
        		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #336fe1">***共享日程</div></div>'+
        		       	 '</td>');
            	}else if(c==3){
            		htmls.push('<td style="background:#e6edf4"  class="hei33">'+
       		       	     '<div></div>'+
       		       	     '<div></div>'+
       		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #336fe1">***共享日程</div></div>'+
       		       	 '</td>');
            	}else if(c==4){
            		htmls.push('<td style="background:#e6edf4"  class="hei33">'+
          		       	     '<div></div>'+
          		       	     '<div></div>'+
          		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #336fe1">***共享日程</div></div>'+
          		       	 '</td>');
               	}else if(c==5){
            		htmls.push('<td style="background:#e6edf4"  class="hei33">'+
          		       	     '<div></div>'+
          		       	     '<div></div>'+
          		       	     '<div><div style="box-sizing:border-box; border-left:2px solid #336fe1">***共享日程</div></div>'+
          		       	 '</td>');
               	}
       	   }else{
       	   	 	 htmls.push('<td style="background:#e7ecef"></td>');
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