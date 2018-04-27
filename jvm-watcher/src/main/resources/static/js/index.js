var jvmChart = echarts.init(document.getElementById('jvmChart'));
var option = {
		title: {
			show: false,
			text: 'jwatcher_memory'
		},
		tooltip : {
			formatter: "{a} <br/>{b} : {c}%"
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
				name: 'JVM内存状态',
				type: 'gauge',
				detail: {formatter:'{value}%'},
				data: [{value: 50, name: 'Java进程已使用'}]
			}
			]
};

setInterval(function () {
	option.series[0].data[0].value = (Math.random() * 100).toFixed(2) - 0;
	jvmChart.setOption(option, true);
}, 2000);

jvmChart.setOption(option);