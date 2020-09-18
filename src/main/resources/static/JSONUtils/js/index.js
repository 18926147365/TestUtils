
$(function(){
    var keyworkNum=0;
    var keyworkIndex=0;
    function isJSON(str) {
        if (typeof str == 'string') {
            try {
                var obj=JSON.parse(str);
                if(typeof obj == 'object' && obj ){
                    return true;
                }else{
                    return false;
                }

            } catch(e) {
                console.log('error：'+str+'!!!'+e);
                return false;
            }
        }
        console.log('It is not a string!')
    }
    $.ajax({
        url:"/copy/getContent",
        dataType:"json",
        success:function(data){
            if(data.code==0){
                var str=data.data;
                if($("#json-src").val()==''){
                    if(isJSON(str)){
                        $("#json-src").val(data.data);
                    }
                    $('#json-src').keyup();
                }


            }
        }
    })


    $.ajax({
        url:"/copy/getCopyJSONList",
        dataType:"json",
        success:function(data){
            if(data.code==0){
                var list=data.data;
                var listSort=new Array();
                for(var i=list.length-1;i>=0;i--){
                    listSort.push(list[i]);
                }
                var html=template("houxuanlist",listSort);
                $("#cus-footer").html(html)
            }
        }
    })

    $("#cus-footer").on("click",".overflows",function(){
        $("#cus-footer .overflows").removeClass("active");
        var text=$(this).text();
        $(this).addClass("active");
        $("#json-src").val(text);
        $('#json-src').keyup();

    })



})

function searchvalue(){
    keyworkNum=0;
    keyworkIndex=0;
    //精准查询、模糊查询
    var val=($("#searchvalue").val());
    var json=$("#json-src").val();
    var jsonKeyList=$("#json-target").find(".json_key");
    $("#keywordSelect").empty();
    $(".keywordSelect").hide();
    $("#json-target").find(".json_key").removeClass("star");

    var bischecked=$('#ismohu').is(':checked');
    $("#keyworkNum").text(0+"/"+0);
    if(val=="" || val.length<=1){
        return ;
    }
    var candidateWorks=new Array();
    var num=0;
    jsonKeyList.each(function(i,info){
        var lval=val.toLowerCase();
        var text=$(info).text();
        if(bischecked){
            if(text.toLowerCase().indexOf(lval)!=-1){
                var texts=text.replace("\"","").replace("\"","");
                if(candidateWorks.indexOf(texts)==-1){
                    candidateWorks.push(texts);
                }
                num++;
                $(info).addClass("star")
            }
        }else{
            var texts=text.replace("\"","").replace("\"","");
            if(texts.toLowerCase()==(lval)){
                if(candidateWorks.indexOf(texts)==-1){
                    candidateWorks.push(texts);
                }
                num++;
                $(info).addClass("star")
            }
        }


    })


    if(candidateWorks.length==0 ){
        return ;
    }
    var kselectHtml="";
    for (let i = 0; i < candidateWorks.length; i++) {
        kselectHtml+=('<option>'+candidateWorks[i]+'</option>');
    }
    $("#keywordSelect").attr("size",candidateWorks.length);
    $("#keywordSelect").append(kselectHtml);
    keyworkNum=num;
    keyworkIndex=1;
    $("#keyworkNum").text(keyworkIndex+"/"+num);
    $(".keywordSelect").css("display","inline-block");
    $(".star").eq(0)[0].scrollIntoView();
    $("#right-box").scrollTop($("#right-box").scrollTop()-44);

}


$('#ismohu').click(function (){
    searchvalue();
})

function validData(){
    $("#searchvalue").val("");
    $("#arraynumhide").hide();
    var jsonStr=$("#json-src").val();
    var json=JSON.parse(jsonStr);
   if ((typeof json=='object')&& json.constructor==Array){
       $("#arraynumhide").show();
       $(".arraynum").val("");
   }else if ((typeof json=='object')){
       $("#arraynumhide").hide();

   }

    $("#searchvalue").keydown(function(event){
        if(event.keyCode==40){//下键
            $("#keywordSelect option").eq(0).attr("selected","true");
            $("#keywordSelect").focus();
        }

    });

    $("#keywordSelect").keydown(function(event){
        var code=event.keyCode;
        if(code==8){//删除
            var val=$("#searchvalue").val();
            val=val.substring(0,val.length);
            $("#searchvalue").val(val);
            $("#searchvalue").focus();
        }else if(code==13){//回车
            $(".keywordSelect").hide();
            $("#searchvalue").val($("#keywordSelect").val());
            searchvalue();
            $("#searchvalue").focus();
        }

    });
    $("#keywordSelect").blur(function (){
        $(".keywordSelect").hide();
    })


    $("#keywordSelect").on("click","option",function(){
        $(".keywordSelect").hide();
        $("#searchvalue").val($("#keywordSelect").val());
        searchvalue();
        $("#searchvalue").focus();
    })

    $("#lastkeywork").click(function (){
        if(keyworkIndex<=1){
            return ;
        }
        keyworkIndex--;
        myscrollIntoView();
    })

    $("#nextkeywork").click(function (){
        if(keyworkIndex>=keyworkNum){
            return ;
        }
        keyworkIndex++;
        myscrollIntoView();
    })

    function myscrollIntoView(){
        $(".star").eq(0)[keyworkIndex].scrollIntoView();
        $("#right-box").scrollTop($("#right-box").scrollTop()-44);
        $("#keyworkNum").text(keyworkIndex+"/"+keyworkNum);
    }
}