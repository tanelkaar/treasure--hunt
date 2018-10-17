var currentPos;
var path;
var marker;
var map;

function initMap() {

	currentPos = new google.maps.LatLng(58.28091316,26.88244579);
	
	map = new google.maps.Map(document.getElementById('map'), {
		zoom : 17,
		center : currentPos
	});

	// hide some labels and stuff
	map.setOptions({
		styles : [ {
			featureType : 'poi',
			stylers : [ {
				visibility : 'off'
			} ]
		}, {
			featureType : 'transit',
			elementType : 'labels.icon',
			stylers : [ {
				visibility : 'off'
			} ]
		} ]
	});

	// draw path
	path = new google.maps.Polyline({
		strokeColor : '#000000',
		strokeOpacity : 1.0,
		strokeWeight : 3
	});

	path.setMap(map);

	path.getPath().push(currentPos);
	initMarkers(currentPos);

	var infoWindow = new google.maps.InfoWindow;
	updateCurrentLocation(infoWindow);

	// to test polyline
	map.addListener('click', function(event) {
		changeCurrentPosition(event.latLng);
	});
}

function initMarkers(currentPos) {
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
	marker = new google.maps.Marker({
		position : currentPos,
		map : map,
		icon : image,
		shape : shape,
		title : 'My location',
		zIndex : 3
	});
}

function updateCurrentLocation(infoWindow) {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var location = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
			changeCurrentPosition(location);
		}, function() {
			handleLocationError(true, infoWindow);
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false, infoWindow);
	}
}

function handleLocationError(browserHasGeolocation, infoWindow) {
	infoWindow.setPosition(pos);
	infoWindow
			.setContent(browserHasGeolocation ? 'Error: The Geolocation service failed.'
					: 'Error: Your browser doesn\'t support geolocation.');
	infoWindow.setPosition(map.getCenter());
	infoWindow.open(map);
}

function changeCurrentPosition(location) {
	path.getPath().push(location);
	marker.setPosition(location);
	map.setCenter(location);
	currentPos = location;
}