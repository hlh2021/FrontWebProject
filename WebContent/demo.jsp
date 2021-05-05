<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Filter Demo</title>

<style>
.with-background {
    background-image: url("images/bg.jpg");
    color:white;
    font-style: italic;
    margin: 0;
}
</style>

</head>
<body>

<div style="width:21%;height:auto">

<h3 class="with-background">Subject:</h3>

<div style="padding: 10px 0 10px 0;">	
	<div style="width:42%;float:left">
		<label>Variables</label>		
	</div>
	
	<div style="width:16%;float:left">
		<label>Operator</label>
	</div>
	
	<div style="width:21%;float:left">
		<label>Value</label>
	</div>
	
	<div style="width:14%;float:left">
		<label>Remove</label>
	</div>
	
	<div style="width:7%;float:left;">
		<label>Add</label>
	</div>
	
</div>


<div style="padding: 15px 0 10px 0;">	
	<div class="variablesDiv" style="width:42%;float:left">		
		<select name="variablesList" id="variablesList">
			<option value="age">Age</option>
			<option value="gender">Gender</option>
			<option value="occupation">Occupation</option>
			<option value="mental">Mental health conditions</option>
			<option value="smoking">Smoking</option>
		</select>
	</div>
	
	<div class="operatorDiv" style="width:16%;float:left">
		<select name="operatorList" id="operatorList">
			<option value="equal">=</option>
			<option value="no_less_than">>=</option>
			<option value="no_more_than"><=</option>
		</select>
	</div>
	
	<div class="valueDiv" style="width:21%;float:left">
		<input type="text" id="valueTyped" name="valueTyped" size=5>
	</div>
	
	<div class="removeDiv" style="width:14%;float:left">
		<input type="button" value="X" name="removeButton" onclick="">
	</div>
	
	<div class="addDiv" style="width:7%;float:left;visibility:hidden;">
		<input type="button" value="+" name="addButton" onclick="">
	</div>
	
</div>

<div style="padding: 15px 0 10px 0;">	
	<div class="variablesDiv" style="width:42%;float:left">		
		<select name="variablesList" id="variablesList">
			<option value="age">Age</option>
			<option value="gender">Gender</option>
			<option value="occupation">Occupation</option>
			<option value="mental">Mental health conditions</option>
			<option value="smoking">Smoking</option>
		</select>
	</div>
	
	<div class="operatorDiv" style="width:16%;float:left">
		<select name="operatorList" id="operatorList">
			<option value="equal">=</option>
			<option value="no_less_than">>=</option>
			<option value="no_more_than"><=</option>
		</select>
	</div>
	
	<div class="valueDiv" style="width:21%;float:left">
		<select name="valueTyped">
			<option value="male">Male</option>
			<option value="female">Female</option>
		</select>
	</div>
	
	<div class="removeDiv" style="width:14%;float:left">
		<input type="button" value="X" name="removeButton" onclick="">
	</div>
	
	<div class="addDiv" style="width:7%;float:left;">
		<input type="button" value="+" name="addButton" onclick="">
	</div>
	
</div>

<div style="visibility:hidden;">	
	<div id="variablesDiv" style="width:42%;float:left">
		<p>
		<label for="variablesList">Variables</label>
		</p>
		<p>
		<select name="variablesList" id="variablesList">
			<option value="age">Age</option>
			<option value="gender">Gender</option>
			<option value="occupation">Occupation</option>
			<option value="mental">Mental health conditions</option>
			<option value="smoking">Smoking</option>
		</select>
		</p>
	</div>
	
	<div id="operatorDiv" style="width:16%;float:left">
		<p>
		<label for="operatorList">Operator</label>
		</p>
		<p>
		<select name="operatorList" id="operatorList">
			<option value="equal">=</option>
			<option value="no_less_than">>=</option>
			<option value="no_more_than"><=</option>
		</select>
		</p>
	</div>
	
	<div id="valueDiv" style="width:21%;float:left">
		<p>
		<label for="valueTyped">Choose Value</label>
		</p>
		<p>
		<select name="valueTyped">
			<option value="male">Male</option>
			<option value="female">Female</option>
		</select>
		</p>
	</div>
	
	<div id="removeDiv" style="width:14%;float:left">
		<p>
		<label for="removeButton">Remove</label>
		</p>
		<p>
		<input type="button" value="X" name="removeButton" onclick="">
		</p>
	</div>
	
	<div id="addDiv" style="width:7%;float:left;">
		<p>
		<label for="addButton">Add</label>
		</p>
		<p>
		<input type="button" value="+" name="addButton" onclick="">
		</p>
	</div>
	
</div>

<div style="width:15%;float:right;margin: 30px;visibility:hidden;">
	<input type="button" value="+Variable" onclick="">
</div>	

</div>

</body>
</html>