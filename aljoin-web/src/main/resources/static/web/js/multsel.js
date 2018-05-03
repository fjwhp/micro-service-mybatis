/**
 * multiple select plugin
 * @date: 2017/09/13
 * @author: liugl@inspur.com
 */
function multselInit(){
	$_sel=$("div.multsel");
	$_sel.each(function(){
		$(this).append("<i></i>");
	});
	$_sel.find("span.view").click(function(){
		$_t=$(this).parent("div.multsel");
		if($_t.hasClass('expand')){
			$_t.find(".selist").hide();
			$_t.find("i:first").removeClass("pop");
			$_t.removeClass('expand');
		}else {
			$_t.find(".selist").show();
			$_t.find("i:first").addClass("pop");
			$_t.addClass('expand');
		}
	});

	$_sel.find(".selist").find("a.seitem").click(function(){
		$_this=$(this);
		
		if($_this.hasClass('checked')){
			$_this.removeClass('checked');
		}else{
			$_this.addClass('checked');
		}
		multselCheck($_this.parent(".selist"));
	});

    $(document).on('click','.seitem',function(){
        $_this=$(this);

        if($_this.hasClass('checked')){
            $_this.removeClass('checked');
        }else{
            $_this.addClass('checked');
        }
        multselCheck($_this.parent(".selist"));
	});

	$(document).click(function(e){
		var t = $("div.multsel")[0],target = e.target;
		if (t !== target && !$.contains(t, target)) {
			$_sel.removeClass('expand').find(".selist").hide();
			$_sel.find("i:first").removeClass("pop");
		}
	});
}

function multselCheck($obj){
	var names='',vals=[],namesArr = [];
	$obj.find('a.checked').each(function(){
		var $t=$(this);
		names+=$t.html()+'  ';
		vals.push($t.attr("value"));
        namesArr.push($t.text());
	});
	if(!names){
		names='请选择...';
	}
	/*$obj.parent('div.multsel').find('span.view').html(names);	*/	
	multselVals(vals,namesArr);
}

/**
 * interface
 * @param vals
 */
function multselVals(vals,names){
	var str='',nameStr='';
	for(var i=0,size=vals.length;i<size;i++){
		str+=vals[i]+";";
	}
	nameStr+=names;
	$('#testarea').html(nameStr);
    $('#testarea').attr("userId",str);
    $("#temp-id").val(str);
    $("#n_receiverNameid").val(str);
}