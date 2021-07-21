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

	$("#upload_link").on('click', function(e) {
		e.preventDefault();
		$("#file").trigger('click');
	});

	$("#file").on("change", function(e) {
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
			error: function(e) {
				console.log(response);
				resetModal("errorResponseModal");
				$("#errorResponseModal").find(".modal-body").text(response.responseText);
				$("#errorResponseModal").modal("show");
			}
		});
	});
});

function loadListItems(element_id, page = 0) {
	let url = "/admin/api/players";

	loadListHtml(element_id, url, page);
}

