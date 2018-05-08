function submitForm(){
	var url = $('#url').val();
	if(url == undefined || url.trim().length==0){
		$.messager.alert("提示", "URL不能为空白", "info");
		return;
	}
	$.messager.confirm('确认', '确认创建RMI连接吗?', function(ok){
		if (ok){
			process.watchProcess(url);
		}
	});
}

function clearForm(){
	$('#ff').form('clear');
}