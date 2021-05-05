<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>Distribution Page</title>
    <link rel="stylesheet" type="text/css" href="EasyUI/themes/gray/easyui.css">
	<link rel="stylesheet" type="text/css" href="EasyUI/themes/icon.css">
	<script type="text/javascript" src="EasyUI/jquery.min.js"></script>
	<script type="text/javascript" src="EasyUI/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="Highcharts/js/highcharts.js"></script>
	<script type="text/javascript" src="Highcharts/js/modules/exporting.js"></script>
</head>

<script type="text/javascript">
	$(function () {
	    $('#container').highcharts({
	        chart: {
	            type: 'areaspline'
	        },
	        title: {
	            text: 'Average fruit consumption during one week'
	        },
	        legend: {
	            layout: 'vertical',
	            align: 'left',
	            verticalAlign: 'top',
	            x: 150,
	            y: 100,
	            floating: true,
	            borderWidth: 1,
	            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
	        },
	        xAxis: {
	            categories: [
	                'Monday',
	                'Tuesday',
	                'Wednesday',
	                'Thursday',
	                'Friday',
	                'Saturday',
	                'Sunday'
	            ]
	        },
	        yAxis: {
	            title: {
	                text: 'Fruit units'
	            }
	        },
	        tooltip: {
	            shared: true,
	            valueSuffix: ' units'
	        },
	        credits: {
	            enabled: false
	        },
	        plotOptions: {
	            areaspline: {
	                fillOpacity: 0.5   //transparence
	            }
	        },
	        series: [{
	            name: 'John',
	            data: [3, 4, 3, 5, 4, 10, 12]
	        }, {
	            name: 'Jane',
	            data: [1, 3, 4, 3, 3, 5, 4]
	        }]
	    });
	    
	    var hasPlotBand = false; 
	    var chart = $('#container').highcharts();
	    if (!hasPlotBand) {
            chart.xAxis[0].addPlotBand({
                from: 4.5,
                to: 6,
                color: 'rgba(68, 170, 213, .2)',
                id: 'plot-band-1'
            });            
        } else {
            chart.xAxis[0].removePlotBand('plot-band-1');
        }
        hasPlotBand = !hasPlotBand;
	    
	});
 </script>


<body>
		<div id="container" style="width: 900px; height: 500px"></div>
</body>
</html>