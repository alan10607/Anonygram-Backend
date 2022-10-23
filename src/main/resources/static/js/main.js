function post(url, data, afterFunc, afterError, ...args){
	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(data),
		headers:{"Content-Type" : "application/json;charset=utf8"},
		dataType: "json",
		async: false,
		success: function (res, status) {
			if(afterFunc != null)
			    afterFunc(res.result, (args != null && args.length == 1 ? args[0] : args));

//			console.log("Status:" + status + ",res:" + JSON.stringify(res));
		},
		error: function (xhr, status) {
			if(afterError != null)
		        afterError(xhr.responseJSON);

			console.log("Status:" + status + ",xhr:" + JSON.stringify(xhr));
		}
	});
}

function postData(url, formData, afterFunc, afterError, ...args){
var formData = new FormData();
formData.append('section', 'general');
formData.append('action', 'previewImg');
// Attach file
formData.append('image', $('input[type=file]')[0].files[0]);

	$.ajax({
		type: "POST",
        enctype: "multipart/form-data",
        processData: false,
        contentType: false,
		url: url,
		data: formData,
		dataType: "json",
		async: false,
		success: function (res, status) {
			if(afterFunc != null)
			    afterFunc(res.result, (args != null && args.length == 1 ? args[0] : args));
		},
		error: function (xhr, status) {
			if(afterError != null)
		        afterError(xhr.responseJSON);

			console.log("Status:" + status + ",xhr:" + JSON.stringify(xhr));
		}
	});
}