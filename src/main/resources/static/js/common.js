function toggleLoadingOverlay() {
	$('#overlay').toggle();
}
function hideLoadingOverlay() {
	$('#overlay').hide();
}
function showLoadingOverlay() {
	$('#overlay').show();
}
function httpRequestTokenHeader() {
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");
	return {
		[header]: token,
	};
}
function resetModal(id) {
	$("#" + id).modal({ backdrop: 'static', keyboard: false });
	$("#" + id).find(".modal-body").text("...");
	$("#" + id).find(".modal-footer button").removeAttr("onClick");
}
function redirectToUrl(url) {
	window.location.href = url;
}
function handleAjaxError(response) {
	console.log(response);

	/* Hide overlay */
	hideLoadingOverlay();
	if (response.status == 400 && response.responseJSON.error == undefined) {
		$.each(response.responseJSON, function(key, value) {
			if ($('[name=' + key + ']').is(':checkbox')) {
				$('[name=' + key + ']:last').parent().append('<span class="field-error">' + value + '</span>');
			} else {
				$('[name=' + key + ']').after('<span class="field-error">' + value + '</span>');
			}
		});
	} else {
		resetModal("errorResponseModal");
		if (response.status == 404 || response.status == 400) {
			if(response.responseJSON.message != "No message") {
				error_text = response.responseJSON.message;
			} else {
				error_text = response.responseJSON.error;
			}
		} else {
			error_text = response.responseText;
		}
		$("#errorResponseModal").find(".modal-body").text(error_text);
		$("#errorResponseModal").modal("show");
	}
}
function formSubmit(event, url, fields, redirect_url) {
	event.preventDefault();

	$(".field-error").remove();

	var form_data = {};
	$.each(fields, function(key, field) {
		let ele = $("[name=" + field + "]");
		if (ele.is(":checkbox")) {
			form_data[field] = [];
			$("[name=" + field + "]:checked").each(function() {
				form_data[field].push($(this).val());
			});
			form_data[field] = form_data[field].length > 0 ? form_data[field] : null;
		} else {
			form_data[field] = ele.val() != "" ? ele.val() : null;
		}
	});

	/* Show overlay */
	showLoadingOverlay();
	
	$.ajax({
		url: url,
		method: "post",
		data: JSON.stringify(form_data),
		// dataType: 'json',
		contentType: "application/json",
		headers: httpRequestTokenHeader(),
		success: function(response) {
			/* Hide overlay */
			hideLoadingOverlay();
			
			resetModal("successResponseModal");
			$("#successResponseModal").find(".modal-body").text(response);

			if (redirect_url != undefined && redirect_url != "") {
				$("#successResponseModal").find(".modal-footer button").attr("onClick", "redirectToUrl('" + redirect_url + "')");
			}

			$("#successResponseModal").modal("show");
		}, error: function(response) {
			handleAjaxError(response);
		}
	});
}

function loadListHtml(element_id, request_url, page = 0) {
	page_url = "";
	if (page > 0) {
		page_url = request_url.indexOf('?') > -1 ? "&page=" + page : "?page=" + page;	
	}
	var request_url = request_url + page_url;

	/* Show overlay */
	showLoadingOverlay();
	$.ajax({
		url: request_url,
		method: "get",
		contentType: "application/json",
		success: function(response) {
			$("#" + element_id).html(response);
			
			/* Hide overlay */
			hideLoadingOverlay();
		}, error: function(response) {
			handleAjaxError(response);
		}
	});
}

function paginatorHtml(response) {
	paginator_html = "";
	currentPage = response.currentPage;
	if (currentPage > 1) {
		paginator_html += "<li><a class='pagination-clickable btn btn-primary btn-pagination' href='javascript:void(0)' attr-page='1'>First</a></li>";
		paginator_html += "<li><a class='pagination-clickable btn btn-primary btn-pagination' href='javascript:void(0)' attr-page='" + (currentPage - 1) + "'>Previous</a></li>";
	} else {
		paginator_html += "<li><a class='btn btn-primary btn-pagination' href='javascript:void(0)'>First</a></li>";
		paginator_html += "<li><a class='btn btn-primary btn-pagination' href='javascript:void(0)'>Previous</a></li>";
	}

	/*
	let counter = 1;
	let max_page_no = 5;
	let from = Math.round(max_page_no/2);
	console.log(from);	
	let t = response.currentPage > from ? response.currentPage-from : 0;
	t = response.totalPages 
	console.log(t);
	let start = 1+t;
	*/
	start = 1;
	for (i = start; i <= response.totalPages; i++) {
		if (currentPage != i) {
			paginator_html += "<li><a class='pagination-clickable btn btn-primary btn-pagination' href='javascript:void(0)' attr-page='" + i + "'>" + i + "</a></li>";
		} else {
			paginator_html += "<li><a class='btn btn-primary btn-pagination active' href='javascript:void(0)'>" + i + "</a></li>";
		}

		/* 
		counter++;
		if (counter > max_page_no) {
			break;
			paginator_html += "<li><a class='btn btn-primary btn-pagination' href='javascript:void(0)'>...</a></li>";
		} 
		*/
	}

	if (currentPage < response.totalPages) {
		paginator_html += "<li><a class='pagination-clickable btn btn-primary btn-pagination' href='javascript:void(0)' attr-page='" + (currentPage + 1) + "'>Next</a></li>";
		paginator_html += "<li><a class='pagination-clickable btn btn-primary btn-pagination' href='javascript:void(0)' attr-page='" + response.totalPages + "'>Last</a></li>";
	} else {
		paginator_html += "<li><a class='btn btn-primary btn-pagination' href='javascript:void(0)'>Next</a></li>";
		paginator_html += "<li><a class='btn btn-primary btn-pagination' href='javascript:void(0)'>Last</a></li>";
	}

	return paginator_html;
}

function hideShowDomBlock(block_elements, active_element) {
	$.each(block_elements, function(k, element) {
		element.addClass("hide");
	});
	active_element.removeClass("hide");
}