$(document).ready( function () {
	$('#process').DataTable({
		"info": false,
		"ordering": false,
		"lengthChange": false,
		"paging": false
	});
	process.initProcess();

} );

var process = {
		initProcess: function(){
			$('.jvm-process').each(function(){
				$(this).click(function(){
					var pid = $(this).text();
					$.messager.confirm('确认', '确认创建连接吗?', function(ok){
						if (ok){
							process.watchProcess(pid);
						}
					});
				});
			});
		},
		watchProcess: function(pid){
			window.open(basePath + "/process/local/open/"+pid);
		}
}
