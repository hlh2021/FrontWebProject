<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>The Crawler Main Page</title>
	<link rel="stylesheet" type="text/css" href="EasyUI/themes/gray/easyui.css">
	<link rel="stylesheet" type="text/css" href="EasyUI/themes/icon.css">
	<script type="text/javascript" src="EasyUI/jquery.min.js"></script>
	<script type="text/javascript" src="EasyUI/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="Highcharts/js/highcharts.js"></script>
	<script type="text/javascript" src="Highcharts/js/modules/exporting.js"></script>
    
</head>

<script type="text/javascript">
	 

	  $(function () {
		
		var options_c = {
				title: {
		            text: 'Correlation Number in This Years',
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
		            data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
		        }]
		};
		    		
		$('#containerRight').highcharts(options_c);
		
		var options_cr = {
				title: {
		            text: 'Total Comments Number and Polarity Number',
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
		            name: 'Overall',
		            data: [0, 0, 0, 0, 0, 0, 0]
		        }, 
		        {
		            name: 'Positive',
		            data: [0, 0, 0, 0, 0, 0, 0]
		        },
		        {
		            name: 'Negative',
		            data: [0, 0, 0, 0, 0, 0, 0]
		        }]
		};
		    		
		$('#container').highcharts(options_cr);
		
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
		/****
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
		***/
		
		function CheckSuccess(data)
		{
		 	alert("Suc\t"+data);
		}
		
		function loadData(title)
		{
			///FrontWebProject
			//document.getElementById("summarydg").src="${pageContext.request.contextPath}/TableUpdate?ChoseStory="+title;
			//alert(document.getElementById("summarydg").src);
			
			var postUrl = "${pageContext.request.contextPath}/TableUpdate?ChoseStory="+title;

		    $.ajax({
		    	url:postUrl,
		    	datatype:"json",
		    	data:{"title":title},
		    	success: function(data){
		    		
		    		var strs = new Array();
		    		strs = data.split(";");
		    		var str_dg = strs[0];
		    		var jsonObj = eval("("+str_dg+")");
			        $('#summarydg').datagrid('loadData',jsonObj);
		    		
			        //refresh the chart in center
			        var options = {
						title: {
            				text: 'Correlation Number in This Years',
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
		    		//alert(datestr);
		    		//alert(vaulestr);
		    		//alert(count);	//=10	      			        					
			        $('#containerRight').highcharts(options);
		    		
			      //refresh the chart in right
			        var options_cr = {
			        		title: {
					            text: 'Total Comments Number and Polarity Number',
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
					            name: 'Overall',
					            data: []
					        }, 
					        {
					            name: 'Positive',
					            data: []
					        },
					        {
					            name: 'Negative',
					            data: []
					        }]
					};
			        var str_cr = strs[2];			       
			        var json_cr = eval("("+str_cr+")");  //parse string to json object
			        for(var i=0;i<json_cr.length;i++){
			        	//alert(json_cr[i].date+" "+json_cr[i].totalNumber+" "+json_cr[i].proportion);
			        	options_cr.xAxis.categories.push(json_cr[i].date);
			        	//options_cr.series[0].data.push(i+1);
			        	//options_cr.series[1].data.push(i/10);
			        	options_cr.series[0].data.push(parseFloat(json_cr[i].totalNumber));
			        	options_cr.series[1].data.push(parseFloat(json_cr[i].posiNumber));
			        	options_cr.series[2].data.push(parseFloat(json_cr[i].negaNumber));
			        }

			     /** 
			      for(var i=0;i<5;i++){
			    	  options_cr.xAxis.categories.push(i+" day ago");
	    			  options_cr.series[0].data.push(i+10);
	    			  options_cr.series[1].data.push(i/10);
			      }**/
					    		
					$('#container').highcharts(options_cr);
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
	        //alert(jsonObj.NewsWebsite);
		}
</script>


<body>
    <div class="easyui-tabs" style="width:auto;height:auto">
        <div title="Home" style="padding:10px" >
        
        
        <div class="easyui-layout" style="width:auto;height:1000px;">
       
       	<div class="timechose" region="north" style="width:auto">
       	
       		<div id= "timepanel"  style="float:left;40%">
       		<table>
		        <tr>
		       		 <script>œŸ
		        formatterDate = function(date) {
		        	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
		        	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
		        	return date.getFullYear() + '-' + month + '-' + day;
		        };
		        window.onload = function () {
		        		$('#nowtime').datebox('setValue', formatterDate(new Date()));
		        };
		        	</script>
		        	
		            <td>Start Date:</td>
		            <td>
		                <input class="easyui-datebox" data-options="sharedCalendar:'#cc'" style="float:left;">
		            </td>
		            <td>End Date:</td>
		            <td>
		                <input id="nowtime" class="easyui-datebox" data-options="sharedCalendar:'#cc'" style="float:left;">
		            </td>
		        </tr>
		    </table>
       		<div id="cc" class="easyui-calendar"></div>
       		</div>
       	
       	    <script>
        $(function(){
            $('#pp').pagination().find('a.l-btn').tooltip({
                content: function(){
                    var cc = $(this).find('span.l-btn-icon').attr('class').split(' ');
                    var icon = cc[1].split('-')[1];
                    return icon + ' week';
                }
            });
        });
    		</script>
    
       	 	<div id="btGo" style="float:left;20%"><a href="javascript:void(0)" class="easyui-linkbutton" onclick="GoDate()">Go</a></div>
        	<div id="pp" class="easyui-pagination" data-options="layout:['first','prev','next','last']" style="float:left;40%" ></div>
	
    	</div>

		<div class="easyui-accordion" data-options="multiple:true" region="west" split="true" style="width:200px;height:auto;">
		
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
		
			<div title ="Tree" data-options="selected:true" style="width:200px;padding:10px;">		
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
				<div title="Event Tracker" style="padding:5px"  align="center" >
				
					<div id="containerRight" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

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

		<div id="content" region="center" style="width:400;padding:5px;" title="Story Comment Calculation" >
			
			<div id="container" style="min-width:700px;height:400px"></div>
			<p align="center"> comments number and polarity number</p>
			
			<table id="summarydg" src="${pageContext.request.contextPath}/TableUpdate" class="easyui-datagrid" title="News Source Summary" style="width:100%;height:auto"
			data-options="rownumbers:true,singleSelect:true,url:'json/summary.json',method:'get',toolbar:'#tb'">
				<thead>
					<tr>
						<th data-options="field:'NewsWebsite',width:140, formatter:rowformater">News Source</th>
						<th data-options="field:'day5',width:85,align:'center'">four days ago and earlier</th>
						<th data-options="field:'day4',width:85,align:'center'">three days ago</th>
						<th data-options="field:'day3',width:85,align:'center'">two days ago</th>
						<th data-options="field:'day2',width:85,align:'center'">one day ago</th>
						<th data-options="field:'day1',width:85,align:'center'">today</th>
						<th data-options="field:'sum',width:85,align:'center'">sum</th>
						
					</tr>
				</thead>
			</table>
		</div>
		</div>
   		</div>
   		
   		
        <div title="Team" style="padding:20px" data-options="region:'center'">
        	<div id = 'Author' style="float:left;width:100%">
					<div id="single-gem-author">
					<div class=Headportrait" style="float:left;padding:20px">
					<img  alt='' src='https://lh3.googleusercontent.com/-KwsR_RuODXc/AAAAAAAAAAI/AAAAAAAAAWM/ogBWF6DOuGw/s180-c-k-no/photo.jpg' srcset='https://lh3.googleusercontent.com/-KwsR_RuODXc/AAAAAAAAAAI/AAAAAAAAAWM/ogBWF6DOuGw/s180-c-k-no/photo.jpg' class='avatar avatar-96 photo' height='96' width='96' />					
					</div>
					<div class="gem-author-info" style="float:left;padding:20px">
							<h3>Qingyuan Liu</h3>
							<p>Computer and Information Sciences Temple University </p>
					</div> <!-- gem-author-info -->	
					
					</div> <!-- single-gem-author -->
			</div>
        	<div id = 'Author2' style="float:left;width:100%">
					<div id="single-gem-author">
					<div class=Headportrait" style="float:left;padding:20px">
					<img  alt='' src='http://www.cis.temple.edu/~edragut/index_files/eddyfoto.jpg' srcset='http://www.cis.temple.edu/~edragut/index_files/eddyfoto.jpg' class='avatar avatar-96 photo' height='96' width='96' />					
					</div>
					<div class="gem-author-info" style="float:left;padding:20px">
							<h3><a href="http://www.cis.temple.edu/~edragut/">Eduard C. Dragut</a></h3>
							<p>Computer and Information Sciences Temple University </p>
					</div> <!-- gem-author-info -->	
					
					</div> <!-- single-gem-author -->
				
			</div>
				

				<div id = 'Author3' style="float:left;width:100%">
					<div id="single-gem-author">
					<div class=Headportrait" style="float:left;padding:20px">
					<img  alt='' src='http://www.afaqs.com/all/news/images/news_story_grfx/2012/08/35163/Arjun-Mukherjee.jpg' srcset='http://www.afaqs.com/all/news/images/news_story_grfx/2012/08/35163/Arjun-Mukherjee.jpg' class='avatar avatar-96 photo' height='96' width='96' />					
					</div>
					<div class="gem-author-info" style="float:left;padding:20px">
							<h3><a href="http://www2.cs.uh.edu/~arjun/">Arjun Mukherjee</a></h3>
							<p>Computer Science Department University of Houston</p>
					</div> <!-- gem-author-info -->	
					
					</div> <!-- single-gem-author -->
				</div>
				
				<div id = 'Author4' style="float:left;width:100%">
					<div id="single-gem-author">
					<div class=Headportrait" style="float:left;padding:20px">
					<img  alt='' src='http://www.cs.binghamton.edu/~meng/image.d/meng-picture-75.jpg' srcset='http://www.cs.binghamton.edu/~meng/image.d/meng-picture-75.jpg' class='avatar avatar-96 photo' height='96' width='96' />					
					</div>
					<div class="gem-author-info" style="float:left;padding:20px">
							<h3><a href="http://www.cs.binghamton.edu/~meng/">Weiyi Meng</a></h3>
							<p>Computer Science Department Binghamton University</p>
					</div> <!-- gem-author-info -->	
					
					</div> <!-- single-gem-author -->
				</div>
				
				<div id = 'Author5' style="float:left;width:100%">
					<div id="single-gem-author">
					<div class=Headportrait" style="float:left;padding:20px">
					<img  alt='' src='images/hong.jpg' srcset='images/hong.jpg' class='avatar avatar-96 photo' height='96' width='96' />					
					</div>
					<div class="gem-author-info" style="float:left;padding:20px">
							<h3>Lihong He</h3>
							<p>Computer and Information Sciences Temple University </p>
					</div> <!-- gem-author-info -->	
					
					</div> <!-- single-gem-author -->
				</div>
				
        </div>
        
        <div title="About" style="padding:10px">
            We propose a system, FLORIN, which provides sup-port for near real-time applications on user generated content on daily news. FLORIN continuously crawls news outlets for articles and user comments accompanying them. It attaches the articles and comments to daily event stories. It identifies the opinionated con-tent in user comments and performs named entity recognition on news articles. All these pieces of information are organized hierar-chically and exportable to other applications. Multiple applications can be built on this data.We have implemented a sentiment analysis system that runs on top of it.
        </div>
    </div>

</body>
</html>