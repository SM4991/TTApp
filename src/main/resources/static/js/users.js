/**
 * 
 */
function enable2FA(){
    set2FA(true);
}
function disable2FA(){
    set2FA(false);
}
function set2FA(use2FA){
    $.ajax({
		url: "/admin/api/users/update/2fa",
		method: "post",
		data: {"use2FA": use2FA},
		headers: httpRequestTokenHeader(),
		success: function(response) {
			if(use2FA){
	        	$("#qr").append('<img src="'+response+'" />').show();
	        }else{
	            window.location.reload();
        	}
		}, error: function(response) {
			handleAjaxError(response);
		}
	});
}