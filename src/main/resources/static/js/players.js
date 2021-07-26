/**
 * 
 */
$(document).ready(function() {
	$("body").on("click", "#list-view .pagination-clickable", function(e) {
		let page_no = $(this).attr("attr-page");
		if (page_no != undefined && page_no != "") {
			loadListItems("page-list-data", page_no);
		}
	});

	$("form#create-form").on("submit", function(event) {
		event.preventDefault();
		formSubmit(event, "/admin/api/players", ["name", "age", "email", "gender", "image"], "/admin/players");
	});

	$("#create-form #upload_link").on('click', function(e) {
		e.preventDefault();
		$("#create-form #file").trigger('click');
	});

	$("#create-form #file").on("change", function(e) {
		var file = $(this)[0].files[0];
		var data = new FormData();
		data.append("file", file);

		console.log(data);
		$.ajax({
			type: "POST",
			enctype: 'multipart/form-data',
			url: "/admin/api/players/upload",
			data: data,
			processData: false,
			contentType: false,
			cache: false,
			timeout: 600000,
			headers: httpRequestTokenHeader(),
			success: function(response) {
				$("#image").val(response);
				$("#default-form-image").attr("src", response);
			},
			error: function(response) {
				handleAjaxError(response);
			}
		});
	});
	
	$("form#import-form").on("submit", function(e) {
		var file = $(this).find("input[type='file']")[0].files[0];
		if(file != undefined && file != '') {
			var data = new FormData();
			data.append("file", file);
	
			console.log(data);
			$.ajax({
				type: "POST",
				enctype: 'multipart/form-data',
				url: "/admin/api/players/import",
				data: data,
				processData: false,
				contentType: false,
				cache: false,
				timeout: 600000,
				headers: httpRequestTokenHeader(),
				success: function(response) {
					console.log(response);
					$("#import-response").html(response);
				},
				error: function(response) {
					handleAjaxError(response);
				}
			});
		} else {
			$(this).find("input[type='file']").after('<span class="field-error">Please select a file</span>');
		}
		
	});
	
	$("form#import-async-form").on("submit", function(e) {
		var files = $("#import-async-form").find("input[type='file']")[0].files;
		console.log(files);
		if(files != undefined && files != '') {
			var data = new FormData();
			$.each(files, function(i, file) {
			    data.append('files', file);
			});
			// data.append("files", files);
	
			console.log(data);
			$.ajax({
				type: "POST",
				enctype: 'multipart/form-data',
				url: "/admin/api/players/importAsync",
				data: data,
				processData: false,
				contentType: false,
				cache: false,
				timeout: 600000,
				headers: httpRequestTokenHeader(),
				success: function(response) {
					console.log(response);
					$("#import-response").html(response);
				},
				error: function(response) {
					handleAjaxError(response);
				}
			});
		} else {
			$(this).find("input[type='file[]']").after('<span class="field-error">Please select a file</span>');
		}
		
	});
});

function loadListItems(element_id, page = 0) {
	let url = "/admin/api/players";

	loadListHtml(element_id, url, page);
}

