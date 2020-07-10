
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



})
