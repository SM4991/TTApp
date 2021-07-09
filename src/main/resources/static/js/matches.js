/**
 * 
 */
function getMatchDetail() {
	mid = $("#mid").val();
	if (authorized == 1) {
		request_url = "/admin/api/matches/" + mid;
	} else {
		request_url = "/api/matches/" + mid;
	}

	/* Show overlay */
	showLoadingOverlay();
	$.ajax({
		url: request_url,
		method: "get",
		contentType: "application/json",
		success: function(response) {
			$("#" + parent_id).html(response);

			/* Hide overlay */
			hideLoadingOverlay();
		}, error: function(response) {
			handleAjaxError(response);
		}
	});
}

function startSet(event, setNumber) {
	if (authorized == 1) {
		mid = $("#mid").val();
		let request_url = "/admin/api/matches/startSet/" + mid + "/" + setNumber;

		/* Show overlay */
		showLoadingOverlay();

		$.ajax({
			url: request_url,
			method: "post",
			contentType: "application/json",
			headers: httpRequestTokenHeader(),
			success: function(response) {
				getMatchDetail(parent_id);
			}, error: function(response) {
				handleAjaxError(response);
			}
		});
	}
}

function updateScore(event, id, player, state) {
	if (authorized == 1) {
		let request_url = "/admin/api/matches/updateScore/" + id + "?player=" + player + "&state=" + state;

		$(".update-score-icon").css("pointer-events", "none");
		$.ajax({
			url: request_url,
			method: "post",
			contentType: "application/json",
			headers: httpRequestTokenHeader(),
			success: function(response) {
				if (response.status == 1) {
					$("#player" + player + "_score").text(response.score);
					$("#set" + id + "_player" + player + "_score").text(response.score);
				} else {
					getMatchDetail(parent_id);
				}
				$(".update-score-icon").css("pointer-events", "auto");
			}, error: function(response) {
				handleAjaxError(response);
				$(".update-score-icon").css("pointer-events", "auto");
			}
		});
	}
}
