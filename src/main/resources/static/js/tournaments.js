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
		formSubmit(event, "/admin/api/tournaments", ["name", "startDate", "regEndDate", "maxScore", "image", "matchTypeIds"], "/admin/tournaments");
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
			url: "/admin/api/tournaments/upload",
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
				handleAjaxError(response);
			}
		});
	});

	$("#create-draw-form select[name='players']").on("change", function() {
		addSelectedDrawPlayer("list-view", $(this).val());
	});

	$("body").on("click", "#create-draw-form .remove-btn", function() {
		removeSelectedDrawPlayer($(this));
	});

	$("form#create-draw-form").on("submit", function(event) {
		event.preventDefault();
		generateDraw(event, "list-view");
	});
});

function loadListItemsByStatus(list_parent_id, status) {
	$(".tournament-tabs-list li").removeClass("active");
	$("#trTab" + status).addClass("active");
	loadListItems(list_parent_id);
}

function loadListItems(list_parent_id, page = 0) {
	var status = $(".tournament-tabs-list li.active a").attr("data-status");

	let url = "";
	console.log(authorized);
	if (authorized == 1) {
		url = "/admin/api/tournaments";
	} else {
		url = "/api/tournaments";
	}
	url += "?status=" + status;
	if (page > 0) {
		var request_url = url + "&page=" + page;
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
					if (authorized == 1) {
						li_dom.find(".main-content").html("<a href='/admin/tournaments/" + data.id + "'>" + data.name + "</a>");
					} else {
						li_dom.find(".main-content").html("<a href='/tournaments/" + data.id + "'>" + data.name + "</a>");
					}
					li_dom.find(".sub-content.start-date-content span.content").text(data.startDate);
					li_dom.find(".sub-content.reg-end-date-content span.content").text(data.regEndDate);

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
			handleAjaxError(response);
		}
	});
}

function addSelectedDrawPlayer(list_parent_id, id) {
	request_url = "/admin/api/players/" + id;

	if (!$("#player" + id).length) {
		/* Show overlay */
		showLoadingOverlay();
		$.ajax({
			url: request_url,
			method: "get",
			contentType: "application/json",
			success: function(response) {
				li_dom = $("#sample-list-card").clone();

				li_dom.find(".list-view-card").attr("id", "player" + id);
				li_dom.find(".list-view-card").attr("data-id", id);
				li_dom.find(".main-content").html(response.name);
				li_dom.find(".sub-content .age-detail").text(response.age);
				li_dom.find(".sub-content .gender-detail").text(response.gender);

				if (response.image != null && response.image != "") {
					li_dom.find(".list-image-content img").attr("src", response.image);
				} else {
					li_dom.find(".list-image-content img").attr("src", "/images/blank-profile-picture.png");
				}

				$("#" + list_parent_id + " .list-view-wrapper").append(li_dom.html());

				/* Hide overlay */
				hideLoadingOverlay();
			}, error: function(response) {
				handleAjaxError(response);
			}
		});
	}
}

function removeSelectedDrawPlayer(object) {
	$(object).parents(".list-view-card").remove();
}

function generateDraw(event, list_parent_id) {
	$(".field-error").remove();
	if ($("#matchType").val() != "") {
		if ($("#" + list_parent_id + " li.list-view-card").length && $("#" + list_parent_id + " li.list-view-card").length >= 2) {
			let t_id = $("#tid").val();
			let request_url = "/admin/api/tournaments/draw";
			let redirect_url = "/admin/tournaments/" + t_id;
			let p_ids = [];
			$("#" + list_parent_id + " li.list-view-card").each(function() {
				p_ids.push($(this).attr("data-id"));
			});

			var form_data = {};
			form_data["tournamentId"] = t_id;
			form_data["playerIds"] = p_ids;
			form_data["matchTypeId"] = $("#matchType").val();

			$.ajax({
				url: request_url,
				method: "post",
				data: JSON.stringify(form_data),
				contentType: "application/json",
				headers: httpRequestTokenHeader(),
				success: function(response) {
					console.log(response);
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
		} else {
			$("#players").parent().append('<span class="field-error">Select atleast 2 players</span>');
		}
	} else {
		$("#matchType").parent().append('<span class="field-error">Select a match type</span>');
	}
}

function loadTournamentFixture() {
	$(".field-error").remove();
	if ($("#matchType").val() != "") {
		let t_id = $("#tid").val();
		let request_url = "/admin/api/tournaments/" + t_id + "/" + $("#matchType").val() + "/fixture/";

		$.ajax({
			url: request_url,
			method: "get",
			contentType: "application/json",
			success: function(response) {
				console.log(response);
				// $("#bracket").html(response);
				let dom = $("<div>").attr("id", 'tournament');
				dom.attr("class", "tournament");
				let html = '';
				$.each(response, function(roundId, orderMatches){
					html += '<div class="round '+roundId+'">';
					let morder = 1;
					$.each(orderMatches, function(order, matches){
						
						$.each(matches, function(index, match){
							html += '<div class="match-block"><ul class="match-player-list">';	
							//Player 1
							let player1_class = match.player1 != undefined && match.player1 != null ? "active" : "inactive";
							let player1_name = match.player1 != undefined && match.player1 != null ? match.player1.name : "Player 1";
							let player1_img = match.player1 != undefined && match.player1 != null && match.player1.image != "" && match.player1.image != null ? match.player1.image : "/images/blank-profile-picture.png";
							html += '<li class="player player1 '+player1_class+'">\
								<span class="image-wrapper">\
									<img src="'+player1_img+'" id="default-form-image" class="default-form-image" />\
								</span>\
								<span class="text-content">\
								'+player1_name+'\
								</span></li>';
								
							//Player 2
							let player2_class = match.player2 != undefined && match.player2 != null ? "active" : "inactive";
							let player2_name = match.player2 != undefined && match.player2 != null ? match.player2.name : "Player 2";
							let player2_img = match.player2 != undefined && match.player2 != null && match.player2.image != "" && match.player2.image != null ? match.player2.name : "/images/blank-profile-picture.png";
							
							html += '<li class="player player2 '+player2_class+'">\
								<span class="image-wrapper">\
									<img src="'+player2_img+'" id="default-form-image" class="default-form-image" />\
								</span>\
								<span class="text-content">\
								'+player2_name+'\
								</span></li>';
							html += '</ul></div>';
						});
						
					});
					html += '</div>';
				});
				$(dom).html(html);
				
				$("#tournament-wrapper").append(dom);
				
			}, error: function(response) {
				handleAjaxError(response);
			}
		});
	} else {
		$("#matchType").parent().append('<span class="field-error">Select a match type</span>');
	}
}

function getTournamentMatches(object, list_parent_id) {
	if ($(object).val() != "") {
		tid = $("#tid").val();
		if (authorized == 1) {
			request_url = "/admin/api/tournaments/" + tid + "/" + $(object).val() + "/matches";
		} else {
			request_url = "/api/tournaments/" + tid + "/" + $(object).val() + "/matches";
		}
		/* Show overlay */
		showLoadingOverlay();
		$.ajax({
			url: request_url,
			method: "get",
			contentType: "application/json",
			success: function(response) {
				li_dom = $("#sample-list-card").clone();

				html = "";
				if (response.length > 0) {
					$.each(response, function(k, match) {
						let player1 = match.player1 != null ? match.player1 : { name: "-", image: "-", age: "-", gender: "-" };
						if(authorized == 1) {
							li_dom.find(".list-title").html("<a href='/admin/matches/" + match.id + "'>" + match.tournamentRound.name + " - " + match.name + "</a>");
						} else {
							li_dom.find(".list-title").html("<a href='/matches/" + match.id + "'>" + match.tournamentRound.name + " - " + match.name + "</a>");
						}
						li_dom.find(".list-view-card .list-left-block .main-content").text(player1.name);
						li_dom.find(".list-view-card .list-left-block .sub-content .age-detail").text(player1.age);
						li_dom.find(".list-view-card .list-left-block .sub-content .gender-detail").text(player1.gender);
						if (player1.image != null && player1.image != "") {
							li_dom.find(".list-image-content .list-left-block img").attr("src", player1.image);
						} else {
							li_dom.find(".list-image-content .list-left-block img").attr("src", "/images/blank-profile-picture.png");
						}

						let player2 = match.player2 != null ? match.player2 : { name: "-", image: "", age: "-", gender: "-" };
						li_dom.find(".list-view-card .list-right-block .main-content").text(player2.name);
						li_dom.find(".list-view-card .list-right-block .sub-content .age-detail").text(player2.age);
						li_dom.find(".list-view-card .list-right-block .sub-content .gender-detail").text(player2.gender);
						if (player2.image != null && player2.image != "") {
							li_dom.find(".list-image-content .list-right-block img").attr("src", player2.image);
						} else {
							li_dom.find(".list-image-content .list-right-block img").attr("src", "/images/blank-profile-picture.png");
						}

						html += li_dom.html();
					});
				} else {
					html = "<h5>No Matches Found</h5>";
				}

				$("#" + list_parent_id + " .list-view-wrapper").html(html);

				/* Hide overlay */
				hideLoadingOverlay();
			}, error: function(response) {
				handleAjaxError(response);
			}
		});
	}
}