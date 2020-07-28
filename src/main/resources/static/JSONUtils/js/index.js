
$(function(){
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
    var val=($("#searchvalue").val());
    var json=$("#json-src").val();
    console.log(json);
}

function arraynumchange(){
    var num=$(".arraynum").val();
    var width=(num.length);
    $(".arraynum").css("width",(width)*10+"px");
}


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






}