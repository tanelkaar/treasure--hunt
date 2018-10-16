function initMap() {
	var currentPos = getCurrentLocation();

	if (currentPos == null) {
		currentPos = {
			lat : 58.28091316,
			lng : 26.88244579
		}
	}
	var map = new google.maps.Map(document.getElementById('map'), {
		zoom : 17,
		center : currentPos
	});

	setMarkers(map, currentPos);
}

function setMarkers(map, currentPos) {
	// Adds markers to the map.

	// Marker sizes are expressed as a Size of X,Y where the origin of the image
	// (0,0) is located in the top left of the image.

	// Origins, anchor positions and coordinates of the marker increase in the X
	// direction to the right and in the Y direction down.
	var image = {
		url : 'img/shark.png',
		// This marker is 20 pixels wide by 32 pixels high.
		size : new google.maps.Size(32, 32),
		// The origin for this image is (0, 0).
		origin : new google.maps.Point(0, 0),
		// The anchor for this image is the base of the flagpole at (0, 32).
		anchor : new google.maps.Point(0, 32)
	};
	// Shapes define the clickable region of the icon. The type defines an HTML
	// <area> element 'poly' which traces out a polygon as a series of X,Y
	// points.
	// The final coordinate closes the poly by connecting to the first
	// coordinate.
	var shape = {
		coords : [ 1, 1, 1, 20, 18, 20, 18, 1 ],
		type : 'poly'
	};
	var marker = new google.maps.Marker({
		position : currentPos,
		map : map,
		icon : image,
		shape : shape,
		title : 'My location',
		zIndex : 1
	});
}

function getCurrentLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			};
			return pos;
		}, function() {
			handleLocationError(true);
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false);
	}
}

function handleLocationError(browserHasGeolocation) {
	/*
	 * infoWindow.setPosition(pos); infoWindow.setContent(browserHasGeolocation ?
	 * 'Error: The Geolocation service failed.' : 'Error: Your browser doesn\'t
	 * support geolocation.'); infoWindow.open(map);
	 */
}