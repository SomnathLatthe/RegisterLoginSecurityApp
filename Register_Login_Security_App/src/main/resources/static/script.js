//not working
$(function(){
    var $registerForm=$('#saveUser');
    $registerForm.validate({
        rules:{
            name:{
                required:true;
            }
        },
        messages:{
            name:{
                required:"name must be required";
            }
        }
    })
})