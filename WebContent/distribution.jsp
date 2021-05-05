<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
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
/***
	function getComparison()
	{
			
		var postUrl = "${pageContext.request.contextPath}/compareNumber
			
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"title":title},
			success: function(data){
				    		
				var strs = new Array();
				strs = data.split(";");
				var str_dg = strs[0];
				var jsonObj = eval("("+str_dg+")");
			//	$('#summarydg').datagrid('loadData',jsonObj);
				    		
				//refresh the chart in center
				var options = {
					title: {
		            	text: 'Comments Number and Tweets Number',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: ''
		           		},
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'CSV',
		            	data: []
		        	}]
				};
		 			        
				var str_hc = strs[1];		    				    		
				var array1 = new Array();		    		
				array1 = str_hc.split("]");
				//alert(array1.length);//12
				var datestr="";
				var vaulestr="";
				var count=0;
				for(var j=0;j<array1.length-1;j++){
				    var array2 = new Array();
				    array2 = array1[j].split("\"");
				    if(array2.length>4){
				    	datestr = array2[1];
					    vaulestr = array2[3];			    			
					    var numb = parseFloat(vaulestr);
					    options.xAxis.categories.push(datestr);
				    	options.series[0].data.push(numb);
				    	count++;		    			
				    }		    					    			
				}
	      			        					
				$('#comparison').highcharts(options);			    						      
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	**/
	
	// the graph for continued days after the story disappears from Google News
	function getContinuedDays()
	{
		var date = document.getElementById("date").value;
		var postUrl = "${pageContext.request.contextPath}/ContinuedDays?sinceDate="+date;
			
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"sinceDate":date},
			success: function(data){
				var key1 = "lastDays";
				var key2 = "storyNumber";
				var jsonStr = data.substr(2, data.length-4);
			
				//show the chart for all continued days
				var options = {
					title: {
		            	text: 'User Continued Interest',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'Continued Days'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'Story Number'
		           		},
		           		min: 0,
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'stories',
		            	data: []
		        	}]
				};
				
				
				
				//show the chart for continued days no more than 30 days
				var options_specify = {
						title: {
			            	text: 'User Continued Interest In A Month',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Continued Days'
			           		},
			        		tickInterval: 1,
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Story Number'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'stories',
			            	data: []
			        	}]
					};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");	
				var numberNoMoreThan = 0;
				var numberMoreThan = 0;
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var datestr = jsonObj[''+key1+''];
					//alert(datestr);
					var vaulestr = jsonObj[''+key2+''];
					var numb = parseFloat(vaulestr);
					//set the value for all days
					options.xAxis.categories.push(datestr);
			    	options.series[0].data.push(numb);
			    	//set the value for specified days
			    	if(datestr <= 30)
			    	{
			    		options_specify.xAxis.categories.push(datestr);
			    		options_specify.series[0].data.push(numb);
			    		numberNoMoreThan = numberNoMoreThan + numb;
			    	}
			    	else
			    		numberMoreThan = numberMoreThan + numb;
				}
				
				//show the bar chart for the number in no more than 30 days
				var option_bar = {
						chart: {
			            	type: 'column'
			        	},
			        	title: {
			            	text: 'Percentage of Stories Number'
			        	},	        	
			        	xAxis: {
			            	categories: []
			        	},			        	
			        	/***
			        	yAxis: {
			            	min: 0,
			            	title: {
			                	text: 'Stories Number'
			            	},
			            	stackLabels: {
			                	enabled: true,
			               		style: {
			                    	fontWeight: 'bold',
			                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
			                	}
			            	}
			        	},
			        	****/			        	
			            yAxis: {
			                title: {
			                    text: 'Stories Percentage'
			                }

			            },
			            /***
			        	legend: {
			            	align: 'right',
			            	x: -30,
			            	verticalAlign: 'top',
			            	y: 25,
			            	floating: true,
			            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
			            	borderColor: '#CCC',
			            	borderWidth: 1,
			            	shadow: false
			        	},
			        	***/
			        	legend: {
			                enabled: false
			            },
			            /**
			        	tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
			        	},
			        	***/
			        	tooltip: {
			                headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
			                pointFormat: '<span style="color:{point.color}"></span> <b>{point.y:.2f}%</b> of total<br/>'
			            },
			        	/***
			        	plotOptions: {
			            	column: {
			                	stacking: 'normal',
			                	dataLabels: {
			                    	enabled: true,
			                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
			                    	style: {
			                        	textShadow: '0 0 3px black'
			                    	}
			                	}
			            	}
			        	},
			        	***/
			        	plotOptions: {
			                series: {
			                    borderWidth: 0,
			                    dataLabels: {
			                        enabled: true,
			                        format: '{point.y:.1f}%'
			                    }
			                }
			            },
			            /***
			        	series: [{		        		
			            	name: 'stories number',
			            	data: []		        	
			        	}]	
			        	***/
		            	series: [{
			                name: 'stories percentage',
			                colorByPoint: true,
			                data: []
			            }]
					};
				
				option_bar.xAxis.categories.push("<=30 days");
				//option_bar.series[0].data.push(numberNoMoreThan);
		    	option_bar.series[0].data.push(parseFloat(numberNoMoreThan)*100/parseFloat(numberNoMoreThan+numberMoreThan));
		    	option_bar.xAxis.categories.push(">30 days");
		    	//option_bar.series[0].data.push(numberMoreThan);
		    	option_bar.series[0].data.push(parseFloat(numberMoreThan)*100/parseFloat(numberNoMoreThan+numberMoreThan));
	      			        					
				$('#allContinuedDays').highcharts(options);	
				$('#continuedDaysBar').highcharts(option_bar);	
				$('#specifiedContinuedDays').highcharts(options_specify);
				
				//show the contined hour graph
				document.getElementById("continuedHourDiv").style.display = "";
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	//the graph for continued hours after the story disappears from Google News
	function getContinuedHours()
	{
		var date = document.getElementById("date").value;
		var fromHour = document.getElementById("continuedHourFrom").value;
		var toHour = document.getElementById("continuedHourTo").value;
		var bucketHour = document.getElementById("bucketHour").value;
		var postUrl = "${pageContext.request.contextPath}/ContinuedHours?sinceDate="
				+date+"&fromHour="+fromHour+"&toHour="+toHour+"&bucketHour="+bucketHour;		
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{sinceDate:'" + date + "',fromHour:'" + fromHour + "',toHour:'" + toHour+ "',bucketHour:'" + bucketHour + "'}",
			success: function(data){
				var key1 = "lastDays";
				var key2 = "storyNumber";
				var jsonStr = data.substr(2, data.length-4);
			
				//show the chart
				var options = {
					title: {
		            	text: 'User Continued Interest',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'Continued Hours'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'Story Number'
		           		},
		           		min: 0,
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'stories',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var datestr = jsonObj[''+key1+''];
					//alert(datestr);
					var vaulestr = jsonObj[''+key2+''];
					var numb = parseFloat(vaulestr);
					//alert(numb);
					options.xAxis.categories.push(datestr);
			    	options.series[0].data.push(numb);
				}				
	      			        					
				$('#continuedHour').highcharts(options);			    						      
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	
	//determine the dropdown list for top stories in the graph of comments number over time
	function showTopForCommentNumber()
	{
		var topNumber = document.getElementById("topForCommentNumber").value;
		var select = document.getElementById("chosenStoryForCommentNumber");
		
		//get the top stories with storyID and name
		var postUrl = "${pageContext.request.contextPath}/GetTopStory?topNumber="+topNumber;
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{topNumber:'" + topNumber + "'}",
			success: function(data){
				//remove the initial option
				select.remove(select.selectedIndex);
				
				//remove the first and last 3 characters
				var jsonStr = data.substr(3, data.length-6);

				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("\"],[\"");				
				
				//add each story record	to the dropdown list
				for(var i=0;i<jsonArray.length;i++)
				{	
					var option = document.createElement("option");
					var storyArray = new Array();	
					storyArray = jsonArray[i].split("\",\"");
				    option.text = storyArray[1];  //story name
				    option.value = storyArray[0];  //story id
				   
				    select.add(option);
				}	
		        
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
				
	}
	
	// the graph of comments number over time for specified story
	function getCommentNumber()
	{
		var storyID = document.getElementById("chosenStoryForCommentNumber").value;
		var postUrl = "${pageContext.request.contextPath}/CommentfNumber?storyID="+storyID;
		/***
		var story = document.getElementById("storyName").value;
		var fromDate = document.getElementById("fromDate").value;
		var toDate = document.getElementById("toDate").value;
		var postUrl = "${pageContext.request.contextPath}/CommentfNumber?story="+story+"&fromDate="+fromDate+"&toDate="+toDate;
		***/	
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"storyID":storyID},
			//data: "{story:'" + story + "',fromDate:'" + fromDate + "',toDate:'" + toDate + "'}",
			success: function(data){
				var key1 = "time";
				var key2 = "commentNumber";
				
				var strs = new Array();
	    		strs = data.split(";");
	    		var str_comNum = strs[0];
				var jsonStr = str_comNum.substr(2, str_comNum.length-4);
			
				//show the chart
				var options = {
					title: {
		            	text: 'Comments Number Over Time',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'Hour'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'Cumulative Comments Number'
		           		},
		           		min: 0,
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'number',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");	
				var lastJsonObj = JSON.parse("{"+jsonArray[jsonArray.length-1]+"}");
				var totalCom = parseFloat(lastJsonObj[''+key2+''])*0.995;
				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var datestr = jsonObj[''+key1+''];
					//alert(datestr);
					var vaulestr = jsonObj[''+key2+''];
					var numb = parseFloat(vaulestr);
					//alert(numb);
					if(numb <= totalCom)
					{
						options.xAxis.categories.push(datestr);
				    	options.series[0].data.push(numb);
					}
					else
						break;
				}
				      					
				$('#commentNumber').highcharts(options);	
				
				var hasPlotBand = false; 
				var fromNum = strs[1];
				var toNum = strs[2];
			    var chart = $('#commentNumber').highcharts();
			    if (!hasPlotBand) {
		            chart.xAxis[0].addPlotBand({
		                from: fromNum,
		                to: toNum,
		                color: 'rgba(68, 170, 213, .2)',
		                id: 'plot-band-1'
		            });            
		        } else {
		            chart.xAxis[0].removePlotBand('plot-band-1');
		        }
		        hasPlotBand = !hasPlotBand;
		        
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	//the graph of comment percentage when the story appears and disappears
	function getCommentPercentageForAll()
	{
		var storyID = 0;	
		var postUrl = "${pageContext.request.contextPath}/CommentPercentageInGoogleNews";
		//var postUrl = "${pageContext.request.contextPath}/CommentPercentageInGoogleNews?storyID="+storyID;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{},
			//data:{"storyID":storyID},
			success: function(data){
				var keyPercentage = "commentPercentage";
				//var keyNumb = "storyNumber";
				var keyNumb = "storyPercentage";
				
				var strs = new Array();
	    		strs = data.split(";");
	    		var str_appear = strs[0];
	    		var str_disappear = strs[1];
				
				var jsonAppStr = str_appear.substr(2, str_appear.length-4);
				var jsonDisappStr = str_disappear.substr(2, str_disappear.length-4);
			
				//show the density of comment percentage when appearing in Google News for all stories
				var options_appear = {
						   chart: {
					            type: 'column'
					        },
					        title: {
					            text: 'Distribution of Comments Percentage When Appearing in Google News'
					        },					        
					        xAxis: {
				            	categories: [],
				            	//min: 1,
					            title: {
					                text: 'Comment Percentage'
					            }
				        	},
					        yAxis: {
					            min: 0,
					            title: {
					                //text: 'Stories Number'
					            	text: 'Stories Percentage'
					            }
					        },
					        plotOptions: {				                
				                series: {
				            		pointPadding: 0,
				            		groupPadding: 0,
				        		}
				            },
					        legend: {
					            enabled: false
					        },
					        tooltip: {
				            	headerFormat: '<b>{point.x}</b><br/>',
				            	pointFormat: '{series.name}: {point.y}<br/>'
				        	},
					        series: [{
					            //name: 'Stories Number',
					            name: 'Stories Percentage',
					            data: [],
					            dataLabels: {
					            	enabled: true,		
					                align: 'center',
					                format: '{point.y:.1f}', // one decimal				                
					            }
					        }]
				};
				var options_appear2 = {
						chart: {
				            type: 'column'
				        },
				        title: {
				            text: 'Distribution of Comments Percentage When Disappearing in Google News'
				        },	
				        
				        xAxis: {
			            	categories: [],
			            	//min: 1,
				            title: {
				                text: 'Comment Percentage'
				            }
			        	},
				        yAxis: {
				            min: 0,
				            title: {
				                //text: 'Stories Number'
				            	text: 'Stories Percentage'
				            }
				        },
				        plotOptions: {				                
			                series: {
			            		pointPadding: 0,
			            		groupPadding: 0,
			            		pointWidth: 100
			        		}
			            },			            
				        legend: {
				            enabled: false
				        },
				        tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>'
			        	},
			        	series: [{
				            //name: 'Stories Number',
				            name: 'Stories Percentage',
				            data: [],
				            dataLabels: {
				                enabled: true,		
				                align: 'center',
				                format: '{point.y:.2f}', // two decimal
				            }
				        }]
				        	
				        	/**
				        	xAxis: {
				            	categories: [],
				            	title: {
					                text: 'Comment Percentage'
					            }
				        	},
				        	yAxis: {
				            	min: 0,
				            	title: {
				                	text: 'Stories Percentage'
				            	},
				            	stackLabels: {
				                	enabled: true,
				               		style: {
				                    	fontWeight: 'bold',
				                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
				                	}
				            	}
				        	},
				        	legend: {
				            	align: 'right',
				            	x: -30,
				            	verticalAlign: 'top',
				            	y: 25,
				            	floating: true,
				            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
				            	borderColor: '#CCC',
				            	borderWidth: 1,
				            	shadow: false
				        	},
				        	tooltip: {
				            	headerFormat: '<b>{point.x}</b><br/>',
				            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
				        	},
				        	plotOptions: {
				            	column: {
				                	stacking: 'normal',
				                	dataLabels: {
				                    	enabled: true,
				                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
				                    	style: {
				                        	textShadow: '0 0 3px black'
				                    	}
				                	}
				            	}
				        	},
					        series: [{
					            //name: 'Stories Number',
					            name: 'Stories Percentage',
					            data: []
					        }]
				        	**/
				};
				
				//show the density of comment percentage when disappearing in Google News for all stories
				var options_disappear = {
						chart: {
				            type: 'column'
				        },
				        title: {
				            text: 'Distribution of Comments Percentage When Disappearing in Google News'
				        },					        
				        xAxis: {
			            	categories: [],
			            	//min: 1,
				            title: {
				                text: 'Comment Percentage'
				            }
			        	},
				        yAxis: {
				            min: 0,
				            title: {
				                //text: 'Stories Number'
				            	text: 'Stories Percentage'
				            }
				        },
				        plotOptions: {				                
			                series: {
			            		pointPadding: 0,
			            		groupPadding: 0,
			        		}
			            },
				        legend: {
				            enabled: false
				        },
				        tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>'
			        	},
				        series: [{
				            //name: 'Stories Number',
				            name: 'Stories Percentage',
				            data: [],
				            dataLabels: {
				            	enabled: true,		
				                align: 'center',
				                format: '{point.y:.1f}', // one decimal			                
				            }
				        }]
				};
				var options_disappear2 = {
						chart: {
				            type: 'column'
				        },
				        title: {
				            text: 'Distribution of Comments Percentage When Disappearing in Google News'
				        },	
				        /**
				        xAxis: {
			            	categories: [],
			            	//min: 1,
				            title: {
				                text: 'Comment Percentage'
				            }
			        	},
				        yAxis: {
				            min: 0,
				            title: {
				                //text: 'Stories Number'
				            	text: 'Stories Percentage'
				            }
				        },
				        plotOptions: {				                
			                series: {
			            		pointPadding: 0,
			            		groupPadding: 0,
			            		pointWidth: 100
			        		}
			            },			            
				        legend: {
				            enabled: false
				        },
				        tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>'
			        	},
			        	series: [{
				            //name: 'Stories Number',
				            name: 'Stories Percentage',
				            data: [],
				            dataLabels: {
				                enabled: true,		
				                align: 'center',
				                format: '{point.y:.1f}', // one decimal
				            }
				        }]
			        	**/
			        	
			        	xAxis: {
			            	categories: [],
			            	title: {
				                text: 'Comment Percentage'
				            }
			        	},
			        	yAxis: {
			            	min: 0,
			            	title: {
			                	text: 'Stories Percentage'
			            	},
			            	stackLabels: {
			                	enabled: true,
			               		style: {
			                    	fontWeight: 'bold',
			                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
			                	}
			            	}
			        	},
			        	legend: {
			            	align: 'right',
			            	x: -30,
			            	verticalAlign: 'top',
			            	y: 25,
			            	floating: true,
			            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
			            	borderColor: '#CCC',
			            	borderWidth: 1,
			            	shadow: false
			        	},
			        	tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
			        	},
			        	plotOptions: {
			            	column: {
			                	stacking: 'normal',
			                	dataLabels: {
			                    	enabled: true,
			                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
			                    	style: {
			                        	textShadow: '0 0 3px black'
			                    	}
			                	}
			            	}
			        	},
				        series: [{
				            //name: 'Stories Number',
				            name: 'Stories Percentage',
				            data: []
				        }]
			        	
				};
				
				//set the info when appearing	        
				var jsonAppArray = new Array();		    		
				jsonAppArray = jsonAppStr.split("},{");	
				var numb50 = 0;
				var numb60 = 0;
				var numb70 = 0;
				for(var i=0;i<jsonAppArray.length;i++)
				{
					var str = "{"+jsonAppArray[i]+"}";  //construct the json string
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var percentstr = jsonObj[''+keyPercentage+''];
					var numbstr = jsonObj[''+keyNumb+''];
					var numb = parseFloat(numbstr);					
					options_appear.xAxis.categories.push(percentstr);
					options_appear.series[0].data.push(numb);	
					if(percentstr > 50)
						numb50 = numb50 + numb;
					if(percentstr > 60)
						numb60 = numb60 + numb;
					if(percentstr > 70)
						numb70 = numb70 + numb;
				}				
				//set the info when appearing, focus on bucket >=50, 60, 70
				options_appear2.xAxis.categories.push(">=50");
				options_appear2.series[0].data.push(numb50);
				options_appear2.xAxis.categories.push(">=60");
				options_appear2.series[0].data.push(numb60);
				options_appear2.xAxis.categories.push(">=70");
				options_appear2.series[0].data.push(numb70);
				
				//set the info when disappearing
				var jsonDisappArray = new Array();		    		
				jsonDisappArray = jsonDisappStr.split("},{");
				var numb80 = 0;
				var numb90 = 0;
				for(var i=0;i<jsonDisappArray.length;i++)
				{
					var str = "{"+jsonDisappArray[i]+"}";  //construct the json string
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var percentstr = jsonObj[''+keyPercentage+''];
					var numbstr = jsonObj[''+keyNumb+''];
					var numb = parseFloat(numbstr);
					options_disappear.xAxis.categories.push(percentstr);
					options_disappear.series[0].data.push(numb);
					if(percentstr > 80)
						numb80 = numb80 + numb;
					if(percentstr > 90)
						numb90 = numb90 + numb;
				}
				//set the info when disappearing, focus on bucket >= 80, 90
				options_disappear2.xAxis.categories.push(">=80");
				options_disappear2.series[0].data.push(numb80);				      					
				options_disappear2.xAxis.categories.push(">=90");
				options_disappear2.series[0].data.push(numb90);
				      					
				$('#appearCommentPercentage').highcharts(options_appear);
				$('#appearCommentPercentage2').highcharts(options_appear2);
				$('#disappearCommentPercentage').highcharts(options_disappear);	
				$('#disappearCommentPercentage2').highcharts(options_disappear2);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	
	//the graph to show comment duration for each article
	function getDurationSinceFirstComment()
	{
		var timeRangeIn = document.getElementById("timeRangeIn").value;
		var postUrl = "${pageContext.request.contextPath}/CommentLastedTimeRange?timeRangeIn="+timeRangeIn;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"timeRangeIn":timeRangeIn},
			
			success: function(data){
				
				var key1 = "commentTimeRange";
				var key2 = "articleNumber";
								
				var jsonStr = data.substr(2, data.length-4);
			
				//show the chart
				var options = {
					title: {
		            	text: '',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'Hour'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		        		min: 0,
		            	title: {
		                	text: 'Articles Number'
		           		},
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'number',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var datestr = jsonObj[''+key1+''];
					//alert(datestr);
					var vaulestr = jsonObj[''+key2+''];
					var numb = parseFloat(vaulestr);
					//alert(numb);
					options.xAxis.categories.push(datestr);
			    	options.series[0].data.push(numb);
				}
				      					
				$('#durationSinceFirstComment').highcharts(options);	
				
				
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	
	// the graph of article volume for top stories and outlets
	function getArticleVolume()
	{
		var topNumber = document.getElementById("topNumber").value;
		var postUrl = "${pageContext.request.contextPath}/ArticleVolume?topNumber="+topNumber;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"topNumber":topNumber},
			success: function(data){
				var keyStory = "story";
				var keyTotal = "totalArticleNumber";
				var keyKnown = "articlesFromKnown";
				var keyHasCom = "articlesWithComment";				
				var key1 = "fromFox";
				var key2 = "fromDailyMail";
				var key3 = "fromGuardian";
				var key4 = "fromWashingtonPost";
				var key5 = "fromNYTimes";
				var key6 = "fromWSJ";
				var key7 = "fromCNN";
				var key8 = "fromBBC";
				var key9 = "fromTIME";
				var key10 = "fromNYDailyNews";
				var jsonStr = data.substr(2, data.length-4);
			    
				//chart of total articles 
				var option = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Total Articles Number'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Articles Number'
		            	},
		            	stackLabels: {
		                	enabled: true,
		               		style: {
		                    	fontWeight: 'bold',
		                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
		                	}
		            	}
		        	},
		        	legend: {
		            	align: 'right',
		            	x: -30,
		            	verticalAlign: 'top',
		            	y: 25,
		            	floating: true,
		            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
		            	borderColor: '#CCC',
		            	borderWidth: 1,
		            	shadow: false
		        	},
		        	tooltip: {
		            	headerFormat: '<b>{point.x}</b><br/>',
		            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
		        	},
		        	plotOptions: {
		            	column: {
		                	stacking: 'normal',
		                	dataLabels: {
		                    	enabled: true,
		                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
		                    	style: {
		                        	textShadow: '0 0 3px black'
		                    	}
		                	}
		            	}
		        	},
		        	series: [{		        		
		            	name: 'Known Outlets',
		            	data: []		        	
		        	}, {
		            	name: 'Unknown Outlets',
		            	data: []
		        	}]	
				};
				
				//chart of total articles 
				var option_com = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Articles Number For Known Outlets'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Articles Number'
		            	},
		            	stackLabels: {
		                	enabled: true,
		               		style: {
		                    	fontWeight: 'bold',
		                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
		                	}
		            	}
		        	},
		        	legend: {
		            	align: 'right',
		            	x: -30,
		            	verticalAlign: 'top',
		            	y: 25,
		            	floating: true,
		            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
		            	borderColor: '#CCC',
		            	borderWidth: 1,
		            	shadow: false
		        	},
		        	tooltip: {
		            	headerFormat: '<b>{point.x}</b><br/>',
		            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
		        	},
		        	plotOptions: {
		            	column: {
		                	stacking: 'normal',
		                	dataLabels: {
		                    	enabled: true,
		                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
		                    	style: {
		                        	textShadow: '0 0 3px black'
		                    	}
		                	}
		            	}
		        	},
		        	series: [{		        		
		            	name: 'With Comment',
		            	data: []		        	
		        	}, {
		            	name: 'No Comment',
		            	data: []
		        	}]	
				};
				
				//chart of articles percentage from outlets
				var option_outlet = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Articles Percentage For Different Outlets'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Articles Percentage'
		            	}
		        	},
		        	tooltip: {
		                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
		                shared: true
		            },
		            plotOptions: {
		                column: {
		                    stacking: 'percent'
		                }
		            },
		        	series: [{
		        		name: 'Fox',
		            	data: []
		        	}, {
		        		name: 'Daily Mail',
		            	data: []
		        	}, {
		        		name: 'the Guardian',
		            	data: []
		        	}, {
		            	name: 'Washington Post',
		            	data: []
		        	}, {
		            	name: 'New York Times',
		            	data: []
		        	}, {
		            	name: 'WSJ',
		            	data: []
		        	}, {
		        		name: 'CNN',
		            	data: []
		        	}, {
		        		name: 'BBC',
		            	data: []
		        	}, {
		            	name: 'TIME',
		            	data: []
		        	}, {
		            	name: 'New York Daily News',
		            	data: []
		        	}, {
		            	name: 'Others',
		            	data: []
		        	}]	
				};
				
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string					
					var jsonObj = JSON.parse(str);	 //parse string to json object	
					
					var storyName = jsonObj[''+keyStory+''];
					var vaulestr = jsonObj[''+keyTotal+''];
					var numb = parseFloat(vaulestr);
					var vaulestrKnown = jsonObj[''+keyKnown+''];
					var numbKnown = parseFloat(vaulestrKnown);
					var vaulestrCom = jsonObj[''+keyHasCom+''];
					var numbCom = parseFloat(vaulestrCom);
					
					var vaule1 = jsonObj[''+key1+''];
					var numb1 = parseFloat(vaule1);
					var vaule2 = jsonObj[''+key2+''];
					var numb2 = parseFloat(vaule2);
					var vaule3 = jsonObj[''+key3+''];
					var numb3 = parseFloat(vaule3);
					var vaule4 = jsonObj[''+key4+''];
					var numb4 = parseFloat(vaule4);
					var vaule5 = jsonObj[''+key5+''];
					var numb5 = parseFloat(vaule5);
					var vaule6 = jsonObj[''+key6+''];
					var numb6 = parseFloat(vaule6);
					var vaule7 = jsonObj[''+key7+''];
					var numb7 = parseFloat(vaule7);
					var vaule8 = jsonObj[''+key8+''];
					var numb8 = parseFloat(vaule8);
					var vaule9 = jsonObj[''+key9+''];
					var numb9 = parseFloat(vaule9);
					var vaule10 = jsonObj[''+key10+''];
					var numb10 = parseFloat(vaule10);
					var others = numbCom-numb1-numb2-numb3-numb4-numb5-numb6-numb7-numb8-numb9-numb10;
					//alert(storyName+": "+numb+", "+numbCom+", "+numbNoCom);
					//set the value of total article volume
					option.xAxis.categories.push(storyName);
			    	option.series[0].data.push(numbKnown);
			    	option.series[1].data.push(numb-numbKnown);
			    	//set the value with comments
			    	option_com.xAxis.categories.push(storyName);
			    	option_com.series[0].data.push(numbCom);
			    	option_com.series[1].data.push(numbKnown-numbCom);
			    	//set the percentage value of outlets
			    	option_outlet.xAxis.categories.push(storyName);
			    	option_outlet.series[0].data.push(numb1);
			    	option_outlet.series[1].data.push(numb2);
			    	option_outlet.series[2].data.push(numb3);
			    	option_outlet.series[3].data.push(numb4);
			    	option_outlet.series[4].data.push(numb5);
			    	option_outlet.series[5].data.push(numb6);
			    	option_outlet.series[6].data.push(numb7);
			    	option_outlet.series[7].data.push(numb8);
			    	option_outlet.series[8].data.push(numb9);
			    	option_outlet.series[9].data.push(numb10);
			    	option_outlet.series[10].data.push(others);
				}				
	      			        					
				$('#articleVolume').highcharts(option);	
				$('#articleWithComment').highcharts(option_com);
				$('#articleOutlet').highcharts(option_outlet);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
		
	}
	
	// the graph of comment volume for top stories and outlets
	function getCommentVolume()
	{
		var topNumber = document.getElementById("topForComment").value;
		var postUrl = "${pageContext.request.contextPath}/CommentVolume?topNumber="+topNumber;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"topNumber":topNumber},
			success: function(data){
				var keyStory = "story";
				var keyTotal = "totalCommentNumber";				
				var key1 = "fromFox";
				var key2 = "fromDailyMail";
				var key3 = "fromGuardian";
				var key4 = "fromWashingtonPost";
				var key5 = "fromNYTimes";
				var key6 = "fromWSJ";
				var key7 = "fromCNN";
				var key8 = "fromBBC";
				var key9 = "fromTIME";
				var key10 = "fromNYDailyNews";
				var jsonStr = data.substr(2, data.length-4);
			    
				//chart of total Comments 
				var option = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Total Comments Number'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Comments Number'
		            	},
		            	stackLabels: {
		                	enabled: true,
		               		style: {
		                    	fontWeight: 'bold',
		                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
		                	}
		            	}
		        	},
		        	legend: {
		            	align: 'right',
		            	x: -30,
		            	verticalAlign: 'top',
		            	y: 25,
		            	floating: true,
		            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
		            	borderColor: '#CCC',
		            	borderWidth: 1,
		            	shadow: false
		        	},
		        	tooltip: {
		            	headerFormat: '<b>{point.x}</b><br/>',
		            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
		        	},
		        	plotOptions: {
		            	column: {
		                	stacking: 'normal',
		                	dataLabels: {
		                    	enabled: true,
		                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
		                    	style: {
		                        	textShadow: '0 0 3px black'
		                    	}
		                	}
		            	}
		        	},
		        	series: [{		        				            	
		            	name: 'Comments',
		            	data: []
		        	}]	
				};
				
				//chart of Comments percentage from outlets
				var option_outlet = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Comments Percentage For Different Outlets'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Comments Percentage'
		            	}
		        	},
		        	tooltip: {
		                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
		                shared: true
		            },
		            plotOptions: {
		                column: {
		                    stacking: 'percent'
		                }
		            },
		        	series: [{
		        		name: 'Fox',
		            	data: []
		        	}, {
		        		name: 'Daily Mail',
		            	data: []
		        	}, {
		        		name: 'the Guardian',
		            	data: []
		        	}, {
		            	name: 'Washington Post',
		            	data: []
		        	}, {
		            	name: 'New York Times',
		            	data: []
		        	}, {
		            	name: 'WSJ',
		            	data: []
		        	}, {
		        		name: 'CNN',
		            	data: []
		        	}, {
		        		name: 'BBC',
		            	data: []
		        	}, {
		            	name: 'TIME',
		            	data: []
		        	}, {
		            	name: 'New York Daily News',
		            	data: []
		        	}, {
		            	name: 'Others',
		            	data: []
		        	}]	
				};
				
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string					
					var jsonObj = JSON.parse(str);	 //parse string to json object	
					
					var storyName = jsonObj[''+keyStory+''];
					var vaulestr = jsonObj[''+keyTotal+''];
					var numb = parseFloat(vaulestr);
					
					var vaule1 = jsonObj[''+key1+''];
					var numb1 = parseFloat(vaule1);
					var vaule2 = jsonObj[''+key2+''];
					var numb2 = parseFloat(vaule2);
					var vaule3 = jsonObj[''+key3+''];
					var numb3 = parseFloat(vaule3);
					var vaule4 = jsonObj[''+key4+''];
					var numb4 = parseFloat(vaule4);
					var vaule5 = jsonObj[''+key5+''];
					var numb5 = parseFloat(vaule5);
					var vaule6 = jsonObj[''+key6+''];
					var numb6 = parseFloat(vaule6);
					var vaule7 = jsonObj[''+key7+''];
					var numb7 = parseFloat(vaule7);
					var vaule8 = jsonObj[''+key8+''];
					var numb8 = parseFloat(vaule8);
					var vaule9 = jsonObj[''+key9+''];
					var numb9 = parseFloat(vaule9);
					var vaule10 = jsonObj[''+key10+''];
					var numb10 = parseFloat(vaule10);
					var others = numb-numb1-numb2-numb3-numb4-numb5-numb6-numb7-numb8-numb9-numb10;
					//alert(storyName+": "+numb+", "+numbCom+", "+numbNoCom);
					//set the value of total comment volume
					option.xAxis.categories.push(storyName);
			    	option.series[0].data.push(numb);
			    	//set the percentage value of outlets
			    	option_outlet.xAxis.categories.push(storyName);
			    	option_outlet.series[0].data.push(numb1);
			    	option_outlet.series[1].data.push(numb2);
			    	option_outlet.series[2].data.push(numb3);
			    	option_outlet.series[3].data.push(numb4);
			    	option_outlet.series[4].data.push(numb5);
			    	option_outlet.series[5].data.push(numb6);
			    	option_outlet.series[6].data.push(numb7);
			    	option_outlet.series[7].data.push(numb8);
			    	option_outlet.series[8].data.push(numb9);
			    	option_outlet.series[9].data.push(numb10);
			    	option_outlet.series[10].data.push(others);
				}				
	      			        					
				$('#commentVolume').highcharts(option);	
				$('#commentOutlet').highcharts(option_outlet);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
		
	}	
	
	// to see the graph of user volume for top stories and outlets
	function viewUserForStory()
	{
		document.getElementById("userForOutlet").style.display="none";       //hidden
		document.getElementById("userForStoryOutlet").style.display="";  //show
		document.getElementById("userContributionForOutlet").style.display="none";       //hidden
	}
	
	// to see the graph of user contribution for specified outlet
	function viewUserContribution()
	{
		document.getElementById("userForOutlet").style.display="none";
		document.getElementById("userForStoryOutlet").style.display="none";		
		document.getElementById("userContributionForOutlet").style.display="";
	}
	
	// to see the graph of user volume for outlets
	function viewUser()
	{
		document.getElementById("userForOutlet").style.display="";
		document.getElementById("userForStoryOutlet").style.display="none";		
		document.getElementById("userContributionForOutlet").style.display="none";
		
		var postUrl = "${pageContext.request.contextPath}/UserVolumeForAllOutlets";
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{}",
			success: function(data){
				var keyName = "outletName";
				var keyTotalUser = "userNumber";	
				var keyTotalComment = "totalComment";
				var keyCommentFromAnonymous = "commentFromAnonymous";
				
				var jsonStr = data.substr(2, data.length-4);
				
				//chart of total users 
				var option = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Users Number For Each Outlet'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Users Number'
		            	},		            	
		            	stackLabels: {
		                	enabled: true,
		               		style: {
		                    	fontWeight: 'bold',
		                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
		                	}
		            	}
		        	},		        	
		        	plotOptions: {
		            	column: {
		                	stacking: 'normal',
		                	dataLabels: {
		                    	enabled: false,		                    	
		                    	style: {
		                        	textShadow: '0 0 3px black'
		                    	}
		                	}
		            	}
		        	},
			        legend: {
			            enabled: false
			        },
			        tooltip: {
		            	headerFormat: '<b>{point.x}</b><br/>',
		            	pointFormat: '{series.name}: {point.y}<br/>'
		        	},		        	
		        	series: [{
			            name: 'Users',
			            data: []			          
			        }]
				};
				
				//chart of comment number for each outlet
				var option_comment_outlet = {
						chart: {
			            	type: 'column'
			        	},
			        	title: {
			            	text: 'Comments Number For Each Outlet'
			        	},
			        	xAxis: {
			            	categories: []
			        	},
			        	yAxis: {
			            	min: 0,
			            	title: {
			                	text: 'Comments Number'
			            	},
			            	stackLabels: {
			                	enabled: true,
			               		style: {
			                    	fontWeight: 'bold',
			                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
			                	}
			            	}
			        	},
			        	legend: {
			            	align: 'right',
			            	x: -30,
			            	verticalAlign: 'top',
			            	y: 25,
			            	floating: true,
			            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
			            	borderColor: '#CCC',
			            	borderWidth: 1,
			            	shadow: false
			        	},
			        	tooltip: {
			            	headerFormat: '<b>{point.x}</b><br/>',
			            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
			        	},
			        	plotOptions: {
			            	column: {
			                	stacking: 'normal',
			                	dataLabels: {
			                    	enabled: true,
			                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
			                    	style: {
			                        	textShadow: '0 0 3px black'
			                    	}
			                	}
			            	}
			        	},
			        	series: [{		        				            	
			            	name: 'From Anonymous',
			            	data: []
			        	}, {		        				            	
			            	name: 'From Non-anonymous',
			            	data: []
			        	}]	
					};
				
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string					
					var jsonObj = JSON.parse(str);	 //parse string to json object	
					
					var name = jsonObj[''+keyName+''];
					var vaulestr = jsonObj[''+keyTotalUser+''];
					var numb = parseFloat(vaulestr);
					var comstr = jsonObj[''+keyTotalComment+''];
					var numbCom = parseFloat(comstr);
					var fromAnonystr = jsonObj[''+keyCommentFromAnonymous+''];
					var numbFromAnony = parseFloat(fromAnonystr);					
					
					//set the value of total users volume for each outlet
					option.xAxis.categories.push(name);
			    	option.series[0].data.push(numb);
			    	//set the value of comments number for each outlet
			    	option_comment_outlet.xAxis.categories.push(name);
			    	option_comment_outlet.series[0].data.push(numbFromAnony);
			    	option_comment_outlet.series[1].data.push(numbCom-numbFromAnony);
				}				
	      			        					
				$('#userVolumeForAllOutlet').highcharts(option);	
				$('#commentVolumeForAllOutlet').highcharts(option_comment_outlet);

			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
		
	}
	
	// the graph of user volume for top stories and outlets
	function getUserVolumeForStory()
	{
		var topNumber = document.getElementById("topForUser").value;
		var postUrl = "${pageContext.request.contextPath}/UserVolume?topNumber="+topNumber;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"topNumber":topNumber},
			success: function(data){
				var keyStory = "story";
				var keyTotal = "totalUserNumber";				
				var key1 = "fromFox";
				var key2 = "fromDailyMail";
				var key3 = "fromGuardian";
				var key4 = "fromWashingtonPost";
				var key5 = "fromNYTimes";
				var key6 = "fromWSJ";
				var key7 = "fromCNN";
				var key8 = "fromBBC";
				var key9 = "fromTIME";
				var key10 = "fromNYDailyNews";
				var jsonStr = data.substr(2, data.length-4);
			    
				//chart of total users 
				var option = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Total Users Number'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Users Number'
		            	},
		            	stackLabels: {
		                	enabled: true,
		               		style: {
		                    	fontWeight: 'bold',
		                    	color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
		                	}
		            	}
		        	},
		        	legend: {
		            	align: 'right',
		            	x: -30,
		            	verticalAlign: 'top',
		            	y: 25,
		            	floating: true,
		            	backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
		            	borderColor: '#CCC',
		            	borderWidth: 1,
		            	shadow: false
		        	},
		        	tooltip: {
		            	headerFormat: '<b>{point.x}</b><br/>',
		            	pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
		        	},
		        	plotOptions: {
		            	column: {
		                	stacking: 'normal',
		                	dataLabels: {
		                    	enabled: true,
		                    	color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
		                    	style: {
		                        	textShadow: '0 0 3px black'
		                    	}
		                	}
		            	}
		        	},
		        	series: [{		        				            	
		            	name: 'Users',
		            	data: []
		        	}]	
				};
				
				//chart of users percentage from outlets
				var option_outlet = {
					chart: {
		            	type: 'column'
		        	},
		        	title: {
		            	text: 'Users Percentage For Different Outlets'
		        	},
		        	xAxis: {
		            	categories: []
		        	},
		        	yAxis: {
		            	min: 0,
		            	title: {
		                	text: 'Users Percentage'
		            	}
		        	},
		        	tooltip: {
		                pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b> ({point.percentage:.0f}%)<br/>',
		                shared: true
		            },
		            
		            plotOptions: {
		                column: {
		                    stacking: 'percent'
		                }
		            },
		            
		        	series: [{
		        		name: 'Fox',
		            	data: []
		        	}, {
		        		name: 'Daily Mail',
		            	data: []
		        	}, {
		        		name: 'the Guardian',
		            	data: []
		        	}, {
		            	name: 'Washington Post',
		            	data: []
		        	}, {
		            	name: 'New York Times',
		            	data: []
		        	}, {
		            	name: 'WSJ',
		            	data: []
		        	}, {
		        		name: 'CNN',
		            	data: []
		        	}, {
		        		name: 'BBC',
		            	data: []
		        	}, {
		            	name: 'TIME',
		            	data: []
		        	}, {
		            	name: 'New York Daily News',
		            	data: []
		        	}
		        	/***, {
		            	name: 'Others',
		            	data: []
		        	}***/
		        	]	
				};
				
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string					
					var jsonObj = JSON.parse(str);	 //parse string to json object	
					
					var storyName = jsonObj[''+keyStory+''];
					var vaulestr = jsonObj[''+keyTotal+''];
					var numb = parseFloat(vaulestr);
					
					var vaule1 = jsonObj[''+key1+''];
					var numb1 = parseFloat(vaule1);
					var vaule2 = jsonObj[''+key2+''];
					var numb2 = parseFloat(vaule2);
					var vaule3 = jsonObj[''+key3+''];
					var numb3 = parseFloat(vaule3);
					var vaule4 = jsonObj[''+key4+''];
					var numb4 = parseFloat(vaule4);
					var vaule5 = jsonObj[''+key5+''];
					var numb5 = parseFloat(vaule5);
					var vaule6 = jsonObj[''+key6+''];
					var numb6 = parseFloat(vaule6);
					var vaule7 = jsonObj[''+key7+''];
					var numb7 = parseFloat(vaule7);
					var vaule8 = jsonObj[''+key8+''];
					var numb8 = parseFloat(vaule8);
					var vaule9 = jsonObj[''+key9+''];
					var numb9 = parseFloat(vaule9);
					var vaule10 = jsonObj[''+key10+''];
					var numb10 = parseFloat(vaule10);
					var others = numb-numb1-numb2-numb3-numb4-numb5-numb6-numb7-numb8-numb9-numb10;
					//alert(storyName+": "+numb+", "+numbCom+", "+numbNoCom);
					//set the value of total users volume
					option.xAxis.categories.push(storyName);
			    	option.series[0].data.push(numb);
			    	//set the percentage value of outlets
			    	option_outlet.xAxis.categories.push(storyName);
			    	option_outlet.series[0].data.push(numb1);
			    	option_outlet.series[1].data.push(numb2);
			    	option_outlet.series[2].data.push(numb3);
			    	option_outlet.series[3].data.push(numb4);
			    	option_outlet.series[4].data.push(numb5);
			    	option_outlet.series[5].data.push(numb6);
			    	option_outlet.series[6].data.push(numb7);
			    	option_outlet.series[7].data.push(numb8);
			    	option_outlet.series[8].data.push(numb9);
			    	option_outlet.series[9].data.push(numb10);
			    	//option_outlet.series[10].data.push(others);
				}				
	      			        					
				$('#userVolume').highcharts(option);	
				$('#userOutlet').highcharts(option_outlet);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
		
	}
	
	// get the chart of user comments contribution
	function getUserCommentContribution()
	{
		var outletName = document.getElementById("outletNameSelection").value;
		var postUrl = "${pageContext.request.contextPath}/UserCommentContribution?outletName="+outletName;
			
		$.ajax({
			url:postUrl,
			datatype:"json",
			data:{"outletName":outletName},
			success: function(data){
				var keyUser = "userPercentage";
				var keyComment = "commentPercentage";
				var jsonStr = data.substr(2, data.length-4);
			
				//show the chart
				var options = {
					title: {
		            	text: 'Users Contribution For Comments',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'non-anonymous users percentage'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'comments percentage'
		           		},
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'comment percentage',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var userstr = jsonObj[''+keyUser+''];
					//alert(userstr);
					var commentstr = jsonObj[''+keyComment+''];
					var comment = parseFloat(commentstr);
					//alert(comment);
					options.xAxis.categories.push(userstr);
			    	options.series[0].data.push(comment);
				}				
	      			        					
				$('#userCommentContribution').highcharts(options);			    						      
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	

	//The densities of the ratios between cumulative comment counts measured in two respective time.
	function getDensityOfRatioForAll()
	{
		document.getElementById("ratioForAll").style.display="";
		document.getElementById("ratioForTop").style.display="none";	

		var firstTime = document.getElementById("firstTime").value;
		var secondTime = document.getElementById("secondTime").value;
		var storyID = 0;

		var postUrl = "${pageContext.request.contextPath}/DensityOfRatio?firstTime="+firstTime+"&secondTime="+secondTime+"&storyID="+storyID;
		
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{firstTime:'" + firstTime + "',secondTime:'" + secondTime + "',storyID:'" + storyID + "'}",
			//data: "{story:'" + story + "',fromDate:'" + fromDate + "',toDate:'" + toDate + "',firstTime:'" + firstTime + "',secondTime:'" + secondTime + "'}",
			success: function(data){
				var keyDensity = "density";
				var keyRatio = "ratio";
								
				var jsonStr = data.substr(2, data.length-4);
			
				//show the density of ratio for all stories
				var options = {
						   chart: {
					            type: 'column'
					        },
					        title: {
					            text: 'Desity of the ratios between two cumulative comment counts'
					        },					        
					        xAxis: {
				            	categories: [],
				            	min: 1,
					            title: {
					                text: 'Relative comment count (logarithmical)'
					            }
				        	},
					        yAxis: {
					            min: 0,
					            title: {
					                text: 'Density'
					            }
					        },
					        plotOptions: {				                
				                series: {
				            		pointPadding: 0,
				            		groupPadding: 0,
				        		}
				            },
					        legend: {
					            enabled: false
					        },
					        tooltip: {
				            	headerFormat: '<b>{point.x}</b><br/>',
				            	pointFormat: '{series.name}: {point.y}<br/>'
				        	},
					        series: [{
					            name: 'Density',
					            data: [],
					            dataLabels: {
					                enabled: false,					                
					            }
					        }]
				};
				//show the log-log line chart for all story
				var options_loglog = {
					title: {
		            	text: 'log-log plot',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'log(N2/N1)'
		           		},
		        		tickInterval: 1,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'log density'
		           		},
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'log',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var ratiostr = jsonObj[''+keyRatio+''];
					var densitystr = jsonObj[''+keyDensity+''];
					var density = parseFloat(densitystr);
					//set density and ratio
					//alert(ratiostr/100);
					options.xAxis.categories.push(ratiostr/100);
			    	options.series[0].data.push(density);
			    	//set log-log
			    	if(density == 0)
			    		continue;
			    	var log = Math.log(density) / Math.log(10);
			    	options_loglog.xAxis.categories.push(ratiostr/100);
			    	options_loglog.series[0].data.push(log);
				}
				      					
				$('#densityOfRatioForAll').highcharts(options);				
				$('#loglogForAll').highcharts(options_loglog);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}
	
	function getDensityOfRatioForTop()
	{	
		
		var firstTime = document.getElementById("firstTime").value;
		var secondTime = document.getElementById("secondTime").value;
		var storyID = document.getElementById("chosenStoryForRatio").value;

		var postUrl = "${pageContext.request.contextPath}/DensityOfRatio?firstTime="+firstTime+"&secondTime="+secondTime+"&storyID="+storyID;
		//alert(storyID);
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{firstTime:'" + firstTime + "',secondTime:'" + secondTime + "',storyID:'" + storyID + "'}",
			success: function(data){
				var keyDensity = "density";
				var keyRatio = "ratio";
								
				var jsonStr = data.substr(2, data.length-4);
			
				//show the density of ratio for top stories
				var options = {
						   chart: {
					            type: 'column'
					        },
					        title: {
					            text: 'Desity of the ratios between two cumulative comment counts'
					        },					        
					        xAxis: {
				            	categories: [],
				            	
					            title: {
					                text: 'Relative comment count (logarithmical)'
					            }
				        	},
					        yAxis: {
					            min: 0,
					            title: {
					                text: 'Density'
					            }
					        },
					        plotOptions: {				                
				                series: {
				            		pointPadding: 0,
				            		groupPadding: 0,
				        		}
				            },
					        legend: {
					            enabled: false
					        },
					        tooltip: {
				            	headerFormat: '<b>{point.x}</b><br/>',
				            	pointFormat: '{series.name}: {point.y}<br/>'
				        	},
					        series: [{
					            name: 'Density',
					            data: [],
					            dataLabels: {
					                enabled: false,					                
					            }
					        }]
				};
				
				//show the log-log line chart for top story
				var options_loglog = {
					title: {
		            	text: 'log-log plot',
		           		x: -20 //center
		        	},
		        	xAxis: {
		        		title: {
		                	text: 'log(N2/N1)'
		           		},
		           		min: 0,
		        		categories: []
		        	},
		        	yAxis: {
		            	title: {
		                	text: 'log density'
		           		},
		            	plotLines: [{
		                	value: 0,
		                	width: 1,
		                	color: '#808080'
		            	}]
		        	},
		        	tooltip: {
		            	valueSuffix: ''
		        	},
		        	legend: {
		            	layout: 'vertical',
		            	align: 'right',
		            	verticalAlign: 'middle',
		            	borderWidth: 0
		        	},
		        	series: [{
		            	name: 'log',
		            	data: []
		        	}]
				};
		 			        
				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("},{");				
				for(var i=0;i<jsonArray.length;i++)
				{
					var str = "{"+jsonArray[i]+"}";  //construct the json string
					//alert("str: "+str);
					var jsonObj = JSON.parse(str);	 //parse string to json object		
					var ratiostr = jsonObj[''+keyRatio+''];
					var densitystr = jsonObj[''+keyDensity+''];
					var density = parseFloat(densitystr);
					//set the density and ratio
					options.xAxis.categories.push(ratiostr/100);
			    	options.series[0].data.push(density);
			    	//set the log-log value
			    	if(density == 0)
			    		continue;
			    	var log = Math.log(density)/Math.log(10);
			    	options_loglog.xAxis.categories.push(ratiostr/100);
			    	options_loglog.series[0].data.push(log);
				}				
				      					
				$('#densityOfRatioForTop').highcharts(options);				
				$('#loglogForTop').highcharts(options_loglog);	
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
	}

	function seeDensityOfRatioForTop()
	{
		document.getElementById("ratioForAll").style.display="none";
		document.getElementById("ratioForTop").style.display="";
	}
	
	
	//determine the dropdown list for top stories in the graph of persistence and decay
	function showTopForRatioDesity()
	{
		var topNumber = document.getElementById("topForRatioDesity").value;
		var select = document.getElementById("chosenStoryForRatio");
		
		//get the top stories with storyID and name
		var postUrl = "${pageContext.request.contextPath}/GetTopStory?topNumber="+topNumber;
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{topNumber:'" + topNumber + "'}",
			success: function(data){
				//remove the initial option
				select.remove(select.selectedIndex);
				
				//remove the first and last 3 characters
				var jsonStr = data.substr(3, data.length-6);

				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("\"],[\"");				
				
				//add each story record	to the dropdown list
				for(var i=0;i<jsonArray.length;i++)
				{	
					var option = document.createElement("option");
					var storyArray = new Array();	
					storyArray = jsonArray[i].split("\",\"");
				    option.text = storyArray[1];  //story name
				    option.value = storyArray[0];  //story id
				   
				    select.add(option);
				}	
		        
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
				
	}
	
	function viewAllArticle()
	{
		document.getElementById("commentPropertyForOutlet").style.display="none";
		document.getElementById("commentPropertyForStory").style.display="none";
		var outletName = "null";
		var storyID = "0";
		showNetworkProperties(outletName, storyID);		
	}
	
	function viewForOutlet()
	{						
		document.getElementById("commentPropertyForOutlet").style.display="";
		document.getElementById("commentPropertyForStory").style.display="none";
	}
	
	function viewForStory()
	{
		//show the downlist of top 10 stories
		var topNumber = 10;
		var select = document.getElementById("chosenStoryForCommentProperty");
		
		//get the top stories with storyID and name
		var postUrl = "${pageContext.request.contextPath}/GetTopStory?topNumber="+topNumber;
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{topNumber:'" + topNumber + "'}",
			success: function(data){
				//remove the initial option
				select.remove(select.selectedIndex);
				
				//remove the first and last 3 characters
				var jsonStr = data.substr(3, data.length-6);

				var jsonArray = new Array();		    		
				jsonArray = jsonStr.split("\"],[\"");				
				
				//add each story record	to the dropdown list
				for(var i=0;i<jsonArray.length;i++)
				{	
					var option = document.createElement("option");
					var storyArray = new Array();	
					storyArray = jsonArray[i].split("\",\"");
				    option.text = storyArray[1];  //story name
				    option.value = storyArray[0];  //story id
				   
				    select.add(option);
				}	
		        
				document.getElementById("commentPropertyForOutlet").style.display="none";
				document.getElementById("commentPropertyForStory").style.display="";
			},
			error: function(XMLHttpRequest, textStatus, errorThrow){
				alert(XMLHttpRequest.status);
				alert(XMLHttpRequest.readyState);
				alert(textStatus);
				alert(errorThrown);
			},
			beforeSend: function(){
				    //alert("loading");
			}			    	
		});
		
	}
	
	function showPropertiesForOutlet()
	{
		var outletName = document.getElementById("outletNameSelection2").value;
		var storyID = "0";
		showNetworkProperties(outletName, storyID);
	}
	
	function showPropertiesForStory()
	{
		var outletName = "null";
		var storyID = document.getElementById("chosenStoryForCommentProperty").value;
		showNetworkProperties(outletName, storyID);
	}
	
	//show the properties distribution of the comment network
	function showNetworkProperties(outletName, storyID)
	{
		
		var postUrl = "${pageContext.request.contextPath}/CommentNetworkProperties?outletName="+outletName+"&storyID="+storyID;
			
		$.ajax({
			url:postUrl,
			datatype:"json",
			data: "{outletName:'" + outletName + "',storyID:'" + storyID + "'}",
			success: function(data){
				var keyDensity = "density";
				var keyInterval = "interval";
				
				var strs = new Array();
	    		strs = data.split(";");
	    		var str_size = strs[0];
				var jsonSizeStr = str_size.substr(2, str_size.length-4);
				var str_depth = strs[1];
				var jsonDepthStr = str_depth.substr(2, str_depth.length-4);
				var str_logdepth = strs[2];
				var jsonLogDepthStr = str_logdepth.substr(2, str_logdepth.length-4);
				var str_width = strs[3];
				var jsonWidthStr = str_width.substr(2, str_width.length-4);
				var str_user = strs[4];
				var jsonUserStr = str_user.substr(2, str_user.length-4);				
				
				var options_size = {
						title: {
			            	text: 'Comment Size',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Log(Size)'
			           		},
			           		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Density'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'density',
			            	data: []
			        	}]
				};
				
				var options_logsize = {
						title: {
			            	text: 'Log-Log Plot of Comment Size',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Log(Size)'
			           		},
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Log(Density)'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'log(density)',
			            	data: []
			        	}]
				};
				
				var options_depth = {
						title: {
			            	text: 'Depth of Comment Tree',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Depth'
			           		},
			        		tickInterval: 1,
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Density'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'density',
			            	data: []
			        	}]
					};
				
				var options_logdepth = {
						title: {
			            	text: 'Log-Log Plot of Comment Tree Depth',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Log(Depth)'
			           		},
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Log(Density)'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'log(density)',
			            	data: []
			        	}]
				};
				
				var options_width = {
						title: {
			            	text: 'Width of Comment Tree',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Width'
			           		},
			        		tickInterval: 1,
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Density'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'density',
			            	data: []
			        	}]
					};
				
				var options_logwidth = {
						title: {
			            	text: 'Log-Log Plot of Comment Tree Width',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Log(Width)'
			           		},
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Log(Density)'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'log(density)',
			            	data: []
			        	}]
				};
				
				var options_user = {
						title: {
			            	text: 'Distribution Of Distinct User Number',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Distinct User Number'
			           		},
			        		tickInterval: 1,
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Density'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'density',
			            	data: []
			        	}]
					};
				
				var options_loguser = {
						title: {
			            	text: 'Log-Log Plot of Distinct User Number',
			           		x: -20 //center
			        	},
			        	xAxis: {
			        		title: {
			                	text: 'Log(User)'
			           		},
			        		categories: []
			        	},
			        	yAxis: {
			            	title: {
			                	text: 'Log(Density)'
			           		},
			           		min: 0,
			            	plotLines: [{
			                	value: 0,
			                	width: 1,
			                	color: '#808080'
			            	}]
			        	},
			        	tooltip: {
			            	valueSuffix: ''
			        	},
			        	legend: {
			            	layout: 'vertical',
			            	align: 'right',
			            	verticalAlign: 'middle',
			            	borderWidth: 0
			        	},
			        	series: [{
			            	name: 'log(density)',
			            	data: []
			        	}]
				};
				
					//for size
					var jsonSizeArray = new Array();		    		
					jsonSizeArray = jsonSizeStr.split("},{");	
					var sizeJsonObj = JSON.parse("{"+jsonSizeArray[jsonSizeArray.length-1]+"}");					
					for(var i=0;i<jsonSizeArray.length;i++)
					{
						var str = "{"+jsonSizeArray[i]+"}";  //construct the json string
						var jsonObj = JSON.parse(str);	 //parse string to json object		
						var intervalstr = jsonObj[''+keyInterval+''];
						var densitystr = jsonObj[''+keyDensity+''];
						var density = parseFloat(densitystr);//take log for the density
						var log = 0;
						if(density > 0)
							log = Math.log(density)/Math.log(10);
						
						options_size.xAxis.categories.push(intervalstr);
						options_size.series[0].data.push(density);
						options_logsize.xAxis.categories.push(intervalstr);
						options_logsize.series[0].data.push(log);
					}
			 		
					//for depth
					var jsonDepthArray = new Array();		    		
					jsonDepthArray = jsonDepthStr.split("},{");					
					for(var i=0;i<jsonDepthArray.length;i++)
					{
						var str = "{"+jsonDepthArray[i]+"}";  //construct the json string
						var jsonObj = JSON.parse(str);	 //parse string to json object		
						var intervalstr = jsonObj[''+keyInterval+''];
						var densitystr = jsonObj[''+keyDensity+''];
						var numb = parseFloat(densitystr);
						options_depth.xAxis.categories.push(intervalstr);
						options_depth.series[0].data.push(numb);
					}
					
					//for log depth
					var jsonLogDepthArray = new Array();		    		
					jsonLogDepthArray = jsonLogDepthStr.split("},{");		
					for(var i=0;i<jsonLogDepthArray.length;i++)
					{
						var str = "{"+jsonLogDepthArray[i]+"}";  //construct the json string
						var jsonObj = JSON.parse(str);	 //parse string to json object		
						var intervalstr = jsonObj[''+keyInterval+''];
						var densitystr = jsonObj[''+keyDensity+''];
						var density = parseFloat(densitystr);
						var log = 0;
						if(density > 0)
							log = Math.log(density)/Math.log(10);
						else
							continue;
						options_logdepth.xAxis.categories.push(intervalstr);
						options_logdepth.series[0].data.push(log);
					}
					
					//for width
					var jsonWidthArray = new Array();		    		
					jsonWidthArray = jsonWidthStr.split("},{");					
					for(var i=0;i<jsonWidthArray.length;i++)
					{
						var str = "{"+jsonWidthArray[i]+"}";  //construct the json string
						var jsonObj = JSON.parse(str);	 //parse string to json object		
						var intervalstr = jsonObj[''+keyInterval+''];
						var densitystr = jsonObj[''+keyDensity+''];
						var numb = parseFloat(densitystr);
						var log = 0;
						if(numb > 0)
							log = Math.log(numb)/Math.log(10);
						options_width.xAxis.categories.push(intervalstr);
						options_width.series[0].data.push(numb);
						options_logwidth.xAxis.categories.push(intervalstr);
						options_logwidth.series[0].data.push(log);
					}
					
					//for distinct user number
					var jsonUserArray = new Array();		    		
					jsonUserArray = jsonUserStr.split("},{");					
					for(var i=0;i<jsonUserArray.length;i++)
					{
						var strUser = "{"+jsonUserArray[i]+"}";  //construct the json string
						var jsonObj = JSON.parse(strUser);	 //parse string to json object		
						var intervalstr = jsonObj[''+keyInterval+''];
						var densitystr = jsonObj[''+keyDensity+''];
						var numb = parseFloat(densitystr);
						var log = 0;
						if(numb > 0)
							log = Math.log(numb)/Math.log(10);
						options_user.xAxis.categories.push(intervalstr);
						options_user.series[0].data.push(numb);
						options_loguser.xAxis.categories.push(intervalstr);
						options_loguser.series[0].data.push(log);
					}
					  
					$('#size').highcharts(options_size);
					$('#loglogSize').highcharts(options_logsize);							
					$('#depth').highcharts(options_depth);	
					$('#loglogDepth').highcharts(options_logdepth);
					$('#width').highcharts(options_width);
					$('#loglogWidth').highcharts(options_logwidth);
					$('#userNumber').highcharts(options_user);
					$('#loglogUserNumber').highcharts(options_loguser);
			}		    	
		});
	}
	
	
</script>

<body>
     <div class="easyui-tabs" style="width:auto;height:auto">
     	<div title="User Continued Interest" style="padding:10px">
     		<h1>This is to show the distribution of user's continued interest after the story disappears from Google News.</h1>
     		<h2>Please enter the time after which the stories appear in Google News: </h2>
     		<input type="text" id="date" name="date" size=12 value="2015-10-01" >
     		<input type="button" value="view graph" onclick="getContinuedDays()">
     		<div id="allContinuedDays" style="width:800px;height:500px"></div>
     		<div id="continuedDaysBar" style="width:800px;height:400px"></div>
     		<div id="specifiedContinuedDays" style="width:800px;height:500px"></div>
     		
     		<div id="continuedHourDiv" style="display: none;">
     			Please specify the time range: 
     			<input type="text" id="continuedHourFrom" name="continuedHourFrom" size=3 value="1" > - 
     			<input type="text" id="continuedHourTo" name="continuedHourTo" size=4 value="480" > hours, 
     			<input type="text" id="bucketHour" name="bucketHour" size=2 value="6" > hours in each bucket  
     			<input type="button" value="submit" onclick="getContinuedHours()">
     			<div id="continuedHour" style="width:800px;height:500px"></div>
     		</div>     		
     	</div>
     	
     	<div title="User Response Delay" style="padding:10px">
     		<h1>This is to show the delay distribution of user response after article is published.</h1>
     	</div>
     	
     	<div title="Comments Number Over Time" style="padding:10px">
     		<h1>This is to show the comments number over time.</h1>
     		<div style="display: none;">
     			<h2>Please enter the story and time range: </h2>
     			<label for="storyName">story:</label>
  				<input type="text" id="storyName" name="storyName" size=15 value="Kanye West" ><br>
  				<label for="fromDate">from:</label>
  				<input type="text" id="fromDate" name="fromDate" size=20 value="2016-01-01" >
  				<label for="toDate">to:</label>
  				<input type="text" id="toDate" name="toDate" size=20 value="2016-03-01" ><br>
     		</div>    		
  			
  			<p>
     			Specify the number of top stories to  
     			<input type="text" id="topForCommentNumber" name="topForCommentNumber" size=5 value="10" >
     			<input type="button" value="show top stories list" onclick="showTopForCommentNumber()">
     		</p> 
     		<p>
     			Specify the story to  
     			<select id="chosenStoryForCommentNumber" name="chosenStoryForCommentNumber">
     			</select>
     		</p>
  			<input type="button" value="view comment number over time" onclick="getCommentNumber()">
  			<div id="commentNumber" style="width:1000px;height:500px"></div> 			  			
  		</div> 
  		
  		<div title="Comments Distribution with Story Change" style="padding:10px">
  			<h1>This is to show the comment distribution when the story appears and disappears in Google News.</h1>
  			<input type="button" value="view comment-story distribution in Google News" onclick="getCommentPercentageForAll()">
  			<div id="appearCommentPercentage" style="width:1000px;height:500px"></div>
  			<div id="appearCommentPercentage2" style="width:1000px;height:500px"></div>
  			<div id="disappearCommentPercentage" style="width:1000px;height:500px"></div>
  			<div id="disappearCommentPercentage2" style="width:1000px;height:500px"></div> 		
  		</div>		
     	
     	<div title="User Interest Duration" style="padding:10px">
     		<h1>This is to show the distribution of user interest duration after the first comment is posted.</h1>
     		Specify the time range for comments to be 
     		<input type="text" id="timeRangeIn" name="timeRangeIn" size=5 value="240" > hours
     		<input type="button" value="view duration distribution" onclick="getDurationSinceFirstComment()">
  			<div id="durationSinceFirstComment" style="width:1000px;height:500px"></div>
  			
     		<h1>This is to show the distribution of user interest duration after article is published.</h1>
     	</div>
     	
     	<div title="Article Volume For Top Stories" style="padding:10px">
     		<h1>This is to show the articles volume for top stories and outlets.</h1>
     		<p>
     			Specify the number of top stories to  
     			<input type="text" id="topNumber" name="topNumber" size=5 value="5" >
     		</p>
     		<input type="button" value="view articles volume" onclick="getArticleVolume()">
     		<div id="articleVolume" style="width:1000px;height:400px"></div>
     		<div id="articleWithComment" style="width:1000px;height:400px"></div>
     		<div id="articleOutlet" style="width:1000px;height:600px"></div>
     	</div>
     	
     	<div title="Comment Volume For Top Stories" style="padding:10px">
     		<h1>This is to show the comments volume for top stories and outlets.</h1>
     		<p>
     			Specify the number of top stories to  
     			<input type="text" id="topForComment" name="topForComment" size=5 value="5" >
     		</p>
     		<input type="button" value="view comments volume" onclick="getCommentVolume()">
     		<div id="commentVolume" style="width:1000px;height:400px"></div>
     		<div id="commentOutlet" style="width:1000px;height:600px"></div>
     	</div>
     	
     	<div title="User Activity" style="padding:10px">
     	
     		<input type="button" value="view user volume" onclick="viewUser()">
     		<input type="button" value="view user volume for stories" onclick="viewUserForStory()"> 
     		<input type="button" value="view user contribution" onclick="viewUserContribution()">    		
     		
     		<div id="userForOutlet" style="display: none;">
     			<h1>This is to show the users number for each outlet.</h1>
     			<div id="userVolumeForAllOutlet" style="width:1000px;height:500px"></div>
     			<h1>This is to show the comment number for each outlet.</h1>
     			<div id="commentVolumeForAllOutlet" style="width:1000px;height:500px"></div>
     		</div>
     		
     		<div id="userForStoryOutlet" style="display: none;">
     			<h1>This is to show the users volume for top stories and outlets.</h1>
     			<p>
     			Specify the number of top stories to  
     			<input type="text" id="topForUser" name="topForUser" size=5 value="5" >
     			</p>
     			<input type="button" value="submit" onclick="getUserVolumeForStory()">
     			<div id="userVolume" style="width:1000px;height:400px"></div>
     			<div id="userOutlet" style="width:1000px;height:600px"></div>
     		</div>
     		
     		<div id="userContributionForOutlet" style="display: none;">	
     			<h1>This is to show the users contribution for comments.</h1>
     			<p>
     			Specify the outlet to  
     			<select id="outletNameSelection" name="outletNameSelection">
     				<option>Daily Mail</option>
     				<option>theguardian</option>
     				<option>washingtonpost</option>
     				<option>foxnews</option>
     				<option>New York Times</option>
     				<option>wsj</option>
     				<option>cnn</option>
     				<option>bbc</option>
     				<option>TIME</option>
     				<option>AlJazeera.com</option>
     				<option>MarketWatch</option>
     				<option>The Seattle Times</option>
     				<option>New York Post</option>
     				<option>VentureBeat</option>
     				<option>Las Vegas Sun</option>
     				<option>startribune</option>
     				<option>New York Daily News</option>
     			</select>
     			</p>
     			<input type="button" value="submit" onclick="getUserCommentContribution()">
     			<div id="userCommentContribution" style="width:1000px;height:500px"></div>
     		</div>
     	    		
     	</div>   	
     	
  		<div title="Persistence and Decay" style="padding:10px">
  			<h1>This is to show the densities of the ratios between cumulative comment counts measured in two respective time ranges.</h1>
  			<p>
  				<label for="firstTime">First Time:</label>
  				<input type="text" id="firstTime" name="firstTime" size=3 value="2" >hours, 
  				<label for="secondTime">Second Time:</label>
  				<input type="text" id="secondTime" name="secondTime" size=3 value="10" >hours,<br>
  			</p>
  			<input type="button" value="view distribution for all stories" onclick="getDensityOfRatioForAll()">
     		<input type="button" value="view distribution for chosen story" onclick="seeDensityOfRatioForTop()">
     				  			
  			<div id="ratioForAll">
  				<div id="densityOfRatioForAll" style="width:1000px;height:500px"></div>
  				<div id="loglogForAll" style="width:1000px;height:500px"></div>
  			</div>
  			
  			<div id="ratioForTop" style="display: none;">
  				<p>
     				Specify the number of top stories to  
     				<input type="text" id="topForRatioDesity" name="topForRatioDesity" size=5 value="10" >
     				<input type="button" value="show top stories list" onclick="showTopForRatioDesity()">
     			</p> 
  				<p>
     				Specify the story to  
     				<select id="chosenStoryForRatio" name="chosenStoryForRatio">
     				</select>
     				<input type="button" value="submit" onclick="getDensityOfRatioForTop()">
     			</p>    			
     			<div id="densityOfRatioForTop" style="width:1000px;height:500px"></div>
     			<div id="loglogForTop" style="width:1000px;height:500px"></div>
  			</div>
  			
     	</div>
     	
     	<div title="Comment Property" style="padding:10px">
     		<h1>This is to show the properties of comment.</h1>
     		<input type="button" value="For All" onclick="viewAllArticle()">
     		<input type="button" value="Per Outlet" onclick="viewForOutlet()"> 
     		<input type="button" value="Per Story" onclick="viewForStory()"> 
     		
     		<div id="commentPropertyForOutlet" style="display: none;">
     			<p>
     			Specify the outlet to  
     			<select id="outletNameSelection2" name="outletNameSelection2">
     				<option>Daily Mail</option>
     				<option>theguardian</option>
     				<option>washingtonpost</option>
     				<option>foxnews</option>
     				<option>New York Times</option>
     				<option>wsj</option>
     				<option>MarketWatch</option>
     				<option>cnn</option>
     				<option>bbc</option>
     				<option>TIME</option>
     				<option>The Seattle Times</option>
     				<option>AlJazeera.com</option>
     				<option>New York Post</option>
     				<option>VentureBeat</option>
     				<option>Las Vegas Sun</option>
     				<option>startribune</option>
     				<option>New York Daily News</option> 				
     			</select>
     			</p>
     			<input type="button" value="submit" onclick="showPropertiesForOutlet()">
     		</div>
     		
     		<div id="commentPropertyForStory" style="display: none;">
     			<p>
     			Specify the story to  
     			<select id="chosenStoryForCommentProperty" name="chosenStoryForCommentProperty">     				    				
     			</select>
     			</p>
     			<input type="button" value="submit" onclick="showPropertiesForStory()">
     		</div>
     		  
     		<div style="width:1200px;height:400px">
     			<div id="depth" style="width:60%;float:left"></div>
     			<div id="loglogDepth" style="width:40%;float:left"></div>
     		</div>   		
     		<div style="width:1200px;height:400px">
     			<div id="size" style="width:60%;float:left"></div>
     			<div id="loglogSize" style="width:40%;float:left"></div>
     		</div>    		
     		<div style="width:1200px;height:400px">
     			<div id="width" style="width:60%;float:left"></div>
     			<div id="loglogWidth" style="width:40%;float:left"></div>
     		</div>
     		<div style="width:1200px;height:400px">
     			<div id="userNumber" style="width:60%;float:left"></div>
     			<div id="loglogUserNumber" style="width:40%;float:left"></div>
     		</div>
     		
     	</div>
     	
     	<div title="Comparison" style="padding:10px">
     		<h1>This is to show the comparison of number between Google News and Twitter.</h1>
     		<input type="button" value="view graph" onclick="getComparison()">
     		<div id="comparison" style="width:800px;height:500px"></div>
     	</div>
     	
     </div>
 
</body>
</html>