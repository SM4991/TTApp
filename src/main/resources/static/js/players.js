/**
 * 
 */
$(document).ready(function() {
	$("body").on("click", "#list-view .pagination-clickable", function(e) {
		let page_no = $(this).attr("attr-page");
		console.log(page_no);
		if (page_no != undefined && page_no != "") {
			loadListItems("list-view", page_no);
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

function loadListItems(list_parent_id, page = 0) {
	let url = "/admin/api/players";
	if (page > 0) {
		var request_url = url + "?page=" + page;
	} else {
		var request_url = url;
	}

	var dom_elements = [
		$(".first-list-view"),
		$("#" + list_parent_id)
	];

	/* Show overlay */
	showLoadingOverlay();
	$.ajax({
		url: request_url,
		method: "get",
		contentType: "application/json",
		success: function(response) {
			if (response.items.length > 0) {
				li_dom = $("#sample-list-card").clone();
				let list_html = "";
				$.each(response.items, function(k, data) {
					li_dom.find(".main-content").html("<a href='/admin/players/" + data.id + "'>" + data.name + "</a>");
					li_dom.find(".sub-content .age-detail").text(data.age);
					li_dom.find(".sub-content .gender-detail").text(data.gender);

					if (data.image != null && data.image != "") {
						li_dom.find(".list-image-content img").attr("src", data.image);
					} else {
						li_dom.find(".list-image-content img").attr("src", "/images/blank-profile-picture.png");
					}

					list_html += li_dom.html();
				});

				$("#" + list_parent_id + " .list-view-wrapper").html(list_html);

				let paginator_html = paginatorHtml(response);

				$("#" + list_parent_id + " .list-view-pagination-wrapper").html(paginator_html);

				hideShowDomBlock(dom_elements, $("#" + list_parent_id));
			} else {
				hideShowDomBlock(dom_elements, $(".first-list-view"));
			}
			/* Hide overlay */
			hideLoadingOverlay();
		}, error: function(response) {
			console.log(response);
			/* Hide overlay */
			toggleLoadingOverlay();
			resetModal("errorResponseModal");
			$("#errorResponseModal").find(".modal-body").text(response.responseText);
			$("#errorResponseModal").modal("show");
		}
	});
}