var waypoints = [];
var arcArr = [];

var ctx;

const initWidth = 1656;
const initHeight = 823;
var width = 1656; //pixels
var height = 823; //pixels
var fieldWidth = 652; // in inches
var fieldHeight = 324; // in inches
var robotWidth = (27 + (3.5 * 2));
var robotHeight = (32 + (3.5 * 2));
// var robotWidth = 34.5; //inches //32.75
// var robotHeight = 38.5; //inches //37.5
const halfL = robotHeight / 2.0;
var pointRadius = 5;
var turnRadius = 30;
var kEpsilon = 1E-9;
var image;
var imageFlipped;
var wto;

var currentFile = 0;

var lastSetStartingPosition = 0;
const level1StartX = (48 + halfL);
var startingPositions = [[22, 205], [22, 117], [level1StartX, 205], [level1StartX, 162], [level1StartX, 119]];

var maxSpeed = 120;
var maxSpeedColor = [0, 255, 0];
var minSpeed = 0;
var minSpeedColor = [255, 0, 0];
var pathFillColor = "rgba(150, 150, 150, 0.5)";

const wheelDiameter = 6;	//Wheel Diameter in inches
const accelValue = 10; 	//Inches/second^2
const maxVelocity = 30; //Inches/second
const timeStep = 0.01;	//Time step in seconds
const encoderTicksPerRev = 4096;

class Translation2d {
	constructor(x, y) {
		this.x = x;
		this.y = y;
	}

	norm() {
		return Math.sqrt(Translation2d.dot(this, this));
	}

	scale(s) {
		return new Translation2d(this.x * s, this.y * s);
	}

	translate(t) {
		return new Translation2d(this.x + t.x, this.y + t.y);
	}

	invert() {
		return new Translation2d(-this.x, -this.y);
	}

	perp() {
		return new Translation2d(-this.y, this.x);
	}

	draw(color) {
		color = color || "#f72c1c";
		ctx.beginPath();
		ctx.arc(this.drawX, this.drawY, pointRadius, 0, 2 * Math.PI, false);
		ctx.fillStyle = color;
		ctx.strokeStyle = color;
		ctx.fill();
		ctx.lineWidth = 0;
		ctx.stroke();
	}

	get drawX() {
		return this.x * (width / fieldWidth);
	}

	get drawY() {
		return height - this.y * (height / fieldHeight);
	}

	get angle() {
		return Math.atan2(-this.y, this.x);
	}

	static diff(a, b) {
		return new Translation2d(b.x - a.x, b.y - a.y);
	}

	static cross(a, b) {
		return a.x * b.y - a.y * b.x;
	}

	static dot(a, b) {
		return a.x * b.x + a.y * b.y;
	}

	static angle(a, b) {
		return Math.acos(Translation2d.dot(a, b) / (a.norm() * b.norm()));
	}
}

class Waypoint {
	constructor(position, speed, radius, marker, comment) {
		this.position = position;
		this.speed = speed;
		this.radius = radius;
		this.marker = marker;
		this.comment = comment;
	}

	draw() {
		this.position.draw((this.radius > 0) ? "rgba(120,120,120,0.8)" : null);
	}

	toString() {
		if (this.marker === "")
			return "new Waypoint(" + this.position.x + "," + this.position.y + "," + this.radius + "," + this.speed + ")";
		else
			return "new Waypoint(" + this.position.x + "," + this.position.y + "," + this.radius + "," + this.speed + ",\"" + this.marker.trim() + "\")";
	}
}

class Line {
	constructor(pointA, pointB) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.slope = Translation2d.diff(pointA.position, pointB.position);
		this.start = pointA.position.translate(this.slope.scale(pointA.radius / this.slope.norm()));
		this.end = pointB.position.translate(this.slope.scale(pointB.radius / this.slope.norm()).invert());
	}

	draw() {
		ctx.beginPath();
		ctx.moveTo(this.start.drawX, this.start.drawY);
		ctx.lineTo(this.end.drawX, this.end.drawY);

		try {
			var grad = ctx.createLinearGradient(this.start.drawX, this.start.drawY, this.end.drawX, this.end.drawY);
			grad.addColorStop(0, getColorForSpeed(this.pointB.speed));
			grad.addColorStop(1, getColorForSpeed(getNextSpeed(this.pointB)));
			ctx.strokeStyle = grad;
		} catch (e) {
			ctx.strokeStyle = "#00ff00"
		}

		ctx.lineWidth = pointRadius * 2;
		ctx.stroke();
		this.pointA.draw();
		this.pointB.draw();
	}

	fill() {
		var start = this.start;
		var deltaEnd = Translation2d.diff(this.start, this.end);
		var angle = deltaEnd.angle;
		var length = deltaEnd.norm();
		for (var i = 0; i < length; i++) {
			drawRotatedRect(start.translate(deltaEnd.scale(i / length)), robotHeight, robotWidth, angle, null, pathFillColor, true);
		}
	}

	length() {
		return Math.sqrt(Math.pow(this.end.x - this.start.x, 2) + Math.pow(this.end.y - this.start.y, 2));
	}

	translation() {
		return new Translation2d(this.pointB.position.y - this.pointA.position.y, this.pointB.position.x - this.pointA.position.x)
	}

	slope() {
		if (this.pointB.position.x - this.pointA.position.x > kEpsilon)
			return (this.pointB.position.y - this.pointA.position.y) / (this.pointB.position.x - this.pointA.position.x);
		else
			return (this.pointB.position.y - this.pointA.position.y) / kEpsilon;
	}

	b() {
		return this.pointA.y - this.slope() * this.pointA.x;
	}

	static intersect(a, b, c, d) {
		var i = ((a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x));
		i = (Math.abs(i) < kEpsilon) ? kEpsilon : i;
		var x = (Translation2d.cross(a, b) * (c.x - d.x) - Translation2d.cross(c, d) * (a.x - b.x)) / i;
		var y = (Translation2d.cross(a, b) * (c.y - d.y) - Translation2d.cross(c, d) * (a.y - b.y)) / i;
		return new Translation2d(x, y);
	}

	static pointSlope(p, s) {
		return new Line(p, p.translate(s));
	}
}

class Arc {
	constructor(lineA, lineB) {
		this.lineA = lineA;
		this.lineB = lineB;
		this.center = Line.intersect(lineA.end, lineA.end.translate(lineA.slope.perp()), lineB.start, lineB.start.translate(lineB.slope.perp()));
		this.center.draw;
		this.radius = Translation2d.diff(lineA.end, this.center).norm();
	}

	draw() {
		var sTrans = Translation2d.diff(this.center, this.lineA.end);
		var eTrans = Translation2d.diff(this.center, this.lineB.start);
		//console.log(sTrans);
		//console.log(eTrans);
		var sAngle, eAngle;
		if (Translation2d.cross(sTrans, eTrans) > 0) {
			eAngle = -Math.atan2(sTrans.y, sTrans.x);
			sAngle = -Math.atan2(eTrans.y, eTrans.x);
		} else {
			sAngle = -Math.atan2(sTrans.y, sTrans.x);
			eAngle = -Math.atan2(eTrans.y, eTrans.x);
		}
		this.lineA.draw();
		this.lineB.draw();
		ctx.beginPath();
		ctx.arc(this.center.drawX, this.center.drawY, this.radius * (width / fieldWidth), sAngle, eAngle);
		ctx.strokeStyle = getColorForSpeed(this.lineB.pointB.speed);
		ctx.stroke();
	}

	fill() {
		this.lineA.fill();
		this.lineB.fill();
		var sTrans = Translation2d.diff(this.center, this.lineA.end);
		var eTrans = Translation2d.diff(this.center, this.lineB.start);
		var sAngle = (Translation2d.cross(sTrans, eTrans) > 0) ? sTrans.angle : eTrans.angle;
		var angle = Translation2d.angle(sTrans, eTrans);
		var length = angle * this.radius;
		for (var i = 0; i < length; i += this.radius / 100) {
			drawRotatedRect(this.center.translate(new Translation2d(this.radius * Math.cos(sAngle - i / length * angle), -this.radius * Math.sin(sAngle - i / length * angle))), robotHeight, robotWidth, sAngle - i / length * angle + Math.PI / 2, null, pathFillColor, true);
		}



	}

	arcLength() {
		if (typeof this.lineA !== 'undefined' && typeof this.lineB !== 'undefined') {
			return 2 * this.radius * Math.asin(this.pointDistance(this.lineA.end.x, this.lineA.end.y, this.lineB.start.x, this.lineB.start.y) / (2 * this.radius));
		}
		else
			console.log("Error calculating length");
	}

	getPointsFromArc(initialPosition, initialVelocity) {
		var points = [];
		var posCurrent = initialPosition;
		//TODO: Fix overlapping segment issue
		var i = 0;
		var lineALength = this.lineA.length() + posCurrent;
		console.log("Line A Length: " + lineALength);
		for (; posCurrent < lineALength; i++) {
			var tCurrent = i * timeStep;
			var currentAccel = 0;

			currentAccel = velCurrent >= maxVelocity ? 0 : accelValue;

			posCurrent = Math.min(currentAccel * Math.pow(tCurrent, 2) * 0.5 + initialVelocity * tCurrent + initialPosition, lineALength);
			var velCurrent = currentAccel * tCurrent + initialVelocity;
			velCurrent = velCurrent > maxVelocity ? maxVelocity : velCurrent;
			//velCurrent = velCurrent < 0 ? 0 : velCurrent;
			points.push(new TalonSRXPoint(convertInchestoRotations(posCurrent), convertInchesPerSecondToNativeUnitsPer100ms(velCurrent), timeStep));
			initialVelocity = velCurrent;
			initialPosition = posCurrent;
		}

		if (!((this.lineA.start.x === this.lineA.end.x || this.lineB.start.x === this.lineB.end.x) || (this.lineA.start.y === this.lineA.end.y || this.lineB.start.y === this.lineB.end.y))) {
			var currArcLength = this.arcLength() + posCurrent;
			console.log("Arc Length: " + currArcLength);
			for (; posCurrent < currArcLength; i++) {
				var tCurrent = i * timeStep;
				var currentAccel = 0;

				currentAccel = velCurrent >= maxVelocity ? 0 : accelValue;

				posCurrent = Math.min(currentAccel * Math.pow(tCurrent, 2) * 0.5 + initialVelocity * tCurrent + initialPosition, currArcLength);
				var velCurrent = currentAccel * tCurrent + initialVelocity;
				velCurrent = velCurrent > maxVelocity ? maxVelocity : velCurrent;
				//velCurrent = velCurrent < 0 ? 0 : velCurrent;
				points.push(new TalonSRXPoint(convertInchestoRotations(posCurrent), convertInchesPerSecondToNativeUnitsPer100ms(velCurrent), timeStep));
				initialVelocity = velCurrent;
				initialPosition = posCurrent;
			}
		}

		var lineBLength = this.lineB.length() + posCurrent;
		console.log("Line B Length: " + lineBLength);
		for (; posCurrent < lineBLength; i++) {
			var tCurrent = i * timeStep;
			var currentAccel = 0;

			currentAccel = velCurrent >= maxVelocity ? 0 : accelValue;

			posCurrent = Math.min(currentAccel * Math.pow(tCurrent, 2) * 0.5 + initialVelocity * tCurrent + initialPosition, lineBLength);
			var velCurrent = currentAccel * tCurrent + initialVelocity;
			velCurrent = velCurrent > maxVelocity ? maxVelocity : velCurrent;
			//velCurrent = velCurrent < 0 ? 0 : velCurrent;
			points.push(new TalonSRXPoint(convertInchestoRotations(posCurrent), convertInchesPerSecondToNativeUnitsPer100ms(velCurrent), timeStep));
			initialVelocity = velCurrent;
			initialPosition = posCurrent;
		}

		return points;
	}

	pointDistance(x1, y1, x2, y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	static fromPoints(a, b, c) {
		return new Arc(new Line(a, b), new Line(b, c));
	}
}

function pointDistance(x1, y1, x2, y2) {
	return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
}

class TalonSRXPoint {
	constructor(pos, vel, time) {
		this.pos = pos;
		this.vel = vel;
		this.time = time;
	}
}

function convertInchestoRotations(inches) {
	return inches / (wheelDiameter * Math.PI);
}

function convertInchesPerSecondToNativeUnitsPer100ms(ips) {
	return (ips * 60) / (wheelDiameter * Math.PI) * encoderTicksPerRev / 600;
}

function convertRotationstoInches(rotations) {
	return rotations * (wheelDiameter * Math.PI);
}

function convertNativeUnitsPer100msToInchesPerSecond(ips) {
	return (ips / 60) * (wheelDiameter * Math.PI) / encoderTicksPerRev * 600;
}

function init() {
	console.log("Startup: " + new Date());

	$("#field").css("width", (width / 1.5) + "px");
	$("#field").css("height", (height / 1.5) + "px");
	ctx = document.getElementById('field').getContext('2d')
	ctx.canvas.width = width;
	ctx.canvas.height = height;
	ctx.clearRect(0, 0, width, height);
	ctx.fillStyle = "#FF0000";
	image = new Image();
	image.src = 'files/field.png';
	image.onload = function () {
		ctx.drawImage(image, 0, 0, width, height);
		update();
	}

	//imageFlipped = new Image();
	//imageFlipped.src = 'fieldflipped.png';
	$('input').bind("change paste keyup", function () {
		console.log("change");
		clearTimeout(wto);
		wto = setTimeout(function () {
			update();
		}, 500);
	});

	setStartingPositionPoint(startingPositions[0][0], startingPositions[0][1]);
	update();
}

function clear() {
	ctx.clearRect(0, 0, width, height);
	ctx.fillStyle = "#FF0000";
	if (flipped)
		ctx.drawImage(imageFlipped, 0, 0, width, height);
	else
		ctx.drawImage(image, 0, 0, width, height);
}

function findRowNumber(row) {
	var rowFound = -1;
	var counter = 0;
	$('tbody').children('tr').each(function () {
		if (($('tbody').children()[counter++] == row))
			rowFound = counter;
	})

	return rowFound;
}

function setAngle(row) {
	// evaluateWaypoints();
	row -= 1;

	var lastX = getRowValue(row, 0);
	var lastY = getRowValue(row, 1);

	var movingX = getRowValue(row - 1, 0);
	var movingY = getRowValue(row - 1, 1);

	var angle = getRowValue(row, 6);

	var newPos = movePointAroundPoint(lastX, lastY, angle, movingX, movingY);


	if (angle != undefined) {
		setRowValue(row - 1, 0, newPos.x);
		setRowValue(row - 1, 1, newPos.y);
		// $($($($('tbody').children()[row - 1]).children()[1]).children()).val(newPos.y);
		// $($($($('tbody').children()[row - 1]).children()[0]).children()).val(newPos.x);


		var centerPoint = translateAtAngle(lastX, lastY, angle, pointDistance(lastX, lastY, newPos.x, newPos.y) / 2.0);
		addRawPointAt(row - 1, centerPoint.x, centerPoint.y, 1, 30, "");

		update();
	}
}

function getRowValue(row, val) {
	return eval($($($($('tbody').children()[row]).children()[val]).children()).val())
}

function setRowValue(row, val, val2) {
	$($($($('tbody').children()[row]).children()[val]).children()).val(val2);
}

function changeRow(row, x, y, radius, speed, marker) {
	$($($($('tbody').children()[row]).children()[0]).children()).val(x);
	$($($($('tbody').children()[row]).children()[1]).children()).val(y);

	$($($($('tbody').children()[row]).children()[2]).children()).val(radius);
	$($($($('tbody').children()[row]).children()[3]).children()).val(speed);

	$($($($('tbody').children()[row]).children()[4]).children()).val(marker);
}

function addPoint(x, y, radius) {
	addPointRound(x, y, radius, 60, false);
}

function addPointRound(x, y, radius, speed, round) {
	var prev;
	if (waypoints.length > 0)
		prev = waypoints[waypoints.length - 1].position;
	else
		prev = new Translation2d(20, 260);

	if (typeof x === "undefined") { x = prev.x + 20; }

	if (typeof y === "undefined") { y = prev.y; }

	if (typeof radius === "undefined") {
		radius = 0;
		if ($("table tr").length > 1 && waypoints.length > 1) {
			if (x !== waypoints[waypoints.length - 1].position.x && x !== waypoints[waypoints.length - 2].position.x &&
				y !== waypoints[waypoints.length - 1].position.y && y !== waypoints[waypoints.length - 2].position.y) {
				var idx = parseFloat($('tbody').children().length) - 1;
				$($($($('tbody').children()[idx]).children()[2]).children()).val(15);
			}
		}
	}

	x = Math.round(x * (round ? 100 : 1)) / (round ? 100.0 : 1);
	y = Math.round(y * (round ? 100 : 1)) / (round ? 100.0 : 1);

	radius = Math.round(radius);

	console.log("Adding point: [" + x + "," + y + "], " + radius);

	addRawPoint(x, y, radius, speed, "");

	update();
	$('input').unbind("change paste keyup");
	$('input').bind("change paste keyup", function () {
		console.log("change");
		clearTimeout(wto);
		wto = setTimeout(function () {
			update();
		}, 500);
	});
}

function evaluateWaypoints() {
	var counter = 0;
	$('tbody').children('tr').each(function () {
		var x = parseFloat(eval($($($(this).children()).children()[0]).val()));

		var y = parseFloat(eval($($($(this).children()).children()[1]).val()));
		var radius = parseFloat(eval($($($(this).children()).children()[2]).val()));
		var speed = parseFloat(eval($($($(this).children()).children()[3]).val()));

		var angle = parseFloat(eval($($($(this).children()).children()[6]).val()));

		if (angle == undefined || isNaN(angle))
			angle = "";

		if (isNaN(radius) || isNaN(speed)) {
			radius = 0;
			speed = 0;
		}

		$($($(this).children()).children()[0]).val(x);
		$($($(this).children()).children()[1]).val(y);
		$($($(this).children()).children()[2]).val(radius);
		$($($(this).children()).children()[3]).val(speed);
		$($($(this).children()).children()[6]).val(angle);
	});

	update();
}

function reDrawCanvas() {
	$("#field").css("width", (width / 1.5) + "px");
	$("#field").css("height", (height / 1.5) + "px");
	ctx = document.getElementById('field').getContext('2d')
	ctx.canvas.width = width;
	ctx.canvas.height = height;
	ctx.clearRect(0, 0, width, height);
	ctx.fillStyle = "#FF0000";
	image = new Image();
	image.src = 'files/field.png';
	image.onload = function () {
		ctx.drawImage(image, 0, 0, width, height);
		update();
	}
}
// $("#multiplePaths").onchange = function(event) {
// 	var fileList = inputElement.files;
// 	console.log(1);
// 	//TODO do something with fileList.  
//  }

function myUpdate(toGetFromFile) {
	if ($("table tr").length > 1) {
		var idx = parseFloat($('tbody').children().length) - 1;
		if ($($($($('tbody').children()[idx]).children()[2]).children()).val() !== 0) {
			$($($($('tbody').children()[idx]).children()[2]).children()).val(0);
		}
	}

	height = initHeight * ($("#canvasSizeSlider").val() / 100);
	width = initWidth * ($("#canvasSizeSlider").val() / 100);

	if (toGetFromFile) {
		waypoints = [];
		$('tbody').children('tr').each(function () {
			var x = parseFloat(eval($($($(this).children()).children()[0]).val()));
			var y = parseFloat(eval($($($(this).children()).children()[1]).val()));
			var radius = parseFloat(eval($($($(this).children()).children()[2]).val()));
			var speed = parseFloat(eval($($($(this).children()).children()[3]).val()));
			if (isNaN(radius) || isNaN(speed)) {
				radius = 0;
				speed = 0;
			}
			var marker = ($($($(this).children()).children()[4]).val())
			if (marker == "undefined")
				marker = "";
			($($($(this).children()).children()[4]).val(marker))

			var comment = ($($($(this).children()).children()[5]).val())
			waypoints.push(new Waypoint(new Translation2d(x, y), speed, radius, marker, comment));
		});
	}

	drawPoints();
	drawRobot();

}
function update() {
	myUpdate(true);
}

function drawRobot() {
	if (waypoints.length > 1) {
		var deltaStart = Translation2d.diff(waypoints[0].position, waypoints[1].position);
		drawRotatedRect(waypoints[0].position, robotHeight, robotWidth, deltaStart.angle, getColorForSpeed(waypoints[1].speed));

		var deltaEnd = Translation2d.diff(waypoints[waypoints.length - 2].position, waypoints[waypoints.length - 1].position);
		drawRotatedRect(waypoints[waypoints.length - 1].position, robotHeight, robotWidth, deltaEnd.angle, getColorForSpeed(0));
	}
}

function drawRotatedRect(pos, w, h, angle, strokeColor, fillColor, noFill) {
	w = w * (width / fieldWidth);
	h = h * (height / fieldHeight);
	fillColor = fillColor || "rgba(0,0,0,0)";
	//ctx.save();
	if (noFill == null || !noFill)
		ctx.beginPath();
	ctx.translate(pos.drawX, pos.drawY);
	ctx.rotate(angle);
	ctx.rect(-w / 2, -h / 2, w, h);
	ctx.fillStyle = fillColor;
	if (noFill == null || !noFill)
		ctx.fill();
	if (strokeColor != null) {
		ctx.strokeStyle = strokeColor;
		ctx.lineWidth = 4;
		ctx.stroke();
	}
	ctx.rotate(-angle);
	ctx.translate(-pos.drawX, -pos.drawY);
	//ctx.restore();

}

function drawPoints() {
	clear();
	arcArr = [];
	var i = 0;
	ctx.beginPath();
	do {
		var a = Arc.fromPoints(getPoint(i), getPoint(i + 1), getPoint(i + 2));
		a.fill();
		i++;
	} while (i < waypoints.length - 2);
	ctx.fill();
	i = 0;
	do {
		var a = Arc.fromPoints(getPoint(i), getPoint(i + 1), getPoint(i + 2));
		arcArr.push(a);
		a.draw();
		i++;
	} while (i < waypoints.length - 2);

}

function doStuff() {
	var points = [];
	console.log(arcArr);
	for (var i = 0; i < arcArr.length; i++) {
		console.log(arcArr[i]);

		var tmp = arcArr[i];
		if (i === 0) {
			console.log("Beginning zero arc");
			var tmpPoints = tmp.getPointsFromArc(0, 0);

			for (var j = 0; j < tmpPoints.length; j++) {
				points.push(tmpPoints[j]);
			}
		} else {
			console.log("Beginning non zero arc");
			var lastPoint = points[points.length - 1];
			console.log(lastPoint);
			if (i === arcArr.length - 1)
				var tmpPoints = tmp.getPointsFromArc(convertRotationstoInches(lastPoint.pos), convertNativeUnitsPer100msToInchesPerSecond(lastPoint.vel));
			else
				var tmpPoints = tmp.getPointsFromArc(convertRotationstoInches(lastPoint.pos), convertNativeUnitsPer100msToInchesPerSecond(lastPoint.vel));

			for (var j = 0; j < tmpPoints.length; j++) {
				points.push(tmpPoints[j]);
			}
		}
		console.log(points);
	}

}

function getPoint(i) {
	if (i >= waypoints.length)
		return waypoints[waypoints.length - 1];
	else
		return waypoints[i];
}

/**
 * Import from waypoint data, not json data
 */
function importData() {
	$('#upl').click();
	let u = $('#upl')[0];
	$('#upl').change(() => {
		var file = u.files[0];
		var fr = new FileReader();
		fr.onload = function (e) {
			var c = fr.result;
			var s1 = c.split("\n");
			var tmpWaypoints = [];
			var tmpLine = [];
			let searchFolder1 = "paths";
			let searchFolder2 = ";";
			let searchString1 = "new Waypoint(";
			let searchString2 = ")";
			let searchReversed1 = "public boolean isReversed() {";
			let searchReversed2 = "}";
			let searchName1 = "public class";
			let searchName2 = "extends";
			let searchAdaption1 = "PathAdapter.";
			let searchAdaption2 = "(";

			$("#title").val(c.split(searchName1)[1].split(searchName2)[0].trim());
			$("#isReversed").prop('checked', c.split(searchReversed1)[1].split(searchReversed2)[0].trim().includes("true"));

			var daFolder = c.split(searchFolder1)[1].split(searchFolder2)[0].trim();
			if (daFolder.length > 0 && daFolder[0] == '.') {
				daFolder = daFolder.slice(1, daFolder.length);
			}

			$("#folder").val(daFolder);


			s1.forEach((line) => {
				if (line.indexOf("//") != 0 && line.indexOf(searchString1) >= 0) {
					tmpLine.push(line);
					tmpWaypoints.push(line.split(searchString1)[1].split(searchString2)[0].split(","));
				}
			});

			if (tmpLine[0].indexOf(searchAdaption1) >= 0) {
				var adaptStr = tmpLine[0].split(searchAdaption1)[1].split(searchAdaption2)[0].trim();
				switch (adaptStr) {
					case "getAdaptedLeftSwitchWaypoint":
						$("#startAdaptionValue").val("startswitchleft").prop('selected', true);
						break;
					case "getAdaptedRightSwitchWaypoint":
						$("#startAdaptionValue").val("startswitchright").prop('selected', true);
						break;
					case "getAdaptedLeftScaleWaypoint":
						$("#startAdaptionValue").val("startscaleleft").prop('selected', true);
						break;
					case "getAdaptedRightScaleWaypoint":
						$("#startAdaptionValue").val("startscaleright").prop('selected', true);
						break;
					default:
						$("#startAdaptionValue").val("startnone").prop('selected', true);
						break;
				}
			} else {
				$("#startAdaptionValue").val("startnone").prop('selected', true);
			}

			if (tmpLine[tmpLine.length - 1].indexOf(searchAdaption1) >= 0) {
				var adaptStr = tmpLine[tmpLine.length - 1].split(searchAdaption1)[1].split(searchAdaption2)[0].trim();
				switch (adaptStr) {
					case "getAdaptedLeftSwitchWaypoint":
						$("#endAdaptionValue").val("endswitchleft").prop('selected', true);
						break;
					case "getAdaptedRightSwitchWaypoint":
						$("#endAdaptionValue").val("endswitchright").prop('selected', true);
						break;
					case "getAdaptedLeftScaleWaypoint":
						$("#endAdaptionValue").val("endscaleleft").prop('selected', true);
						break;
					case "getAdaptedRightScaleWaypoint":
						$("#endAdaptionValue").val("endscaleright").prop('selected', true);
						break;
					default:
						$("#endAdaptionValue").val("endnone").prop('selected', true);
						break;
				}
			} else {
				$("#endAdaptionValue").val("endnone").prop('selected', true);
			}

			waypoints = [];
			$("tbody").empty();
			tmpWaypoints.forEach((wptmp, i) => {
				var wp;
				var x = 0;
				var y = 0;
				var radius = 0;
				var speed = 0;
				var marker = "";
				if (wptmp.length >= 4) {
					x = wptmp[0];
					y = wptmp[1];
					radius = wptmp[2];
					speed = wptmp[3];
				}
				if (wptmp.length >= 5) {
					marker = wptmp[4].replace(/"/g, "");
				}

				if (marker == " undefined")
					marker = "";

				wp = new Waypoint(new Translation2d(x, y), speed, radius, marker);
				addRawPoint(wp.position.x, wp.position.y, wp.radius, wp.speed, wp.marker);
			});
			update();

			$('input').unbind("change paste keyup");
			$('input').bind("change paste keyup", function () {
				console.log("change");
				clearTimeout(wto);
				wto = setTimeout(function () {
					update();
				}, 500);
			});

		}
		fr.readAsText(file);
	});
	update();
}

// function nextFile() {
// 	var files = document.getElementById("multiplePaths");
// 	files = files.files;
// 	currentFile++;

// 	if (currentFile > files.length)
// 		currentFile = 0;

// 	console.log(files[currentFile]);
// 	importData(currentFile);
// }



// function importData(fileNumber) {
// 	let u = $('#multiplePaths')[fileNumber];

// 	var file = u;
// 	var fr = new FileReader();
// 	fr.onload = function (e) {
// 		var c = fr.result;
// 		var s1 = c.split("\n");
// 		var tmpWaypoints = [];
// 		var tmpLine = [];
// 		let searchString1 = "new Waypoint(";
// 		let searchString2 = ")";
// 		let searchReversed1 = "public boolean isReversed() {";
// 		let searchReversed2 = "}";
// 		let searchName1 = "public class";
// 		let searchName2 = "extends";
// 		let searchAdaption1 = "PathAdapter.";
// 		let searchAdaption2 = "(";
// 		$("#title").val(c.split(searchName1)[1].split(searchName2)[0].trim());
// 		$("#isReversed").prop('checked', c.split(searchReversed1)[1].split(searchReversed2)[0].trim().includes("true"));

// 		s1.forEach((line) => {
// 			if (line.indexOf("//") != 0 && line.indexOf(searchString1) >= 0) {
// 				tmpLine.push(line);
// 				tmpWaypoints.push(line.split(searchString1)[1].split(searchString2)[0].split(","));
// 			}
// 		});

// 		if (tmpLine[0].indexOf(searchAdaption1) >= 0) {
// 			var adaptStr = tmpLine[0].split(searchAdaption1)[1].split(searchAdaption2)[0].trim();
// 			switch (adaptStr) {
// 				case "getAdaptedLeftSwitchWaypoint":
// 					$("#startAdaptionValue").val("startswitchleft").prop('selected', true);
// 					break;
// 				case "getAdaptedRightSwitchWaypoint":
// 					$("#startAdaptionValue").val("startswitchright").prop('selected', true);
// 					break;
// 				case "getAdaptedLeftScaleWaypoint":
// 					$("#startAdaptionValue").val("startscaleleft").prop('selected', true);
// 					break;
// 				case "getAdaptedRightScaleWaypoint":
// 					$("#startAdaptionValue").val("startscaleright").prop('selected', true);
// 					break;
// 				default:
// 					$("#startAdaptionValue").val("startnone").prop('selected', true);
// 					break;
// 			}
// 		} else {
// 			$("#startAdaptionValue").val("startnone").prop('selected', true);
// 		}

// 		if (tmpLine[tmpLine.length - 1].indexOf(searchAdaption1) >= 0) {
// 			var adaptStr = tmpLine[tmpLine.length - 1].split(searchAdaption1)[1].split(searchAdaption2)[0].trim();
// 			switch (adaptStr) {
// 				case "getAdaptedLeftSwitchWaypoint":
// 					$("#endAdaptionValue").val("endswitchleft").prop('selected', true);
// 					break;
// 				case "getAdaptedRightSwitchWaypoint":
// 					$("#endAdaptionValue").val("endswitchright").prop('selected', true);
// 					break;
// 				case "getAdaptedLeftScaleWaypoint":
// 					$("#endAdaptionValue").val("endscaleleft").prop('selected', true);
// 					break;
// 				case "getAdaptedRightScaleWaypoint":
// 					$("#endAdaptionValue").val("endscaleright").prop('selected', true);
// 					break;
// 				default:
// 					$("#endAdaptionValue").val("endnone").prop('selected', true);
// 					break;
// 			}
// 		} else {
// 			$("#endAdaptionValue").val("endnone").prop('selected', true);
// 		}

// 		waypoints = [];
// 		$("tbody").empty();
// 		tmpWaypoints.forEach((wptmp, i) => {
// 			var wp;
// 			var x = 0;
// 			var y = 0;
// 			var radius = 0;
// 			var speed = 0;
// 			var marker = "";
// 			if (wptmp.length >= 4) {
// 				x = wptmp[0];
// 				y = wptmp[1];
// 				radius = wptmp[2];
// 				speed = wptmp[3];
// 			}
// 			if (wptmp.length >= 5) {
// 				marker = wptmp[4].replace(/"/g, "");
// 			}

// 			if (marker == " undefined")
// 				marker = "";

// 			wp = new Waypoint(new Translation2d(x, y), speed, radius, marker);
// 			addRawPoint(wp.position.x, wp.position.y, wp.radius, wp.speed, wp.marker);
// 		});
// 		update();

// 		$('input').unbind("change paste keyup");
// 		$('input').bind("change paste keyup", function () {
// 			console.log("change");
// 			clearTimeout(wto);
// 			wto = setTimeout(function () {
// 				update();
// 			}, 500);
// 		});

// 	}
// 	fr.readAsText(file);
// 	update();
// }

//JSON Functions
// function importData() {
// 	$('#upl').click();
// 	let u = $('#upl')[0];
// 	$('#upl').change(() => {
// 		var file =  u.files[0];
// 		var fr = new FileReader();
// 		fr.onload = function(e) {
// 			var c = fr.result;
// 			let re = /(?:\/\/\sWAYPOINT_DATA:\s)(.*)/gm;
// 			let reversed = /(?:\/\/\sIS_REVERSED:\s)(.*)/gm;
// 			let title = /(?:\/\/\sFILE_NAME:\s)(.*)/gm;
// 			//console.log();
// 			$("#title").val(title.exec(c)[1]);
// 			$("#isReversed").prop('checked', reversed.exec(c)[1].includes("true"));
// 			let jde = re.exec(c)[1];
// 			let jd = JSON.parse(jde);
// 			// console.log(jd);
// 			waypoints = []
// 			$("tbody").empty();
// 			jd.forEach((wpd) => {
// 				let wp = new Waypoint(new Translation2d(wpd.position.x, wpd.position.y), wpd.speed, wpd.radius, wpd.marker, wpd.comment);
// 				// console.log(wp);
// 				$("tbody").append("<tr>"
// 					+"<td><input value='" + wp.position.x + "'></td>"
// 					+"<td><input value='" + wp.position.y + "'></td>"
// 					+"<td><input value='" + wp.radius + "'></td>"
// 					+"<td><input value='" + wp.speed + "'></td>"
// 					+"<td class='marker'><input placeholder='Marker' value='" + wp.marker + "'></td>"
// 					+"<td class='comments'><input placeholder='Comments' value='" + wp.comment + "'></td>"
// 					+"<td><button onclick='$(this).parent().parent().remove();''>Delete</button></td></tr>"
// 				);
// 			})
// 			update();
// 			$('input').unbind("change paste keyup");
// 			$('input').bind("change paste keyup", function() {
// 				console.log("change");
// 				clearTimeout(wto);
// 					wto = setTimeout(function() {
// 					update();
// 				}, 500);
// 			});
// 		}
// 		fr.readAsText(file);
// 	});
//     update();
// }
//
// function getDataString() {
// 	var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
// 	var pathInit = "";
// 	for(var i=0; i<waypoints.length; i++) {
// 		pathInit += "        " + waypoints[i].toString() + "\n";
// 	}
// 	var startPoint = "new Translation2d(" + waypoints[0].position.x + ", " + waypoints[0].position.y + ")";
// 	var importStr = "WAYPOINT_DATA: " + JSON.stringify(waypoints);
// 	var isReversed = $("#isReversed").is(':checked');
// 	var deg = isReversed ? 180 : 0;
// 	var str = `package org.usfirst.frc.team195.robot.Autonomous.Paths;
//
// import org.usfirst.frc.team195.robot.Utilities.TrajectoryFollowingMotion.*;
// import org.usfirst.frc.team195.robot.Utilities.TrajectoryFollowingMotion.PathBuilder.Waypoint;
//
// import java.util.ArrayList;
//
// public class ${title} implements PathContainer {
//
//     @Override
//     public Path buildPath() {
//         ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
// ${pathInit}
//         return PathBuilder.buildPathFromWaypoints(sWaypoints);
//     }
//
//     @Override
//     public RigidTransform2d getStartPose() {
//         return new RigidTransform2d(${startPoint}, Rotation2d.fromDegrees(${deg}));
//     }
//
//     @Override
//     public boolean isReversed() {
//         return ${isReversed};
//     }
// 	// ${importStr}
// 	// IS_REVERSED: ${isReversed}
// 	// FILE_NAME: ${title}
// }`
// 	return str;
// }

function getReducedDataString() {
	var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
	var pathInit = "";
	var startAdaptStr = $("#startAdaptionValue").val();
	var endAdaptStr = $("#endAdaptionValue").val();
	for (var i = 0; i < waypoints.length; i++) {
		pathInit += "        sWaypoints.add(";
		pathInit += waypoints[i].toString();
		pathInit += ");\n";
	}
	var startPoint = "new Translation2d(" + waypoints[0].position.x + ", " + waypoints[0].position.y + ")";
	var isReversed = $("#isReversed").is(':checked');
	var deg = isReversed ? 180 : 0;
	var str = `public class ${title} extends PathContainer {
    
  
	public ${title}() {
			this.sWaypoints = new ArrayList<Waypoint>();
	${pathInit}
		}

	@Override
    public boolean isReversed() {
        return ${isReversed}; 
	}

}`;
	return str;
}


function logWaypoints() {
	console.log("Waypoints length: " + waypoints.length);
	for (var i = 0; i < waypoints.length - 1; i++) {
		console.log(i + "\t" + waypoints[0].position.x + "\t" + waypoints[0].position.y + "\t" + waypoints[0].radius + "\t" + waypoints[0].speed);
	}
}


function newPathTwoPoints() {

	var length = $('tbody').children('tr').length;

	var points = [];

	//Push waypoints to points array
	var counter = 0;
	$('tbody').children('tr').each(function () {
		if (counter > length - 3) {
			var x = parseFloat(eval($($($(this).children()).children()[0]).val()));
			var y = parseFloat(eval($($($(this).children()).children()[1]).val()));
			var radius = parseFloat(eval($($($(this).children()).children()[2]).val()));
			var speed = parseFloat(eval($($($(this).children()).children()[3]).val()));
			if (isNaN(radius) || isNaN(speed)) {
				radius = 0;
				speed = 0;
			}
			var marker = ($($($(this).children()).children()[4]).val())
			var comment = ($($($(this).children()).children()[5]).val())
			points.push(new Waypoint(new Translation2d(x, y), speed, radius, marker, comment));
		}
		counter++;
	});

	console.log("Length: " + length);
	for (var i = points.length - 1; i >= 0; i--) {
		addRawPoint(points[i].position.x, points[i].position.y, points[i].radius, points[i].speed);
		console.log("Adding point " + i);
	}

	length = $('tbody').children('tr').length - 1;
	console.log("Length: " + length);

	for (var i = 1; i < length; i++) {
		document.getElementById("myTable").deleteRow(1);
	}

	update();
}

function addRawRow(val) {
	var x = parseFloat(eval($($($(val).children()).children()[0]).val()));
	var y = parseFloat(eval($($($(val).children()).children()[1]).val()));
	var radius = parseFloat(eval($($($(val).children()).children()[2]).val()));
	var speed = parseFloat(eval($($($(val).children()).children()[3]).val()));
	if (isNaN(radius) || isNaN(speed)) {
		radius = 0;
		speed = 0;
	}
	var marker = ($($($(this).children()).children()[4]).val())
	var comment = ($($($(this).children()).children()[5]).val())
	addRawPoint(x, y, radius, speed, marker);
}

function addRawPointAt(row, x, y, radius, speed, marker) {
	$('#myTable > tbody > tr').eq(row).after(getRowHTML(x, y, radius, speed, marker));
}
function getRowHTML(x, y, radius, speed, marker) {
	const row = $("tbody").children("tr").length;
	return "<tr>"
		+ "<td><input value='" + x + "'></td>"
		+ "<td><input value='" + y + "'></td>"
		+ "<td><input value='" + radius + "'></td>"
		+ "<td><input value='" + speed + "'></td>"
		+ "<td class='marker'><input placeholder='Marker' value='" + marker + "'></td>"
		+ (row == 0 ? "" : "<td><button onclick='$(this).parent().parent().remove();update();''>Delete</button></td>")
		+ "<td><input list='" + "angles" + "'value=''></td>"
		+ "<td><button background-color=#205c36 onclick='update();setAngle(findRowNumber(($(this).parent().parent()[0])))''>Set Angle</button></td>"
		+ "</tr>";
}

function addRawPoint(x, y, radius, speed, marker) {
	$("tbody").append(getRowHTML(x, y, radius, speed, marker));
}


function getDataString() {
	var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
	var folder = $("#folder").val();
	if (folder != undefined && folder != "")
		folder = "." + folder;
	else
		folder = "";

	var pathInit = "";
	var startAdaptStr = $("#startAdaptionValue").val();
	var endAdaptStr = $("#endAdaptionValue").val();
	for (var i = 0; i < waypoints.length; i++) {
		pathInit += "        sWaypoints.add(";
		pathInit += waypoints[i].toString();
		pathInit += ");\n";
	}
	var startPoint = "new Translation2d(" + waypoints[0].position.x + ", " + waypoints[0].position.y + ")";
	var isReversed = $("#isReversed").is(':checked');
	var deg = isReversed ? 180 : 0;
	var str = `package frc.robot.auto.commands.paths` + folder + `;

	import java.util.ArrayList;
	
	import frc.robot.auto.commands.functions.drive.pathfollowing.PathContainer;
	import frc.robot.auto.commands.functions.drive.pathfollowing.PathBuilder.Waypoint;

public class ${title} extends PathContainer {
    
    public ${title}() {
        this.sWaypoints = new ArrayList<Waypoint>();
${pathInit}
    }

	@Override
    public boolean isReversed() {
        return ${isReversed}; 
	}
}`;

	// str = str.replace(/[\u00bb\u00bf]/g, '');
	// str = str.replace("\u00bb",'');
	// str = str.replace("\u00bf",'');

	// str = str.replace(/[^A-Za-z 0-9 \.,\?""!@#\$%\^&\*\(\)-_=\+;:<>\/\\\|\}\{\[\]`~]*/g, '')

	// str = str.replace(/[\u00bb]/g,'');

	return str;
}

function exportData() {
	update();
	var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
	var blob = new Blob([getDataString()], { type: "text/plain;charset=utf-8" });
	saveAs(blob, title + ".java");
}

function showData() {
	update();
	var title = ($("#title").val().length > 0) ? $("#title").val() : "UntitledPath";
	$("#modalTitle").html(title + ".java");
	$(".modal > pre").text(getDataString());
	showModal();
}

function copyToClipBoard() {
	const data = document.createElement("textarea");
	// data.value = getReducedDataString(); 
	data.value = getDataString();

	// console.log(data.value);
	// console.log(document.body);

	document.body.appendChild(data);
	data.select();
	document.execCommand("copy");
	document.body.removeChild(data);
}

function showModal() {
	$(".modal, .shade").removeClass("behind");
	$(".modal, .shade").removeClass("hide");
}

function closeModal() {
	$(".modal, .shade").addClass("hide");
	setTimeout(function () {
		$(".modal, .shade").addClass("behind");
	}, 500);
}

var flipped = false;
function flipField() {
	flipped = !flipped;
	if (flipped)
		ctx.drawImage(imageFlipped, 0, 0, width, height);
	else
		ctx.drawImage(image, 0, 0, width, height);
	update();
}


function changeStartPoint() {
	var x = parseFloat($($($($('tbody').children()[0]).children()[0]).children()).val());
	var y = parseFloat($($($($('tbody').children()[0]).children()[1]).children()).val());

	var valueToSetTo = lastSetStartingPosition + 1;
	if (valueToSetTo > startingPositions.length - 1)
		valueToSetTo = 0;

	lastSetStartingPosition = valueToSetTo;
	setStartingPositionPoint(startingPositions[valueToSetTo][0], startingPositions[valueToSetTo][1]);
	return;
}

function changeStartPointV1() {
	var x = parseFloat($($($($('tbody').children()[0]).children()[0]).children()).val());
	var y = parseFloat($($($($('tbody').children()[0]).children()[1]).children()).val());

	for (var i = 0; i < startingPositions.length; i++) {
		if (x == startingPositions[i][0] && y == startingPositions[i][1]) {
			var valueToSetTo = i + 1;
			if (i >= startingPositions.length - 1)
				valueToSetTo = 0;

			setStartingPositionPoint(startingPositions[valueToSetTo][0], startingPositions[valueToSetTo][1]);
			return;
		}
	}
}

function copyLastPoint() {
	var length = $($('tbody').children('tr')).length - 1;
	var row = $($('tbody').children('tr'))[length];
	addRawRow(row);
	update();
}

function setStartingPositionPoint(x, y) {
	console.log('setting starting position to ' + "\t[" + x + "]\t[" + y + "]");
	$($($($('tbody').children()[0]).children()[0]).children()).val(x);
	$($($($('tbody').children()[0]).children()[1]).children()).val(y);
	update();
}

function oldChangeStartPoint() {
	if (parseFloat($($($($('tbody').children()[0]).children()[1]).children()).val()) == startLeftY) {
		$($($($('tbody').children()[0]).children()[1]).children()).val(startCenterY);
	} else if (parseFloat($($($($('tbody').children()[0]).children()[1]).children()).val()) == startCenterY) {
		$($($($('tbody').children()[0]).children()[1]).children()).val(startRightY);
	} else if (parseFloat($($($($('tbody').children()[0]).children()[1]).children()).val()) == startRightY) {
		$($($($('tbody').children()[0]).children()[1]).children()).val(startLeftY);
	}
	update();
}
function canvasClick(canvas, evt) {
	var mPos = getMousePos(canvas, evt);
	addPoint(mPos.x, mPos.y);

}

function storeNearestCoordinate(canvas, evt) {
	var mPos = getMousePos(canvas, evt);
	coordToScoot = findNearestCoord(mPos.x, mPos.y);
}

function findNearestCoord(x, y) {
	var rows = document.getElementById("myTable").rows;

	var distance = [];
	for (var i = 0; i < rows.length; i++) {
		var tempX = rows[i].cells[0].innerHTML;
		var tempY = rows[i].cells[1].innerHTML;

		tempX = innerHTMLToNumber(tempX);
		tempY = innerHTMLToNumber(tempY);

		var tempDistance = Math.sqrt(Math.pow(x - tempX, 2) + Math.pow(y - tempY, 2));
		distance.push(tempDistance);
	}

	var minDistance = 3452;
	var minDistanceSlot = -1;
	for (var i = 0; i < distance.length; i++) {
		if (distance[i] < minDistance) {
			minDistance = distance[i];
			minDistanceSlot = i;
		}
	}

	if (minDistance == 3452)
		return -1;

	return minDistanceSlot;
}

function getPlotterPoint(left, value) {
	var x;
	var y;

	var angle;
	switch (value) {
		case "feeder_station":
			angle = 180;
			x = 0;
			{
				const val = 25.72;
				if (left)
					y = fieldHeight - val;
				else
					y = val;
			}

			x += halfL;
			break;
		case "cargo_ship_face":
			angle = 360;
			x = 172.25 + 48;
			x -= halfL;
			{
				const val = (fieldHeight / 2.0);
				const diff = 10.88;
				if (left) {
					y = val + diff;
				} else {
					y = val - diff;
				}
			}

			break;
		case "cargo_ship_bay_1":

			x = 172.25 + 48 + 40.5;
			{
				const val = 133.13;
				if (left) {
					angle = 270;
					y = fieldHeight - val;
					y += halfL;
				} else {
					angle = 90;
					y = val;
					y -= halfL;
				}
			}

			break;
		case "cargo_ship_bay_2":
			x = 172.25 + 48 + 40.5 + 21.75;
			{
				const val = 133.13;
				if (left) {
					angle = 270;
					y = fieldHeight - val;
					y += halfL;
				} else {
					angle = 90;
					y = val;
					y -= halfL;
				}
			}
			break;
		case "cargo_ship_bay_3":
			x = 172.25 + 48 + 40.5 + (21.75 * 2);
			{
				const val = 133.13;
				if (left) {
					angle = 270;
					y = fieldHeight - val;
					y += halfL;
				} else {
					angle = 90;
					y = val;
					y -= halfL;
				}
			}
			break;
		case "rocket_closest":
			x = 166.57 + 48;
			{
				const temp = ((27.44 - 7.875) / 2.0) + 7.875;
				if (left) {
					y = fieldHeight - temp;
				} else {
					y = temp;
				}
			}

			{
				const toAngle = (left ? (90 - 61.25) : 61.25 + 90 - 180);
				const translation = translateAtAngle(x, y, toAngle, halfL);
				angle = toAngle;

				// addRawPoint(x,y,0,0,"TEST");

				x = translation.x;
				y = translation.y;
			}
			break;
		case "rocket_mid":
			x = 181.28 + 48;
			{
				const val = 27.44;
				if (left) {
					angle = 90;
					y = fieldHeight - val;
					y -= halfL;
				} else {
					angle = 270;
					y = val;
					y += halfL;
				}
			}
			break;
		case "rocket_far":
			x = (229.13 - (166.57 + 48)) + 229.13;
			// x = 195.99;

			{
				const temp = ((27.44 - 7.875) / 2.0) + 7.875;
				if (left) {
					y = fieldHeight - temp;
				} else {
					y = temp;
				}
			}

			{
				const toAngle = (left ? (270 + 61.25 - 180) : 90 - 61.25 + 180);
				const translation = translateAtAngle(x, y, toAngle, halfL);
				angle = toAngle;

				x = translation.x;
				y = translation.y;
			}

			break;
	}

	return {
		x: x,
		y: y,
		angle: angle
	};
}

function toRadians(angle) {
	return angle * (Math.PI / 180);
}

function toDegrees(angle) {
	return angle * (180 / Math.PI);
}

function angleCheck(angle_) {
	if (angle_ == 0)
		angle_ = 360;
	else if (angle_ <= 90 && angle_ >= 0) {
		if (angle_ < 180)
			angle_ += 180;
		else if (angle_ > 180)
			angle_ -= 180;
	} else if (angle_ >= 180 && angle_ <= 270) {
		if (angle_ < 180)
			angle_ += 180;
		else if (angle_ > 180)
			angle_ -= 180;
	}

	return angle_;
}

function movePointAroundPoint(x1_, y1_, angle_, x2_, y2_) {
	const radius = Math.sqrt(Math.pow((x1_ - x2_), 2) + Math.pow(y1_ - y2_, 2));

	angle_ = angleCheck(angle_);

	const xTemp = Math.cos(toRadians(angle_)) * radius;
	const yTemp = Math.sin(toRadians(angle_)) * radius;

	xDelta = copySign(xTemp, yTemp);
	yDelta = copySign(yTemp, xTemp);

	// console.log(y1_ + "\t" + angle_ + "\t" + radius + "\t" + toRadians(angle_) + "\t" + Math.cos(toRadians(angle_)) + "\t" + Math.cos(toRadians(angle_)) * radius);

	// if (angle_ > 90 && angle_ < 180) {
	// 	newX *= -1;
	// } else if (angle_ < 90 && angle_ > 0) {
	// 	newX *= -1;
	// 	newY *= -1;
	// } else if (angle_ < 360 && angle_ > 270) {
	// 	newY *= -1;
	// 	// } else if (angle_ >= 270 && angle_ < 360) {
	// }

	// switch (angle_) {
	// 	case 0:
	// 		newX *= -1;
	// 		break;
	// 	case 90:
	// 		newY *= -1;
	// 		break;
	// }

	var newX = eval(xDelta + "+" + x1_);
	var newY = eval(yDelta + "+" + y1_);

	newX = Math.round(newX * 1000.0) / 1000.0;
	newY = Math.round(newY * 1000.0) / 1000.0;

	return {
		x: newX,
		y: newY
		// x: 0,
		// y: 0
	}
}

function copySign(value, signToCopy) {
	var neg = false;
	if (signToCopy < 0)
		neg = true;

	if ((value > 0 && neg) || (value < 0 && !neg))
		value *= -1;

	return value;
}

function translateAtAngle(x_, y_, angle_, lengthAway_) {
	var xDelta, yDelta;

	angle_ = angleCheck(angle_);

	const xTemp = Math.cos(toRadians(angle_)) * lengthAway_;
	const yTemp = Math.sin(toRadians(angle_)) * lengthAway_;

	xDelta = copySign(xTemp, yTemp);
	yDelta = copySign(yTemp, xTemp);
	// console.log("Angle: " + angle_ + "\tXDelta: " + xDelta + "\tYDelta: " + yDelta);

	// if (angle_ <= 270 && angle_ > 180) {
	// 	// console.log("Angle case 1");
	// 	x_ -= xDelta;
	// 	y_ += yDelta;
	// } else if (angle_ <= 180 && angle_ > 90) {
	// 	// console.log("Angle case 2");
	// 	x_ -= xDelta;
	// 	y_ -= yDelta;
	// } else if (angle_ <= 90 && angle_ > 0) {
	// 	// console.log("Angle case 3");
	// 	x_ += xDelta;
	// 	y_ -= yDelta;
	// } else if (angle_ >= 270 && angle_ < 360) {
	// 	// console.log("Angle case 4");
	// 	x_ += xDelta;
	// 	y_ += yDelta;
	// }

	x_ += xDelta;
	y_ += yDelta;

	return {
		x: x_,
		y: y_
	};
}


function addPlotterPoint() {
	var left = $("#pointPlotterLeftRight").is(':checked');
	var thing = $("#pointPlotterPoint").val();

	var position = getPlotterPoint(left, thing);
	addPointRound(position.x, position.y, 15, 30, true);
	const size = $('tbody').children('tr').length;

	setRowValue(size - 1, 6, position.angle);
	setAngle(size);
	// setRowValue(size-1,2,15);
	setRowValue(size - 2, 2, 15);

	update();
	return;

	update();
}

function canvasDrag(canvas, evt) {
	if ($("#allowDrag").is(':checked')) {
		var mPos = getMousePos(canvas, evt);

		movePoint(coordToScoot - 1, Math.round(mPos.x), Math.round(mPos.y));
		// update();
	}
}

function innerHTMLToNumber(innerHTML) {
	var cell = innerHTML.split('"')[1];
	return parseFloat(cell);
}

function numberToInnerHTML(number) {
	var ret = "";

	ret = '<input value="' + number + '">';

	return ret;
}

function movePoint(pointNumber, newX, newY) {
	if (newX != 0 && newY != 0 && newX != 1 && newY != 1) {
		var row = document.getElementById("myTable").rows[pointNumber + 1];
		row.cells[0].innerHTML = numberToInnerHTML(newX);
		row.cells[1].innerHTML = numberToInnerHTML(newY);
	}

	// console.log($('tbody').children('tr')[pointNumber]);

}

function getMousePos(canvas, evt) {
	var rect = canvas.getBoundingClientRect(); // abs. size of element

	var scaleX = width / fieldWidth / 1.5;
	var scaleY = height / fieldHeight / 1.5;
	var xRet = (evt.clientX - rect.left) / scaleX;
	var yRet = (rect.height - (evt.clientY - rect.top)) / scaleY;

	if (xRet < 0 || yRet < 0) {
		xRet = 0;
		yRet = 0;
	}

	return {
		x: xRet,   // scale mouse coordinates after they have
		y: yRet	// been adjusted to be relative to element
	}
}

function lerpColor(color1, color2, factor) {
	var result = color1.slice();
	for (var i = 0; i < 3; i++) {
		result[i] = Math.round(result[i] + factor * (color2[i] - color1[i]));
	}
	return result;
}

function getColorForSpeed(speed) {
	var u = Math.max(0, Math.min(1, speed / maxSpeed));
	if (u < 0.5)
		return RGBToHex(lerpColor(minSpeedColor, [255, 255, 0], u * 2));
	return RGBToHex(lerpColor([255, 255, 0], maxSpeedColor, u * 2 - 1));

}

function hexToRGB(hex) {
	var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
	return result ? [
		parseFloat(result[1], 16),
		parseFloat(result[2], 16),
		parseFloat(result[3], 16)
	] : null;
}

function RGBToHex(rgb) {
	return "#" + ((1 << 24) + (rgb[0] << 16) + (rgb[1] << 8) + rgb[2]).toString(16).slice(1);
}

function getNextSpeed(prev) {
	for (var i = 0; i < waypoints.length - 1; i++) {
		if (waypoints[i] == prev)
			return waypoints[i + 1].speed;
	}
	return 0;
}
