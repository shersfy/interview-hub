$(function(){
	capacity.initMemoChart();
	capacity.initCPUChart();
})

var interval = 2000; // 数据刷新频率
var capacity = {
		// memory
		initMemoChart: function(){
			var initMemo  = JSON.parse($('#memoChart').attr('memo'));
			var memoChart = echarts.init(document.getElementById('memoChart'));
			var option = {
					title: {
						text: '物理内存',
						subtext: initMemo.totalStr
					},
					tooltip : {
						formatter: "{b} <br/>{a} : {c}%"
					},
					toolbox: {
						feature: {
							saveAsImage: {
								show: false,
								type: 'jpg'
							}
						},
						top : 'bottom',
						itemGap: 20
					},
					series: [
						{
							name: '已使用',
							type: 'gauge',
							detail: {formatter:'{value}%'},
							data: [{value: parseFloat(initMemo.usedPercent).toFixed(2), name: '物理内存'}]
						}
						]
			};

			setInterval(function () {
				$.ajax({
					url: 'memo',
					success:function(data){
						if (data.code != 200 ){
							alert("请求错误: "+url);
							return ;
						}

						option.tooltip.formatter = "{b} "+data.model.totalStr+"<br/>{a} "+data.model.usedStr+": {c}%";
						option.series[0].data[0].value = data.model.usedPercent.toFixed(2)-0;
						memoChart.setOption(option, true);
					}
				});
			}, interval);

			memoChart.setOption(option);
		},
		// CPU
		initCPUChart:function(){
			var initCpu  = JSON.parse($('#cpuChart').attr('cpu'));
			var elements = new Array('nice', 'user', 'system', 'wait', 'idle');
			var cpuChart = echarts.init(document.getElementById('cpuChart'));
			var option   = {
					title : {
						text: 'CPU使用率',
						subtext: initCpu.name
					},
					tooltip : {
						trigger: 'item',
						//formatter: "{a} <br/>{b} : {c} ({d}%)"
						formatter: "{a} <br/>{b}: {d}%"
					},
					legend: {
						orient: 'vertical',
						left: 'right',
						data: elements
					},
					series : [
						{
							name: '使用率',
							type: 'pie',
							radius : '55%',
							center: ['50%', '50%'],
							data:[
								{value:initCpu.nice.toFixed(2), name: elements[0]},
								{value:initCpu.user.toFixed(2), name: elements[1]},
								{value:initCpu.system.toFixed(2), name: elements[2]},
								{value:initCpu.wait.toFixed(2), name: elements[3]},
								{value:initCpu.idle.toFixed(2), name: elements[4]}
								],
								itemStyle: {
									emphasis: {
										shadowBlur: 10,
										shadowOffsetX: 0,
										shadowColor: 'rgba(0, 0, 0, 0.5)'
									}
								}
						}
						]
			};

			setInterval(function () {
				$.ajax({
					url: 'cpu',
					success:function(data){
						if (data.code != 200 ){
							alert("请求错误: "+url);
							return ;
						}

						option.series[0].data[0].value = data.model.nice.toFixed(2);
						option.series[0].data[1].value = data.model.user.toFixed(2);
						option.series[0].data[2].value = data.model.system.toFixed(2);
						option.series[0].data[3].value = data.model.wait.toFixed(2);
						option.series[0].data[4].value = data.model.idle.toFixed(2);
						cpuChart.setOption(option, true);
					}
				});
			}, interval);

			cpuChart.setOption(option);
		}

}
