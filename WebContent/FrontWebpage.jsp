<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<html>
<head>
	<meta charset="UTF-8">
	<title>Google News Crawler Demo</title>
	<link rel="stylesheet" type="text/css" href="EasyUI/themes/gray/easyui.css">
	<link rel="stylesheet" type="text/css" href="EasyUI/themes/icon.css">
	<script type="text/javascript" src="EasyUI/jquery.min.js"></script>
	<script type="text/javascript" src="EasyUI/jquery.easyui.min.js"></script>
	  <script type="text/javascript" src="Highcharts/js/highcharts.js"></script>
  <script type="text/javascript" src="Highcharts/js/modules/exporting.js"></script>
	
	 <script type="text/javascript">
	 

	 
	  $(function () {
		    $('#container').highcharts({
		        title: {
		            text: 'Comments Number In a Week',
		            x: -20 //center
		        },
		        xAxis: {
		            categories: ['7 days before', '6 days before', '5 days before', '4 days before', '3 days before', '2 days before','yesterday', 'today']
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
		            name: 'Iran',
		            data: [300, 202, 950, 145, 182, 215, 952]
		        }, {
		            name: 'Jordan Spieth',
		            data: [2, 8, 57, 113, 170, 220, 248]
		        }, {
		            name: 'New Horizons',
		            data: [9, 6, 35, 84, 135, 170, 186]
		        }, {
		            name: 'Mexico',
		            data: [309, 402, 57, 85, 119, 152, 170]
		        }]
		    });
		});
	 function rowformater(value,row,index)
	{
			return "<a href='"+row.NewsWebsite+"' target='_blank'>"+row.NewsWebsite+"</a>";
	}
	 
		$(function(){
			$('#pg').propertygrid({
				showHeader:false,
				 nowrap:false
			});
	});
		var mycolumns = [[
				    		{field:'name',title:'MyName',width:60,sortable:true},
				   		    {field:'value',title:'MyValue',width:20,resizable:false}
				        ]];
		
		
		
		
		$(function () {
		    $('#containerRight').highcharts({
		        chart: {
		            type: 'column'
		        },
		        title: {
		            text: 'Fine-grained sentiment expressed in new comments on the Event'
		        },
		        subtitle: {
		            text: ''
		        },
		        xAxis: {
		            categories: [
		                '17-Jul',
		                '18-Jul',
		                '19-Jul',
		                '20-Jul',
		                '21-Jul'],
		            crosshair: true
		        },
		        yAxis: {
		            min: 0,
		            title: {
		                text: 'Scaled Distribution'
		            }
		        },
		        tooltip: {
		            headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
		            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
		                '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
		            footerFormat: '</table>',
		            shared: true,
		            useHTML: true
		        },
		        plotOptions: {
		            column: {
		                pointPadding: 0.2,
		                borderWidth: 0
		            }
		        },
		        series: [{
		            name: 'angry',
		            data: [49.9, 71.5, 106.4, 129.2, 144.0]

		        }, {
		            name: 'scorn',
		            data: [83.6, 78.8, 98.5, 93.4, 106.0]

		        }, {
		            name: 'concern',
		            data: [48.9, 38.8, 39.3, 41.4, 47.0]

		        }, {
		            name: 'sympathy',
		            data: [42.4, 33.2, 34.5, 39.7, 52.6]

		        }, {
		            name: 'appall',
		            data: [42.4, 33.2, 34.5, 39.7, 52.6]

		        }]
		    });
		});
		
		function CheckSuccess(data)
		{
		 alert("Suc\t"+data);
		}
		
		function loadData(title)
		{
			///FrontWebProject
			//document.getElementById("summarydg").src="${pageContext.request.contextPath}/TableUpdate?ChoseStory="+title;
			//alert(document.getElementById("summarydg").src);
			
			var postUrl = "${pageContext.request.contextPath}/TableUpdate?ChoseStory="+title;//提交地址 

		    $.ajax({
		    	url:postUrl,
		    	datatype:"json",
		    	data:{"title":title},
		    	success: function(data){
		    		alert("Suceess!\n"+data);
					var jsonObj = eval("("+data+")");
			        $('#summarydg').datagrid('loadData',jsonObj);
		    	},
		    	error: function(XMLHttpRequest, textStatus, errorThrow){
		    		alert(XMLHttpRequest.status);
		    		alert(XMLHttpRequest.readyState);
		    		alert(textStatus);
		    		alert(errorThrown);
		    	},
		    	beforeSend: function(){
		    		alert("loading");
		    	}
		    	
		    })
	        //alert(jsonObj.NewsWebsite);
		}
 </script>

</head>
<body>
	<div class="easyui-layout" style="width:auto;height:650px;">

		<div class="easyui-accordion" data-options="multiple:true" region="west" split="true" style="width:200px;height:650px;">
		
		<div title="Basic Information" style="width:200px;" data-options="tools:'#p-tools'">

			<table id="pg" class="easyui-propertygrid" style="width:auto" data-options="
						url: 'json/information.json',
						method: 'get',
						showGroup: true,
						scrollbarSize: 0,
						columns: mycolumns
					">
			</table>
		</div>
		<div id="p-tools">
        <a href="javascript:void(0)" class="icon-mini-edit" onclick="$('#w').window('open')"></a>
        <a href="javascript:void(0)" class="icon-mini-refresh" onclick="alert('refresh')"></a>
   		 </div>
		
		<div title ="Tree" data-options="selected:true" style="width:200px;height:300px;padding:10px;">		
			<ul id = "tt" class= "easyui-tree" data-options="url:'json/tree.json',
			method:'get',animate:true,
			onClick: function(node){
                var title = node.text;
                loadData(title);
            }">
            </ul>
		</div>
		
	</div>
            
	<div region="east" split="true" style="width:440px;height:auto">
		<div class="easyui-tabs" style="width:auto;height:auto">
		<div title="Event Aftermath Analyzer" style="padding:5px"  align="center" >
			<div id="containerRight" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
			<p> Fine-grained sentiment expressed in news comments on the </br> 8/19 Event "Foley execution"</p>

				<table class="easyui-datagrid" title="Sentiment Dimensions" style="width:auto;height:auto" 
								data-options="autoRowHeight:true,fitColumns :true,singleSelect:true,collapsible:true,url:'json/SentimentDimensions.json',method:'get'">							
					<thead>
						<tr>
							<th data-options="field:'agree',width:90,align:'center'">agree</th>
							<th data-options="field:'angry',width:90,align:'center'">angry</th>
							<th data-options="field:'scorn',width:90,align:'center'">scorn</th>
							<th data-options="field:'concern',width:90,align:'center'">concern</th>
						</tr>
					</thead>
							
				</table>
				
				<table class="easyui-datagrid" title="Aspect Categories" "fit:true" style="width:auto;height:auto"
						data-options="fitColumns :true,singleSelect:true,collapsible:true,url:'json/AspectCategory.json',method:'get'">
					<thead>
						<tr>
							<th data-options="field:'execution',width:90,align:'center'">execution</th>
							<th data-options="field:'isis',width:90,align:'center'">isis</th>
							<th data-options="field:'US',width:90,align:'center'">US</th>
							<th data-options="field:'foley',width:90,align:'center'">foley</th>
						</tr>
					</thead>
				</table>
		</div>
	</div>
</div>
	<div id="content" region="center" style="width:400;padding:5px;" title="Story Sentiment Tracker " >
			<p>Dynamics between search traffic, news traffic and social sentiment on the ISIS story for the month of Aug, 2014.</p>
			
			<div id="container" style="min-width:700px;height:400px"></div>
			
			<table id="summarydg" src="${pageContext.request.contextPath}/TableUpdate" class="easyui-datagrid" title="News Source Summary" style="width:auto;height:auto"
			data-options="rownumbers:true,singleSelect:true,url:'json/summary.json',method:'get',toolbar:'#tb'">
				<thead>
					<tr>
						<th data-options="field:'NewsWebsite',width:140, formatter:rowformater">News Source</th>
						<th data-options="field:'day5',width:90,align:'center'">four days ago</th>
						<th data-options="field:'day4',width:90,align:'center'">three days ago</th>
						<th data-options="field:'day3',width:90,align:'center'">two days ago</th>
						<th data-options="field:'day2',width:90,align:'center'">one day ago</th>
						<th data-options="field:'day1',width:90,align:'center'">today</th>
						
					</tr>
				</thead>
			</table>

	<div id="tb" style="padding:5px;height:auto">
		<div>
			Date From: <input class="easyui-datebox" style="width:80px">
			To: <input class="easyui-datebox" style="width:80px">

			<a href="#" class="easyui-linkbutton" iconCls="icon-search">Search</a>
		</div>
		

	</div>
	
	
	
	</div>
	</div>

	
</body>
</html>