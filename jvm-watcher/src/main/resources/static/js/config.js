function submitForm(){
	$.ajax({
		url: basePath+"/watch/config" ,//url
		data: $('#ff').serialize(),
		success: function (result) {
			if(result.code==200){
				$.messager.confirm('提示', "是否返回首页?", function(ok){
					if(ok){
						window.location.href = basePath;
					}
				});
			} else {
				$.messager.alert('错误', result.msg, 'error');
			}
		}
	});
}

function clearForm(){
	$('#ff').form('clear');
}