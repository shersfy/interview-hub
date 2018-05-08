function submitForm(){
	var url = $('#url').val();
	if(url == undefined || url.length==0){
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